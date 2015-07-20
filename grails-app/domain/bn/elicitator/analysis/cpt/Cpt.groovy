package bn.elicitator.analysis.cpt

import bn.elicitator.Probability
import bn.elicitator.State
import bn.elicitator.Variable
import org.apache.commons.collections.CollectionUtils

class Cpt {

    static hasMany = [ probabilities : Probability ]
    
    static mapping = {
        probabilities cascade: 'all'
    }

	public Variable getVariable() {
        probabilities.size() > 0 ? probabilities[ 0 ].childState.variable : null
    }

	double getProbabilityFor( State state, List<State> parentStates ) {

		Probability probability = probabilities.find {
			if ( it.childState.id != state.id ) {
				return false
			} else {
				if (parentStates?.size() > 0)
					return CollectionUtils.isEqualCollection(parentStates*.id, it.parentStates*.id)
				else
					return true
			}
		}

		if (probability == null) {
			return 0
		} else {
			return probability.probability
		}
	}
}
