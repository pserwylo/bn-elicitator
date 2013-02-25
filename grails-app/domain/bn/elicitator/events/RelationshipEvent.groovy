package bn.elicitator.events

import bn.elicitator.Relationship
import bn.elicitator.Variable

/**
 * It would be nice to just save a foreign key to the relationship, but that will result in lost information after
 * we update the relationship again.
 */
abstract class RelationshipEvent extends LoggedEvent {

	Variable parent
	Variable child

}
