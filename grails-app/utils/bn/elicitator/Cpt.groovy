package bn.elicitator

import bn.elicitator.network.BnProbability
import org.apache.commons.collections.CollectionUtils


class Cpt {

	private Variable variable

	private List<Probability> probabilities

	public Cpt( List<Probability> probabilities ) {
		this.probabilities = probabilities
		this.variable      = probabilities.size() > 0 ? probabilities[ 0 ].childState.variable : null
	}

	public Variable getVariable() { this.variable }

	public List<Probability> getProbabilities() { this.probabilities }

	double getProbabilityFor( State state, List<State> states ) {
		Probability probability = probabilities.find {
			it.childState.id == state.id && CollectionUtils.isEqualCollection( states*.id, it.parentStates*.id )
		}
		if (probability == null) {
			return 0
		} else {
			return probability.probability
		}
	}
}
