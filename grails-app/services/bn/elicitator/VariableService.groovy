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

class VariableService 
{

	DelphiService delphiService

	/**
	 * Calculates how many variable the user has visited (seen in the browser).
	 */
	public int getVisitedCount( ShiroUser user = ShiroUser.current, Integer delphiPhase = AppProperties.properties.delphiPhase )
	{
		VisitedVariable.findAllByVisitedByAndDelphiPhase( user, delphiPhase ).size()
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
		List<Relationship> problemRelationships = Relationship.findAllByChildAndCreatedByAndDelphiPhaseInListAndParentIsNotNull( toSearch, ShiroUser.current, phasesToSearch )
		List<Variable> parents = problemRelationships*.parent
		parents.each
		{
			if ( !toElicit.contains( it ) )
			{
				findParentsToElicit( it, toElicit, hasCheckedParents )
			}
		}
	}

	public List<Variable> getInitialChildVars()
	{
		return Variable.findAllByVariableClass( VariableClass.problem )
	}

	/**
	 * Get all variables for which we want to elicit parents for.
	 * @return
	 */
	public List<Variable> getAllChildVars() 
	{
		List<Variable> initialChildren = getInitialChildVars()

		List<Variable> toExclude = []
		List<Variable> parents = findAllRelatedVars( initialChildren, toExclude )
		initialChildren.addAll( parents )

		// Hacky way to filter non-unique members out...
		return initialChildren.toSet().toList().sort( new Comparator<Variable>() {

			@Override
			int compare( Variable first, Variable second )
			{
				Boolean firstIsProblem = ( first.variableClass == VariableClass.problem )
				Boolean secondIsProblem = ( second.variableClass == VariableClass.problem )

				if ( firstIsProblem && !secondIsProblem )
				{
					return -1;
				}
				else if ( !firstIsProblem && secondIsProblem )
				{
					return 1;
				}
				else
				{
					return first.readableLabel.compareTo( second.readableLabel )
				}
			}

		})
	}

	/**
	 * Recursively travers upwards from children, looking for parents which are related.
	 * @param children
	 * @param toExclude
	 * @return
	 */
	private List<Variable> findAllRelatedVars( List<Variable> children, List<Variable> toExclude )
	{
		List<Relationship> relationships = Relationship.findAllByDelphiPhaseAndCreatedByAndChildInListAndExists( delphiService.phase, ShiroUser.current, children, true )
		relationships = relationships.findAll { !toExclude.contains( it ) }

		// Hacky way to filter out non-unique items...
		List<Variable> parents = relationships*.parent.toSet().toList()
		List<Variable> notYetChecked = parents.findAll { !children.contains( it ) && !toExclude.contains( it ) }

		if ( notYetChecked.size() > 0 )
		{
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
	public List<Variable> getPotentialParents( Variable child )
	{
		Variable.findAllByVariableClassInList( child.variableClass.potentialParents )


	}

	/**
	 * If the variable has been visited by the current user, then the VisitedVariable corresponding to this visit will
	 * be returned (for the current delphi phase and the current user). Otherwise it will return null.
	 * @param variable
	 * @return
	 */
	VisitedVariable getVisitedVariable( Variable variable, Integer delphiPhase = AppProperties.properties.delphiPhase, ShiroUser user = ShiroUser.current )
	{
		VisitedVariable.findByVariableAndVisitedByAndDelphiPhase( variable, user, delphiPhase )
	}

	/**
	 * @see VariableService#createRelationships(bn.elicitator.Variable, java.util.List)
	 */
	void initRelationships()
	{
		Integer count = Relationship.countByCreatedByAndDelphiPhase( ShiroUser.current, this.delphiService.phase )
		if ( count == 0 )
		{
			List<Variable> allVariables = Variable.list()
			Map<VariableClass, List<Variable>> potentialParentsCache = [:]
			for ( Variable child in allVariables )
			{
				// We don't want to call this so many times when we really only need to do it a couple of times...
				if ( !potentialParentsCache.containsKey( child.variableClass ) )
				{
					potentialParentsCache[ child.variableClass ] = this.getPotentialParents( child )
				}
				List<Variable> potentialParents = potentialParentsCache[ child.variableClass ];

				this.createRelationships( child, potentialParents )
			}
		}
	}

	/**
	 * Ensures that 'variable' has valid relationship objects (for the current user and delphi phase). There may already
	 * be relationships, in which case we ignore that. However, if there isn't, then we create them here, and set
	 * {@link Relationship#exists} to false.
	 * @param child
	 * @param potentialParents Just to reduce the amount of work (if we already have a reference to it, just pass it in,
	 * otherwise we'll get it outselves here).
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
					child: child,
					parent: parent,
					createdBy: ShiroUser.current,
					delphiPhase: AppProperties.properties.delphiPhase,
					exists: oldRelationship?.exists,
					confidence: oldRelationship?.exists ? oldRelationship.confidence : null
				).save()
			}
		}
	}

	/**
	 * Mark a variable as visited, so that we can check if off the list and provide feedback to the user (and admin)
	 * about what variables are left to visit.
	 * @param variable
	 */
	void visitVariable( Variable variable ) {

		VisitedVariable visited = getVisitedVariable( variable )

		if ( visited == null )
		{
			new VisitedVariable( variable: variable, visitedBy: ShiroUser.current, delphiPhase: delphiService.phase ).save()
		}

		LoggedEvent.logViewRelationships( variable )

	}

	/**
	 * Finds all parents for each of 'children'.
	 * @param children
	 * @return
	 * @see getSpecifiedParents( Variable )
	 */
	List<Variable> getSpecifiedParents( List<Variable> children )
	{
		return Relationship.findAllByCreatedByAndDelphiPhaseAndChildInListAndExists( ShiroUser.current, delphiService.phase, children, true )*.parent
	}

	/**
	 * Finds all of the parents of 'child' which the current user specified. This will only pull up relationships from
	 * the current round. Ideally, the first time you visit, say, the second round, you'd like to see your relationships
	 * from last time. This is taken care of because we instantiate new relationships each round, prefilling them with
	 * values from the previous round (see {@link VariableService#createRelationships(bn.elicitator.Variable, java.util.List)}).
	 * @param variable
	 * @return
	 */
	List<Variable> getSpecifiedParents( Variable child )
	{
		return getSpecifiedRelationshipsByChild( child )*.parent
	}

	List<Relationship> getSpecifiedRelationshipsByParent( Variable parent )
	{
		return Relationship.findAllByCreatedByAndDelphiPhaseAndParentAndExists( ShiroUser.current, delphiService.phase, parent, true )
	}

	List<Relationship> getSpecifiedRelationshipsByChild( Variable child )
	{
		return Relationship.findAllByCreatedByAndDelphiPhaseAndChildAndExists( ShiroUser.current, delphiService.phase, child, true )
	}

	/**
	 * Same as {@link VariableService#getSpecifiedParents(bn.elicitator.Variable)} but the other way around.
	 * @param child
	 * @return
	 * @see VariableService#getSpecifiedParents(bn.elicitator.Variable)
	 */
	List<Variable> getSpecifiedChildren( Variable child )
	{
		return Relationship.findAllByCreatedByAndDelphiPhaseAndParentAndExists( ShiroUser.current, delphiService.phase, child, true )*.child
	}
}
