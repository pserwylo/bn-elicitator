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
import bn.elicitator.events.FinishedVariableEvent
import bn.elicitator.init.DumbProfiler

class VariableService 
{

	DelphiService delphiService
	UserService   userService

	public List<ReviewedRelationship> myReviewedRelationshipsFor( Variable child ) {
		return ReviewedRelationship.withCriteria {
			relationship {
				eq( 'child', child )
			}
			eq( 'reviewedBy',  userService.current )
			eq( 'delphiPhase', delphiService.phase )
		}
	}

	/**
	 * Number of variables completed by user *user* (defaults to current user).
	 */
	public int completedCount( User user = userService.current ) {
		VisitedVariable.countByVisitedByAndDelphiPhase( user, delphiService.phase )
	}

	/**
	 * Number of people who have completed variable *var*.
	 */
	public int completedCount( Variable var ) {
		VisitedVariable.countByVariable( var )
	}

	public void eachVariableClass( Closure closure ) {

		Map<Long,List<Variable>> categories = [:]

		List<VariableClass> classes = VariableClass.list();
		List<Variable> variables    = Variable.list();

		classes.each { categories.put( it.id, [] ) }

		variables.each { var ->
			categories[ var.variableClass.id ].add( var )
		}

		classes.each { VariableClass varClass ->

			List<Variable> potentialParents = []
			varClass.potentialParents.each { VariableClass parentClass ->
				potentialParents.addAll( categories[ parentClass.id ] )
			}

			closure( varClass, categories[ varClass.id ], potentialParents )
		}
	}

	/**
	 * Recursively searches backwards from the 'toSearch' variable, and stops when all of its parents have already been
	 * checked, or when there are no parents. It can't go forever, because we have enforced an acyclic graph through
	 * constraints on possible parents.
	 * @param toSearch
	 * @param toElicit
	 * @param hasCheckedParents
	 */
	private void findParentsToElicit( Variable toSearch, List<Variable> toElicit, Set<Variable> hasCheckedParents )
	{
		toElicit.add( toSearch )
		hasCheckedParents.add( toSearch )

		List<Integer> phasesToSearch = [ delphiService.phase ]
		if ( delphiService.hasPreviousPhase )
		{
			phasesToSearch.add( delphiService.phase - 1 );
		}
		List<Relationship> problemRelationships = Relationship.findAllByChildAndCreatedByAndDelphiPhaseInListAndParentIsNotNull( toSearch, userService.current, phasesToSearch )
		List<Variable> parents = problemRelationships*.parent
		parents.each
		{
			if ( !toElicit.contains( it ) )
			{
				findParentsToElicit( it, toElicit, hasCheckedParents )
			}
		}
	}

	public boolean hasVisitedLastRound( Variable child, User user = userService.current ) {
		VisitedVariable.countByDelphiPhaseAndVisitedByAndVariable( delphiService.previousPhase, user, child ) > 0
	}

	/**
	 * Build a list of variables which have not yet been visited.
	 * @param varList
	 * @return
	 */
	List<Variable> getStillToVisit() {
		List<Variable> varList      = getAllChildVars()
		List<Variable> visitedList  = VisitedVariable.findAllByVariableInListAndVisitedByAndDelphiPhase( varList, userService.current, delphiService.phase )*.variable
		List<Variable> stillToVisit = varList.findAll { !visitedList.contains( it ) }
		return stillToVisit
	}

	/**
	 * Get all variables for which we want to elicit parents for.
	 * @return
	 */
	public List<Variable> getAllChildVars() {
		Allocation allocation = Allocation.findByUser( userService.current )
		List<Variable> vars;
		if ( delphiService.hasPreviousPhase ) {
			vars = allocation.variables.findAll { hasVisitedLastRound( it ) }.toList()
		} else {
			vars = allocation.variables
		}
		vars.sort( new VariableSorter() )
	}

	class VariableSorter implements Comparator<Variable> {
		@Override
		int compare( Variable first, Variable second ) {
			first.readableLabel.compareTo( second.readableLabel )
		}
	}

	/**
	 * Recursively travers upwards from children, looking for parents which are related.
	 * @param children
	 * @param toExclude
	 * @return
	 */
	private List<Variable> findAllRelatedVars( List<Variable> children, List<Variable> toExclude ) {
		List<Relationship> relationships = Relationship.findAllByDelphiPhaseAndCreatedByAndChildInListAndExists( delphiService.phase, userService.current, children, true )
		relationships = relationships.findAll { !toExclude.contains( it ) }

		// Hacky way to filter out non-unique items...
		List<Variable> parents = relationships*.parent.toSet().toList()
		List<Variable> notYetChecked = parents.findAll { !children.contains( it ) && !toExclude.contains( it ) }

		if ( notYetChecked.size() > 0 ) {
			List<Variable> checked = []
			checked.addAll( children )
			checked.addAll( toExclude )

			List<Variable> others = findAllRelatedVars( notYetChecked, checked )
			parents.addAll( others )
		}

		return parents
	}

	/**
	 * Search through the variables which can potentially be parents of 'child'.
	 * These variables are those which are in groups that are above the current group, and possible variables in the current group
	 * if the withinGroup property is true.
	 * @return List of variables sorted by name.
	 */
	public List<Variable> getPotentialParents( Variable child ) {
		Variable.findAllByVariableClassInList( child.variableClass.potentialParents )
	}

	/**
	 * If the variable has been visited by the current user, then the VisitedVariable corresponding to this visit will
	 * be returned (for the current delphi phase and the current user). Otherwise it will return null.
	 * @param variable
	 * @return
	 */
	VisitedVariable getVisitedVariable( Variable variable ) {
		VisitedVariable.findByVariableAndVisitedByAndDelphiPhase( variable, userService.current, AppProperties.properties.delphiPhase )
	}

	/**
	 * @see VariableService#createRelationships(bn.elicitator.Variable, java.util.List)
	 */
	void initRelationships() {
		Integer count = Relationship.countByCreatedByAndDelphiPhase( userService.current, this.delphiService.phase )
		if ( count == 0 ) {
			Allocation allocation = Allocation.findByUser( userService.current )
			eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialParents ->
				allocation.variables.each { child ->
					if ( varsInClass.contains( child ) ) {
						this.createRelationships( child, potentialParents )
					}
				}
			}
		}
	}

	/**
	 * Ensures that 'variable' has valid relationship objects (for the current user and delphi phase). There may already
	 * be relationships, in which case we ignore that. However, if there isn't, then we create them here, and set
	 * {@link Relationship#exists} to false.
	 * @param child
	 * @param potentialParents Just to reduce the amount of work (if we already have a reference to it, just pass it in,
	 * otherwise we'll get it ourselves here).
	 */
	void createRelationships( Variable child, List<Variable> potentialParents = null ) {

		if ( potentialParents == null )
		{
			potentialParents = this.getPotentialParents( child )
		}

		for( Variable parent in potentialParents )
		{
			Relationship relationship = this.delphiService.getMyCurrentRelationship( parent, child )
			if ( !relationship )
			{
				Relationship oldRelationship = this.delphiService.getMyPreviousRelationship( parent, child );

				new Relationship(
					child:       child,
					parent:      parent,
					createdBy:   userService.current,
					delphiPhase: AppProperties.properties.delphiPhase,
					exists:      oldRelationship ? oldRelationship.exists : false,
				).save( failOnError: true )
			}
		}
	}

	/**
	 * Mark a variable as visited, so that we can check if off the list and provide feedback to the user (and admin)
	 * about what variables are left to visit.
	 * @param variable
	 */
	void finishVariable( Variable variable ) {
		VisitedVariable visited = getVisitedVariable( variable )
		if ( visited == null ) {
			new VisitedVariable( variable: variable, visitedBy: userService.current, delphiPhase: delphiService.phase ).save()
		}
		FinishedVariableEvent.logEvent( variable )
	}

	/**
	 * Finds all parents for each of 'children'.
	 * @param children
	 * @return
	 */
	List<Variable> getSpecifiedParents( List<Variable> children ) {
		return Relationship.findAllByCreatedByAndDelphiPhaseAndChildInListAndExists( userService.current, delphiService.phase, children, true )*.parent
	}

	/**
	 * Finds all of the parents of 'child' which the current user specified. This will only pull up relationships from
	 * the current round. Ideally, the first time you visit, say, the second round, you'd like to see your relationships
	 * from last time. This is taken care of because we instantiate new relationships each round, prefilling them with
	 * values from the previous round (see {@link VariableService#createRelationships(bn.elicitator.Variable, java.util.List)}).
	 * @param variable
	 * @return
	 */
	List<Variable> getSpecifiedParents( Variable child ) {
		return getSpecifiedRelationshipsByChild( child )*.parent
	}

	List<Relationship> getSpecifiedRelationshipsByParent( Variable parent ) {
		return Relationship.findAllByCreatedByAndDelphiPhaseAndParentAndExists( userService.current, delphiService.phase, parent, true )
	}

	List<Relationship> getSpecifiedRelationshipsByChild( Variable child ) {
		return Relationship.findAllByCreatedByAndDelphiPhaseAndChildAndExists( userService.current, delphiService.phase, child, true )
	}

	/**
	 * Same as {@link VariableService#getSpecifiedParents(bn.elicitator.Variable)} but the other way around.
	 * @param child
	 * @return
	 * @see VariableService#getSpecifiedParents(bn.elicitator.Variable)
	 */
	List<Variable> getSpecifiedChildren( Variable child ) {
		return Relationship.findAllByCreatedByAndDelphiPhaseAndParentAndExists( userService.current, delphiService.phase, child, true )*.child
	}
}
