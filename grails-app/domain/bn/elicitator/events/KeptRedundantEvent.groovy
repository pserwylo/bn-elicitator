package bn.elicitator.events

import bn.elicitator.Relationship

class KeptRedundantEvent extends RelationshipEvent {

	static logEvent( Relationship relationship ) {
		String description = "Kept redundant relationship '$relationship.parent.readableLabel' to '$relationship.child.readableLabel'"
		saveEvent( new KeptRedundantEvent( parent: relationship.parent, child : relationship.child, description: description ) )
	}

}
