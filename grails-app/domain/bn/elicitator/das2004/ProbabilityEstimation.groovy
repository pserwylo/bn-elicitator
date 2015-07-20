package bn.elicitator.das2004

import bn.elicitator.State
import bn.elicitator.auth.User

import java.text.DecimalFormat

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
    
    String toString() {
        String formattedProb = new DecimalFormat( "#.##" ).format( probability )
        parentConfiguration ?
            "Pr( ${childState.toConciseString() } | ${ parentConfiguration.toString() } ) = $formattedProb" :
            "Pr( ${childState.toConciseString() } ) = $formattedProb"
    }

}