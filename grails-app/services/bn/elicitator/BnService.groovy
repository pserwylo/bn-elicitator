package bn.elicitator

class BnService {

	def delphiService
	def variableService

	/**
	 * "child" has "redundantParent" as a direct parent, and as an indirect parent by following the list of parents
	 * in "chains".
	 */
	public class RedundantRelationship {

		Variable child
		Variable redundantParent
		Relationship relationship
		List<Variable> mediatingChain
		List<List<Variable>> chains = []

		public String toString()
		{
			return "Redundant relationship ${redundantParent} -> ${child} (due to ${mediatingChain*.readableLabel.join( " -> " )})"
		}

	}

	public class CyclicalRelationship {

		List<Variable> chain = []

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

	public class TreeNode {
		Variable var
		TreeNode child = null
		List<TreeNode> parents = []

		List<Variable> collapseTree()
		{
			return []
		}

		List<Variable> getPathTo( Variable parent )
		{
			List<Variable> path = null

			for ( TreeNode parentNode in parents )
			{
				path = parentNode.getPathTo( parent )
				if ( path != null )
				{
					path.add( var )
					return path
				}
			}

			// If we didn't return, none of our children contain 'parent'. Perhaps we do?
			if ( parent == var )
			{
				path = [ var ]
			}

			return path
		}

		List<Variable> getDescendantsIncludingSelf()
		{
			List<Variable> descendants
			if ( child == null )
			{
				descendants = []
			}
			else
			{
				descendants = child.getDescendantsIncludingSelf()
			}
			descendants.add( var )
			return descendants
		}

		List<Variable> getDescendants()
		{
			List<Variable> descendants = getDescendantsIncludingSelf()
			descendants.pop()
			return descendants
		}

		List<TreeNode> getLeaves()
		{
			List<TreeNode> leaves = []

			if ( parents.size() == 0 )
			{
				leaves.add( this )
			}
			else
			{
				for ( TreeNode parent in parents )
				{
					leaves.addAll( parent.getLeaves() )
				}
			}
			return leaves
		}
	}

	public void keepRedundantRelationship(RedundantRelationship redundantRelationship) {

		redundantRelationship.relationship.isRedundant = Relationship.IS_REDUNDANT_NO
		redundantRelationship.relationship.save( flush: true )

	}

	public void removeRedundantRelationship(RedundantRelationship redundantRelationship) {

		Comment comment = redundantRelationship.relationship.comment
		if ( comment != null )
		{
			comment.delete( flush: true )
			redundantRelationship.relationship.comment = null
		}

		redundantRelationship.relationship.isRedundant = Relationship.IS_REDUNDANT_YES
		redundantRelationship.relationship.exists = false
		redundantRelationship.relationship.save( flush: true )
	}

	public List<CyclicalRelationship> getCyclicalRelationships() {

		List<Variable> allVars = Variable.list()
		List<CyclicalRelationship> cyclicalRelationships = []

		for ( Variable child in allVars )
		{
			TreeNode treeOfParents = new TreeNode( var: child )
			populateAllParents( treeOfParents )

			List<TreeNode> leafNodes = treeOfParents.leaves
			for ( TreeNode leaf in leafNodes )
			{
				List<Variable> descendants = leaf.descendantsIncludingSelf
				List<Variable> leafParents = variableService.getSpecifiedParents( leaf.var )
				for ( Variable leafParent in leafParents )
				{
					Integer index = descendants.indexOf( leafParent )
					if ( index >= 0 )
					{

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
	 * Builds a tree of parents for each variable.
	 * This tree is traversed, looking for each of the variables direct parents (attempting to find them in a place other
	 * than a direct parent).
	 * @return
	 * @see RedundantRelationship
	 */
	public List<RedundantRelationship> getRedundantRelationships() {

		List<Variable> allVars = Variable.list()
		List<RedundantRelationship> redundantRelationships = []

		for ( Variable child in allVars )
		{
			TreeNode treeOfParents = new TreeNode( var: child )
			populateAllParents( treeOfParents )
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

						RedundantRelationship rel = redundantRelationships.find { it.child == child && it.redundantParent == directParent.var }

						if ( rel != null )
						{
							rel.chains.add( path )
						}
						else
						{
							redundantRelationships.add(
								new RedundantRelationship(
										child: child,
										redundantParent: directParent.var,
										relationship: Relationship.findByChildAndParentAndDelphiPhase( child, directParent.var, delphiService.phase ),
										mediatingChain: path,
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
	 *
	 * @param child
	 */
	private void populateAllParents( TreeNode child )
	{
		List<Variable> parents = variableService.getSpecifiedParents( child.var )
		for ( Variable parent in parents )
		{
			if ( !child.getDescendants().contains( parent ) )
			{
				TreeNode parentNode = new TreeNode( var: parent, child: child )
				populateAllParents( parentNode )

				child.parents.add( parentNode )
			}
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

}
