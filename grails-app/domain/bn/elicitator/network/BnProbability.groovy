package bn.elicitator.network

import bn.elicitator.Probability
import bn.elicitator.State
import bn.elicitator.auth.User

/**
 * The probability for this configuration of child and parent states, which will be output with the final Bn.
 *
 * @see bn.elicitator.Probability For more info on the difference between the answer to a probability estimation
 * question and the final probability which is calculated and then used in the resulting Bn.
 */
class BnProbability {

	static constraints = {
	}

	static hasMany = [ parentStates : State ]

	State childState

	Double probability

	Probability toProbability() {
		new Probability(
			childState : childState,
			probability : probability,
			parentStates : parentStates,
		)
	}
}
