package bn.elicitator.das2004

import bn.elicitator.State
import bn.elicitator.auth.User

class ProbabilityEstimation {

	static constraints = {
	}

	State childState

	CompatibleParentConfiguration parentConfiguration

	double probability

	User createdBy

}