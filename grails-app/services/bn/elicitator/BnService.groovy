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

import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.auth.User
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode
import bn.elicitator.network.BnProbability

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
		List<Probability> probabilities = []

		if ( user != null ) {
			probabilities = Probability.withCriteria {
				childState {
					variable {
						eq( 'id', childVariable.id )
					}
				}

				createdBy {
					eq( 'id', userService.current.id )
				}
			}
		} else {

			probabilities = BnProbability.withCriteria {
				childState {
					variable {
						eq( 'id', childVariable.id )
					}
				}
			}.collect { it.toProbability() }

		}

		return new Cpt( probabilities : probabilities )
	}

}
