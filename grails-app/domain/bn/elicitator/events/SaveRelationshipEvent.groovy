package bn.elicitator.events

import bn.elicitator.Relationship
import bn.elicitator.Variable

/**
 * It would be nice to just save a foreign key to the relationship, but that will result in lost information after
 * we update the relationship again.
 */
class SaveRelationshipEvent extends RelationshipEvent {

	String comment
	Boolean doesRelationshipExist

	static logEvent( Relationship relationship ) {
		saveEvent( new SaveRelationshipEvent( parent: relationship.parent, child : relationship.child, comment: relationship.comment?.comment, doesRelationshipExist: relationship.exists ) )
	}

	@Override
	String getDescription() {
		String exists = doesRelationshipExist ? "exists" : "doesn't exist"
		"Relationship '$parent.readableLabel' -> '$child.readableLabel' $exists"
	}
}
