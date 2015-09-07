package bn.elicitator.collate.cpt

import bn.elicitator.Variable
import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.Probability
import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.analysis.CandidateNetwork

class MeanCpt extends CptCollationAlgorithm {

    MeanCpt(Map<Variable, List<Cpt>> cptsForAnalysis, Das2004Service service, CandidateNetwork goldStandardStructure) {
        super(cptsForAnalysis, service, goldStandardStructure)
    }
    
    protected List<Cpt> combineCpts( Map<Variable, List<Cpt>> allCpts ) {

        allCpts.collect { Variable variable, List<Cpt> cptsToCombine ->

            if ( cptsToCombine ) {
                Cpt cpt = new Cpt(
                    probabilities: cptsToCombine[0].probabilities.collect { Probability prob ->
                        new Probability(
                                parentStates : prob.parentStates,
                                childState   : prob.childState,
                                probability  : ( cptsToCombine*.getProbabilityFor( prob.childState, prob.parentStates?.asList() ).sum() as Double ) / cptsToCombine[ 0 ].probabilities.size()
                        )
                    }
                )
                cpt.normalize()
                return cpt
            } else {
                return new Cpt( probabilities: [] )
            }
            
        }
    }
    
}
