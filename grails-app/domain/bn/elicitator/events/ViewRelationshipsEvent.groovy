package bn.elicitator.events

import bn.elicitator.Variable

class ViewRelationshipsEvent extends LoggedEvent {

	Variable parent

	static void logEvent( Variable parent ) {
		String description = "Viewed potential parents of '$parent.readableLabel'"
		saveEvent( new ViewRelationshipsEvent( parent: parent, description: description ) )
	}

}
