package bn.elicitator.collate.cpt

import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.Probability
import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.analysis.CandidateNetwork

class MeanCpt extends CptCollationAlgorithm {

    MeanCpt(Das2004Service service, CandidateNetwork goldStandardStructure) {
        super(service, goldStandardStructure)
    }
    
    protected Cpt combineCpts( List<Cpt> cpts ) {
        if ( cpts ) {
            new Cpt(
                probabilities: cpts[0].probabilities.collect { Probability prob ->
                    new Probability(
                            parentStates: prob.parentStates,
                            childState: prob.childState,
                            probability: ( cpts*.getProbabilityFor(prob.childState, prob.parentStates?.asList()).sum() as Double ) / cpts[ 0 ].probabilities.size()
                    )
                }
            )
        } else {
            new Cpt( probabilities: [] )
        }
    }
    
}
