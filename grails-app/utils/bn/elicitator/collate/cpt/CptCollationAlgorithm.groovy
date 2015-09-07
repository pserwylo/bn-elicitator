package bn.elicitator.collate.cpt

import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.Probability
import bn.elicitator.Variable
import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.auth.User
import bn.elicitator.das2004.CompletedDasVariable
import bn.elicitator.network.Graph

abstract class CptCollationAlgorithm {

    private Map<Variable, List<Cpt>> cptsForAnalysis
    protected final Das2004Service service
    protected final Graph goldStandard

    public CptCollationAlgorithm( Map<Variable, List<Cpt>> cptsForAnalysis, Das2004Service service, Graph goldStandard ) {
        this.cptsForAnalysis = cptsForAnalysis
        this.service = service
        this.goldStandard = goldStandard
    }

    /**
     * Takes a list of multiple versions of each CPT for an entire network (each node has one CPT for each expert
     * who answered questions in relation to that node). The collection of CPTs belonging to a single node should
     * be combined, then the resulting CPTs for each node returned.
     */
    protected abstract List<Cpt> combineCpts( Map<Variable, List<Cpt>> cpts )
    
    public final List<Cpt> run() {
        combineCpts( cptsForAnalysis )
    }

}
