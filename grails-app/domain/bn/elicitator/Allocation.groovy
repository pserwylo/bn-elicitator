package bn.elicitator

import bn.elicitator.auth.User

class Allocation {

	List<Variable> variables = []

	int totalQuestionCount   = 0

	User user

	void addVariable( Variable var, List<Variable> potentialParents ) {
		variables.add( var )
		totalQuestionCount += potentialParents.size()
	}

	String toString() {
		"User $user allocated ${variables.size()} variables ($totalQuestionCount questions)."
	}
}
