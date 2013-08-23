package bn.elicitator.das2004

import bn.elicitator.Variable
import bn.elicitator.auth.User

class PairwiseComparison {

	static constraints = {
		mostImportantParent( nullable : true )
	}

	Variable child


	Variable parentOne

	Variable parentTwo

	Variable mostImportantParent

	int weight = 0

	User createdBy

}