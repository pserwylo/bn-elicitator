package bn.elicitator

import org.apache.commons.collections.CollectionUtils


class Cpt {

	private Variable variable

	private List<Probability> probabilities

	public Cpt( List<Probability> probabilities ) {
		this.probabilities = probabilities
		this.variable = probabilities.size() > 0 ? probabilities[ 0 ].childState.variable : null
	}

	public Variable getVariable() { this.variable }

	public List<Probability> getProbabilities() { this.probabilities }

	double getProbabilityFor( State state, List<State> states ) {
		Probability probability = probabilities.find {
			it.childState == state && CollectionUtils.isEqualCollection( states, it.parentStates )
		}
		probability?.probability ?: 0
	}
}
