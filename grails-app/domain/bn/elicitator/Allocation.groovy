package bn.elicitator

import bn.elicitator.auth.User

abstract class Allocation {

	static hasMany = [ variables: Variable ]

	int totalQuestionCount   = 0

	User user

	void addVariable( Variable var, int numQuestions ) {
		if ( !variables ) {
			variables = []
		}
		variables.add( var )
		totalQuestionCount += numQuestions
	}

	String toString() {
		"User $user allocated ${variables.size()} variables ($totalQuestionCount questions)."
	}
}
