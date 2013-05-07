package bn.elicitator

import bn.elicitator.auth.User

class Allocation {

	static hasMany = [ variables: Variable ]

	int totalQuestionCount   = 0

	User user

	void addVariable( Variable var, List<Variable> potentialParents ) {
		if ( !variables ) {
			variables = []
		}
		variables.add( var )
		totalQuestionCount += potentialParents.size()
	}

	String toString() {
		"User $user allocated ${variables.size()} variables ($totalQuestionCount questions)."
	}
}
