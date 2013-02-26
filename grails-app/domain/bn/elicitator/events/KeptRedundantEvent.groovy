package bn.elicitator.events

import bn.elicitator.Relationship

class KeptRedundantEvent extends RelationshipEvent {

	static logEvent( Relationship relationship ) {

		saveEvent( new KeptRedundantEvent( parent: relationship.parent, child : relationship.child ) )
	}

	@Override
	String getDescription() {
		"Kept redundant relationship '$parent.readableLabel' to '$child.readableLabel'"
	}
}
