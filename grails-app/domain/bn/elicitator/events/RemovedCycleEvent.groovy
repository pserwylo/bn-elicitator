package bn.elicitator.events

import bn.elicitator.Variable

class RemovedCycleEvent extends RelationshipEvent {

	static logEvent( Variable parent, Variable child ) {
		String description = "Removed cycle by killing '$parent.readableLabel' to '$child.readableLabel'"
		saveEvent( new RemovedCycleEvent( parent: parent, child : child, description: description ) )
	}

}
