package bn.elicitator.events

import bn.elicitator.Relationship

class RemovedRedundantEvent extends RelationshipEvent {

	static logEvent( Relationship relationship ) {

		saveEvent( new RemovedRedundantEvent( parent: relationship.parent, child : relationship.child ) )
	}

	@Override
	String getDescription() {
		"Removed redundant relationship '$parent.readableLabel' to '$child.readableLabel'"
	}
}
