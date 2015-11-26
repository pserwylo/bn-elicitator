package bn.elicitator

import bn.elicitator.auth.User

import java.text.DecimalFormat

/**
 * This is the calculated probability (based on the algorithm which processes the users responses).
 *
 * This is distinct from the {@link bn.elicitator.das2004.ProbabilityEstimation} given by the user when they
 * answered the relevant survey question. It is also distinct from the {@link bn.elicitator.network.BnProbability}
 * which is the final probability value after averaging (or doing whatever is appropriate) for each user, which is
 * used in the final Bn that is output from the system.
 *
 * @see bn.elicitator.das2004.ProbabilityEstimation
 * @see bn.elicitator.network.BnProbability
 */
class Probability implements Comparable<Probability> {

	static constraints = {
        createdBy nullable: true
	}

	static hasMany = [ parentStates : State ]

	State childState

	Double probability

	User createdBy

    String toString() {
        String formattedProb = new DecimalFormat( "#.####" ).format( probability )
        return "${toStringWithoutValue()} = $formattedProb"
    }
    
    String toStringWithoutValue() {
        parentStates ?
            "Pr( ${childState.toConciseString() } | ${ sortedParentStates*.toConciseString().join( ', ' ) } )" :
            "Pr( ${childState.toConciseString() } )"
    }

    String toShortStringWithoutValue() {
        parentStates ?
            "${childState.toShortConciseString()}|${sortedParentStates*.toShortConciseString().join( ',' )}" :
            "Pr( ${childState.toConciseString() } )"
    }

    public List<State> getSortedParentStates() {
        parentStates?.sort { it1, it2 -> it1.label <=> it2.label }
    }

    @Override
    int compareTo(Probability that) {
        int initialResult = this.childState.label <=> that.childState.label
        if (initialResult != 0) {
            return initialResult
        }
        
        List<State> mySortedParents = this.sortedParentStates
        List<State> theirSortedParents = that.sortedParentStates
        while (mySortedParents.size() > 0 && theirSortedParents.size() > 0) {
            int result = mySortedParents[0].label <=> theirSortedParents[0].label
            if (result != 0) {
                return result
            }
            mySortedParents.remove(0)
            theirSortedParents.remove(0)
        }
        
        return 0;
    }
}
