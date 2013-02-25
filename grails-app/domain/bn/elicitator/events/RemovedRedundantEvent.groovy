package bn.elicitator.events

import bn.elicitator.Relationship

class RemovedRedundantEvent extends RelationshipEvent {

	static logEvent( Relationship relationship ) {
		String description = "Removed redundant relationship '$relationship.parent.readableLabel' to '$relationship.child.readableLabel'"
		saveEvent( new RemovedRedundantEvent( parent: relationship.parent, child : relationship.child, description: description ) )
	}

}
