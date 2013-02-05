/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bn.elicitator

/**
 * This may not be the best name for the service, but I'd like to have all of the stuff to do with comparing results
 * from different rounds and giving feedback in here.
 */
class DelphiService {

	def userService

	/**
	 * Returns true if the current delphi phase is not the first.
	 * @return
	 */
	def getHasPreviousPhase() {
		return AppProperties.properties.delphiPhase > 1
	}

	/**
	 * Alias to {@link AppProperties#getDelphiPhase()}
	 * @return
	 */
	int getPhase() {
		return AppProperties.properties.delphiPhase
	}

	/**
	 * Returns the number of the last delphi phase.
	 * @return
	 * @throws Exception If there is no previous phase, then we throw an exception. Therefore, check 'hasPreviousPhase' first.
	 * @see DelphiService#getHasPreviousPhase()
	 */
	int getPreviousPhase() throws Exception {
		if ( !hasPreviousPhase )
		{
			throw new Exception( "No previous phases (current phase is ${phase})" )
		}
		return AppProperties.properties.delphiPhase - 1
	}

	/**
	 * For two given variables, give me the relationship that the current user created between them last phase (or null
	 * if none).
	 * @param parent
	 * @param child
	 * @return
	 */
	def getMyPreviousRelationship( Variable parent, Variable child ) {
		return this.hasPreviousPhase ? Relationship.findByCreatedByAndDelphiPhaseAndParentAndChild( ShiroUser.current, previousPhase, parent, child ) : null
	}

	/**
	 * For two given variables, give me the relationship that the current user created between them for the current
	 * phase (or null if none yet).
	 * @param parent
	 * @param child
	 * @return
	 */
	def getMyCurrentRelationship( Variable parent, Variable child ) {
		return Relationship.findByCreatedByAndDelphiPhaseAndParentAndChild( ShiroUser.current, phase, parent, child )
	}

	/**
	 * For two given variables, give me the relationships that all other users other than the current one created
	 * between them.
	 * @param parent
	 * @param child
	 * @return
	 */
	def getOthersPreviousRelationships( Variable parent, Variable child ) {
		return hasPreviousPhase ? Relationship.findAllByCreatedByNotEqualAndDelphiPhaseAndParentAndChild( ShiroUser.current, previousPhase, parent, child ) : []
	}

	/**
	 * Both mine and others previous, as well as my current relationships.
	 * @param parent
	 * @param child
	 * @return
	 */
	def getAllPreviousRelationshipsAndMyCurrent( Variable parent, Variable child ) {
		List<Relationship> relationships = []
		if ( hasPreviousPhase )
		{
			relationships = Relationship.findAllByDelphiPhaseAndParentAndChild( previousPhase, parent, child )
			Relationship current = this.getMyCurrentRelationship( parent, child )
			if ( current )
			{
				relationships.add( 0, current )
			}
		}

		return relationships
	}

	/**
	 * Calculate the number of users who have completed the round specified by 'delphiPhase'.
	 * These are people who have explicitly stated that they have completed.
	 * @param delphiPhase
	 * @return
	 */
	Integer completedUsers( Integer delphiPhase = phase )
	{
		// TODO: Implement this...
	}

	/**
	 * Build a list of variables which have not yet been visited.
	 * @param varList
	 * @return
	 */
	List<Variable> getStillToVisit( List<Variable> varList )
	{
		List<Variable> visitedList = VisitedVariable.findAllByVariableInListAndVisitedByAndDelphiPhase( varList, ShiroUser.current, phase )*.variable
		List<Variable> stillToVisit = varList.findAll { !visitedList.contains( it ) }
		return stillToVisit
	}

	/**
	 * @param parent
	 * @param child
	 * @return
	 * @see #getOthersPreviousRelationships(bn.elicitator.Variable, bn.elicitator.Variable)
	 */
	def countOthersPreviousRelationships( Variable parent, Variable child ) {
		return Relationship.countByCreatedByNotEqualAndDelphiPhaseAndParentAndChild( ShiroUser.current, previousPhase, parent, child )
	}

	/**
	 * Same as other, but will always compare last times relationships to each other.
	 * @param parent
	 * @param child
	 * @return
	 * @see DelphiService#calcAgreement(bn.elicitator.Variable, bn.elicitator.Variable, bn.elicitator.Relationship)
	 */
	Agreement calcAgreement( Variable parent, Variable child ) {

		Relationship relationship = this.getMyCurrentRelationship( parent, child )

		if ( !relationship )
		{
			relationship = this.getMyPreviousRelationship( parent, child )
		}

		return this.calcAgreement( parent, child, relationship )

	}

	/**
	 * Same as other version, but allows us to do a comparison with any relationship, not just with last rounds...
	 * @param parent
	 * @param child
	 * @param myRelationship
	 * @return
	 * @see DelphiService#calcAgreement(bn.elicitator.Variable, bn.elicitator.Variable)
	 */
	Agreement calcAgreement( Variable parent, Variable child, Relationship myRelationship ) {

		List<Relationship> othersRelationships = this.getOthersPreviousRelationships( parent, child )
		Boolean isCurrent = ( myRelationship?.delphiPhase == phase )

		Agreement agreement = new Agreement(
			parent: parent,
			child: child,
			myRelationship: myRelationship,
			othersRelationships: othersRelationships,
			current: isCurrent
		)

		if ( myRelationship?.exists && myRelationship?.confidence != null )
		{
			agreement.myConfidence = myRelationship.confidence
		}

		if ( agreement.othersRelationshipsWhichExist.size() > 0 )
		{
			agreement.othersConfidence = (Double)agreement.othersRelationshipsWhichExist*.confidence.sum() / agreement.othersRelationshipsWhichExist.size()
			agreement.othersCount = agreement.othersRelationshipsWhichExist.size()
		}

        // Whether or not you agree with others is a function of both how many other people state the relationship exists,
        // along with how confident they are...
		float score = calcConsensusScore( agreement )
		boolean byMe = ( agreement.specifiedBy & Agreement.BY_ME ) == Agreement.BY_ME
		agreement.agree = ( byMe && score > 0.6f || !byMe && score < 0.4f )

		return agreement
	}

	/**
	 * A function of both what percentage of participants specified a relationship, and their average confidence.
	 *  - If everyone is 100% certain, then we have a score of 1.0
	 *  - If nobody thinks the relationship exists, we have a score of 0.0
	 * At this time, we will simply sum the two values together, and then divide by two.
	 *
	 * <pre>
	 *        all
	 *         |
	 *  # of   |*     agree
	 *  others |   *
	 *   (y)   |      *
	 *         | disagree *
	 *         +-------------- 100%
	            confidence (x)
	 * </pre>
	 * @param agreement
	 * @return
	 */
	float calcConsensusScore( Agreement agreement )
	{
		float x = agreement.othersConfidence / 100
		float y = (float)agreement.othersCount / userService.expertCount

		return ( x + y * 2 ) / 3
	}

	/**
	 * Search for the {@link CompletedPhase} object which represents the current user for the current phase.
	 * @return
	 */
	CompletedPhase getCompleted() {
		return CompletedPhase.findByCompletedByAndDelphiPhase( ShiroUser.current, phase )
	}

	/**
	 * Get a list of all {@link CompletedPhase}s for the entire round.
	 * @return
	 */
	def getCompletedCurrentRound() {
		return CompletedPhase.findAllByDelphiPhase( phase )
	}
}

