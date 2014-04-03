package bn.elicitator.network
import bn.elicitator.Probability
import bn.elicitator.State
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

	static BnProbability fromProbability( Probability p ) {
		new BnProbability(
			childState   : p.childState,
			probability  : p.probability,
			parentStates : p.parentStates
		)
	}

	String toString() {
		if ( parentStates.size() == 0 ) {
			"P( $childState.variable.label = $childState.label )"
		} else {
			def given = parentStates.collect{ "$it.variable.label = $it.label" }
			"P( $childState.variable.label = $childState.label | ${given.join( ", ")} )"
		}
	}
}
