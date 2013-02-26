package bn.elicitator.events

import bn.elicitator.Variable

class ViewRelationshipsEvent extends LoggedEvent {

	Variable parent

	static void logEvent( Variable parent ) {
		saveEvent( new ViewRelationshipsEvent( parent: parent ) )
	}

	@Override
	String getDescription() {
		"Viewed potential parents of '$parent.readableLabel'"
	}
}
