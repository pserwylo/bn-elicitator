package bn.elicitator.analysis.cpt

import bn.elicitator.Probability
import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.auth.User
import org.apache.commons.collections.CollectionUtils

import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat

class Cpt {

    User createdBy
    
    static hasMany = [ probabilities : Probability ]
    
    static mapping = {
        createdBy nullable: true
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
    
    void normalize() {
        
        if ( probabilities?.size() > 0 && probabilities[ 0 ].parentStates?.size() > 0 ) {

            // Group based on parent states...
            Map<Object, List<Probability>> parentStates = probabilities.groupBy { Probability prob ->
                prob.parentStates*.id.sort { it1, it2 -> it1 <=> it2 }
            }

            // For each combination of parent states, normalize all values for each child state.
            parentStates.each {
                List<Probability> toNormalize = it.value
                normalizeProbabilities( toNormalize )
            }
            
        } else {
            
            normalizeProbabilities( probabilities )
            
        }
    }
    
    private void normalizeProbabilities( Collection<Probability> toNormalize ) {

        BigDecimal sum = toNormalize*.probability.sum() as BigDecimal
        println "Normalizing ${toNormalize*.probability} - currently summs to $sum"
        if ( sum == 0 ) {
            println "Tried to normalize, but sum of probabilities was 0 :("
            /*toNormalize.each { Probability prob ->
                prob.probability = 1 / toNormalize.size()
            }*/
        } else {
            BigDecimal factor = 1 / sum
            toNormalize.each { Probability prob ->
                prob.probability *= factor
            }
            sum = toNormalize*.probability.sum() as BigDecimal
            if ( sum < 0.9999999 || sum > 1.0000001 ) {
                throw new IllegalStateException( "Probabilities summed to $sum ${toNormalize*.probability}." )
            }
        }
        
    }
}
