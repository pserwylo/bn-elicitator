package bn.elicitator

import bn.elicitator.auth.User

class Probability {

	static constraints = {
	}

	static hasMany = [ parentStates : State ]

	State childState

	Double probability

	User createdBy

}
