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
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode

/**
 * All methods in this class presume that we are only operating on data for the current user, unless the method
 * signature says otherwise.
 */
class BnService {

	def delphiService
	def variableService
	def userService

	public void fixProblems( List<List<Variable>> cyclesToRemove )
	{
		cyclesToRemove.each { removeCycle( it[ 0 ], it[ 1 ] ) }
	}

	/**
	 * "child" has "redundantParent" as a direct parent, and as an indirect parent by following the list of parents
	 * in "chains".
	 */
	public class IndirectRelationship {

		Variable child
		Variable redundantParent
		Relationship relationship
		List<List<Variable>> chains = []

		public String toString()
		{
			return "Redundant relationship ${redundantParent} -> ${child} (due to ${chains.join( " and " )})"
		}

	}

	public class CyclicalRelationship {

		private List<Relationship> relationships = []
		List<Variable> chain = []

		List<Relationship> getRelationships()
		{
			return this.relationships
		}

		void setChain( List<Variable> chain )
		{
			this.relationships = []
			this.chain = chain
			for ( int i = 1; i < this.chain.size(); i ++ )
			{
				Variable child = this.chain.get( i - 1 )
				Variable parent = this.chain.get( i )
				this.relationships.add( 0, delphiService.getMyCurrentRelationship( parent, child ) )
			}
		}

		public String toString()
		{
			return "Cyclical relationships: " + chain*.readableLabel.join( " -> " )
		}

		/**
		 * Two chains are the same if they include the same sequence of variables.
		 * Because the first and last item should be the same, we remove one of them, and then we iteratively
		 * rotate one list so that the last item keeps coming to the front. If it eventually looks like our chain,
		 * then we are the same.
		 * @param that
		 * @return
		 */
		public boolean equals( Object that )
		{
			boolean equal = false
			if ( that instanceof CyclicalRelationship )
			{
				if ( that.chain.size() == chain.size() )
				{
					List<Variable> myChain = []
					List<Variable> otherChain = []
					myChain.addAll( chain )
					otherChain.addAll( that.chain )

					myChain.pop()
					otherChain.pop()

					// Rotate the items in the chain all the way around to see if any rotation matches our pattern...
					for ( i in 0..(otherChain.size()-1) )
					{
						otherChain = otherChain + otherChain.remove( 0 )
						if ( otherChain == myChain )
						{
							equal = true
							break;
						}
					}
				}
			}
			return equal
		}

	}

	/**
	 * This tree is a tree of all parent-child relationships between variables.
	 * @see BnService#populateAllParents(bn.elicitator.BnService.TreeNode, java.lang.Boolean)
	 */
	public class TreeNode {

		Variable var
		TreeNode child = null
		List<TreeNode> parents = []

		boolean equals( TreeNode compare ) {

			boolean same = ( var == compare.var )
			for ( TreeNode parent in parents ) {

				boolean parentHasEqual = false
				for ( TreeNode compareParent in compare.parents ) {
					if ( parent.equals( compareParent ) ) {
						parentHasEqual = true
						break
					}
				}

				if ( !parentHasEqual ) {
					same = false
					break
				}
			}

			return same
		}

		List<Variable> getDescendantsIncludingSelf() {
			List<Variable> descendants
			if ( child == null ) {
				descendants = []
			} else {
				descendants = child.getDescendantsIncludingSelf()
			}
			descendants.add( var )
			return descendants
		}

		List<Variable> getDescendants() {
			List<Variable> descendants = getDescendantsIncludingSelf()
			descendants.pop()
			return descendants
		}

		List<TreeNode> getLeaves() {
			List<TreeNode> leaves = []

			if ( parents.size() == 0 ) {
				leaves.add( this )
			} else {
				for ( TreeNode parent in parents ) {
					leaves.addAll( parent.getLeaves() )
				}
			}
			return leaves
		}

		String toString() { var.readableLabel }
	}

	public void removeCycle( Variable parent, Variable child ) {
		Relationship rel = delphiService.getMyCurrentRelationship( parent, child )
		if ( rel != null ) {
			if ( rel.comment != null ) {
				rel.comment.comment = ""
				rel.comment.save()
			}
			rel.exists = false
			rel.save( flush: true )
		}
	}

	public List<Variable> getSpecifiedParents( Variable child, List<Relationship> allRelationshipsForUser ) {
		allRelationshipsForUser.findAll { it.child == child }*.parent
	}

	public List<CyclicalRelationship> getCyclicalRelationships() {

		List<Variable> allVars                           = Variable.list()
		List<CyclicalRelationship> cyclicalRelationships = []
		List<Relationship> allRelationships              = Relationship.findAllByCreatedByAndDelphiPhaseAndExists( userService.current, delphiService.phase, true )

		for ( Variable child in allVars ) {
			TreeNode treeOfParents = new TreeNode( var: child )
			populateAllParents( treeOfParents, allRelationships )

			List<TreeNode> leafNodes = treeOfParents.leaves
			for ( TreeNode leaf in leafNodes ) {
				List<Variable> descendants = leaf.descendantsIncludingSelf
				List<Variable> leafParents = getSpecifiedParents( leaf.var, allRelationships )
				for ( Variable leafParent in leafParents ) {
					Integer index = descendants.indexOf( leafParent )
					if ( index >= 0 ) {
						List<Variable> chain = [ leaf.var ]
						chain.addAll( descendants[ index..descendants.size()-1 ] );

						cyclicalRelationships.add(
							new CyclicalRelationship(
								chain: chain
							)
						);
					}
				}
			}
		}

		return cyclicalRelationships.unique()
	}

	/**
	 * Look for relationships which are potentially redunant, due to the fact that their direct relationship between
	 * parent and child is better explained through one or more mediating variables.
	 *
	 * It does this by building a tree of parents for each variable, traversing it looking for everyones direct parents.
	 * If the direct parent is found as an indirect parent also, then it is potentially redundant.
	 * @return
	 * @see IndirectRelationship
	 */
	public List<IndirectRelationship> getRedundantRelationships() {

		List<Variable> allVars = Variable.list()
		List<IndirectRelationship> redundantRelationships = []

		List<Relationship> allRelationships = Relationship.findAllByCreatedByAndDelphiPhaseAndExists( userService.current, delphiService.phase, true )

		for ( Variable child in allVars )
		{
			TreeNode treeOfParents = new TreeNode( var: child )
			populateAllParents( treeOfParents, allRelationships )
			for ( TreeNode directParent in treeOfParents.parents )
			{
				for ( TreeNode otherDirectParent in treeOfParents.parents )
				{
					if ( otherDirectParent == directParent )
					{
						continue;
					}

					List<Variable> path = otherDirectParent.getPathTo( directParent.var )
					if ( path?.size() > 1 )
					{
						path.add( child )

						IndirectRelationship rel = redundantRelationships.find { it.child == child && it.redundantParent == directParent.var }

						if ( rel != null )
						{
							rel.chains.add( path )
						}
						else
						{
							redundantRelationships.add(
								new IndirectRelationship(
									child: child,
									redundantParent: directParent.var,
									relationship: Relationship.findByChildAndParentAndDelphiPhaseAndCreatedBy( child, directParent.var, delphiService.phase, ShiroUser.current ),
									chains: [ path ]
								)
							)
						}

					}
				}
			}
		}

		return redundantRelationships

	}

	/**
	 * @param child
	 */
	private void populateAllParents( TreeNode child, List<Relationship> allRelationships )
	{
		List<Variable> parents = allRelationships.findAll { it.child == child.var }*.parent
		for ( Variable parent in parents ) {
			TreeNode parentNode = new TreeNode( var: parent, child: child )
			if ( !child.getDescendants().contains( parent ) ) {
				populateAllParents( parentNode, allRelationships )
			}
			child.parents.add( parentNode )
		}
	}

	/**
	 * Recursively builds a chain of relationships which lead from parent -> child.
	 * If we are in the 'firstTime', then we ignore any parent -> child relationships (this is the direct relationship,
	 * but we are looking for longer chains which imply this indirect relationship is possibly redundant).
	 * @param child
	 * @param parent
	 * @param mediatingChain
	 */
	private void findMediatingChain( Variable child, Variable parent, List<Variable> mediatingChain, boolean firstTime = true )
	{

		List<Variable> parents = variableService.getSpecifiedParents( child )
		for ( Variable p in parents )
		{
			if ( p == parent )
			{
				if ( firstTime )
				{
					// If first time, then we are looking at the direct relationship between parent -> child which we are
					// not interested in. Rather, we want to dig deeper and find a longer indirect chain.
					continue;
				}
				else
				{
					// Begin the chain and stop searching...
					mediatingChain.add( p );
					break;
				}
			}

			// Didn't find it here?	 Keep searching, it might be somewhere down here...
			findMediatingChain( p, parent, mediatingChain, false )

			// Something down there started adding to it, so we presume we found the parent...
			if ( mediatingChain.size() > 0 )
			{
				mediatingChain.add( p )
				break;
			}
		}

		if ( firstTime )
		{
			mediatingChain.add( child )
		}

	}

	/**
	 * @param children
	 * @return
	 */
	private void findIndirectParents( List<Variable> children, List<Variable> allParents )
	{
		List<Variable> parents = variableService.getSpecifiedParents( children )
		parents.removeAll( allParents );

		if ( parents.size() > 0 )
		{
			allParents.addAll( parents )
			findIndirectParents( parents, allParents )
		}
	}

	public List<BnArc> getArcsByChild( Variable childVariable ) {
		if ( childVariable ) {
			return BnArc.withCriteria {
				child {
					variable {
						eq( 'id', childVariable.id )
					}
				}
			}
		} else {
			return []
		}
	}

	public List<BnArc> getArcsByChildren( List<Variable> children ) {
		if ( children.size() > 0 ) {
			return BnArc.withCriteria {
				child {
					variable {
						inList( 'id', children*.id )
					}
				}
			}
		} else {
			return []
		}
	}

	public List<BnArc> getArcsByChild( BnNode child) {
		getArcsByChild( child.variable )
	}

	public Cpt getCptFor( Variable childVariable, User user = userService.current ) {
		List<Probability> probabilities = Probability.withCriteria {
			childState {
				variable {
					eq( 'id', childVariable.id )
				}
			}

			if ( user == null ) {
				isNull( 'createdBy' )
			} else {
				createdBy {
					eq( 'id', userService.current.id )
				}
			}
		}

		new Cpt( probabilities )
	}
}
