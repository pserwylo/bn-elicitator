package bn.elicitator.events

import bn.elicitator.Variable

class RemovedCycleEvent extends RelationshipEvent {

	static logEvent( Variable parent, Variable child ) {
		saveEvent( new RemovedCycleEvent( parent: parent, child : child ) )
	}

	@Override
	String getDescription() {
		"Removed cycle by killing relationship from '$parent.readableLabel' to '$child.readableLabel'"
	}
}
