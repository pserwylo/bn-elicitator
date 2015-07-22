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
    
    String toString() {
        if ( mostImportantParent == null ) {
            return "$parentOne.label == $parentTwo"
        } else {
            Variable leastImportant = mostImportantParent.id == parentOne.id ? parentTwo : parentOne
            return "$mostImportantParent.label ${weight}x $leastImportant.label"
        }
    }

}