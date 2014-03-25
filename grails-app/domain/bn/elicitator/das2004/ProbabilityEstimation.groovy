package bn.elicitator.das2004

import bn.elicitator.State
import bn.elicitator.auth.User

/**
 * The answer given by a user to a specific question about parent states and the probability of seeing the child
 * state.
 * @see bn.elicitator.Probability For more info on the difference between the answer to a probability estimation
 * question and the final probability which is calculated and then used in the resulting Bn.
 */
class ProbabilityEstimation {

	static constraints = {
		parentConfiguration( nullable : true )
	}

	State childState

	CompatibleParentConfiguration parentConfiguration

	double probability

	User createdBy

}