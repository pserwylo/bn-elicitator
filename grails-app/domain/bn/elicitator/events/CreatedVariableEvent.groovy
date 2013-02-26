package bn.elicitator.events

import bn.elicitator.Variable

class CreatedVariableEvent extends LoggedEvent {

	Variable variable
	Variable whileViewingVariable

	static logEvent( Variable var, Variable whileViewingVariable ) {
		saveEvent( new CreatedVariableEvent( variable : var, whileViewingVariable: whileViewingVariable ) )
	}

	String getDescription() {
		"Created '$variable.readableLabel' (while pondering '${whileViewingVariable?.readableLabel}')"
	}

}
