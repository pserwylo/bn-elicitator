package bn.elicitator.troia

import bn.elicitator.Probability

class DawidSkeneProbability {

	Probability probability
	double uncertainty

	String toString() {
		String child   = "$probability.childState.variable.label = $probability.childState.label"
		String parents = ( probability.parentStates?.size() > 0 ) ? "| ${probability.parentStates*.label.join( ', ' )} " : ""
		"P( $child $parents) = $probability.probability"
	}

}
