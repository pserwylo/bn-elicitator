package bn.elicitator.events

import bn.elicitator.Variable

class CreatedVariableEvent extends LoggedEvent {

	Variable variable

	static logEvent( Variable var ) {
		String description = "Created: '$var.readableLabel'"
		saveEvent( new CreatedVariableEvent( variable : var, description: description ) )
	}

}
