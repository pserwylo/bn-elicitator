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

import bn.elicitator.auth.User

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
		getPreviousRelationshipFor( userService.current, parent, child )
	}

	def getPreviousRelationshipFor( User user, Variable parent, Variable child ) {
		return this.hasPreviousPhase ? Relationship.findByCreatedByAndDelphiPhaseAndParentAndChild( user, previousPhase, parent, child ) : null
	}

	/**
	 * For two given variables, give me the relationship that the current user created between them for the current
	 * phase (or null if none yet).
	 * @param parent
	 * @param child
	 * @return
	 */
	def getMyCurrentRelationship( Variable parent, Variable child ) {
		return getCurrentRelationshipFor( userService.current, parent, child )
	}

	def getCurrentRelationshipFor( User user, Variable parent, Variable child ) {
		return Relationship.findByCreatedByAndDelphiPhaseAndParentAndChild( user, phase, parent, child )
	}

	/**
	 * For two given variables, give me the relationships that all other users other than the current one created
	 * between them.
	 * @param parent
	 * @param child
	 * @return
	 */
	def getOthersPreviousRelationships( Variable parent, Variable child ) {
		return hasPreviousPhase ? Relationship.findAllByCreatedByNotEqualAndDelphiPhaseAndParentAndChild( userService.current, previousPhase, parent, child ) : []
	}

	/**
	 * Both mine and others previous, as well as my current relationships.
	 * @param parent
	 * @param child
	 * @return
	 */
	def getAllPreviousRelationshipsAndMyCurrent( Variable parent, Variable child, Boolean justLastRound = true ) {
		List<Relationship> relationships = []
		if ( hasPreviousPhase ) {
			if ( justLastRound ) {
				relationships = Relationship.findAllByDelphiPhaseAndParentAndChild( previousPhase, parent, child )
			} else {
				relationships = Relationship.findAllByDelphiPhaseLessThanEqualsAndParentAndChild( previousPhase, parent, child ).sort { early, late -> late.delphiPhase <=> early.delphiPhase }
			}
			Relationship current = this.getMyCurrentRelationship( parent, child )
			if ( current ) {
				relationships.add( 0, current )
			}
		}

		return relationships
	}

	/**
	 * @param parent
	 * @param child
	 * @return
	 * @see #getOthersPreviousRelationships(bn.elicitator.Variable, bn.elicitator.Variable)
	 */
	def countOthersPreviousRelationships( Variable parent, Variable child ) {
		return Relationship.countByCreatedByNotEqualAndDelphiPhaseAndParentAndChild( userService.current, previousPhase, parent, child )
	}

	/**
	 * Search for the {@link CompletedPhase} object which represents the current user for the current phase.
	 * @return
	 */
	CompletedPhase getCompleted() {
		return CompletedPhase.findByCompletedByAndDelphiPhase( userService.current, phase )
	}

	/**
	 * Get a list of all {@link CompletedPhase}s for the entire round.
	 * @return
	 */
	def getCompletedCurrentRound() {
		return CompletedPhase.findAllByDelphiPhase( phase )
	}
}

