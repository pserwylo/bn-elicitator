package bn.elicitator.das2004

import bn.elicitator.State
import bn.elicitator.auth.User

class ProbabilityEstimation {

	static constraints = {
		parentConfiguration( nullable : true )
	}

	State childState

	CompatibleParentConfiguration parentConfiguration

	double probability

	User createdBy

}