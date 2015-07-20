package bn.elicitator.collate.cpt

import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.analysis.CandidateNetwork

class DawidSkeneCpt extends CptCollationAlgorithm {

    DawidSkeneCpt(Das2004Service service, CandidateNetwork goldStandardStructure) {
        super(service, goldStandardStructure)
    }

    protected Cpt combineCpts( List<Cpt> cpts ) {
        null
    }
    
}
