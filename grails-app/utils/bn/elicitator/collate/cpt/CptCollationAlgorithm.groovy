package bn.elicitator.collate.cpt

import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.Probability
import bn.elicitator.Variable
import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.auth.User
import bn.elicitator.das2004.CompletedDasVariable
import bn.elicitator.network.Graph

abstract class CptCollationAlgorithm {

    protected final Das2004Service service
    protected final Graph goldStandard

    public CptCollationAlgorithm( Das2004Service service, Graph goldStandard ) {
        this.service = service
        this.goldStandard = goldStandard
    }

    protected abstract Cpt combineCpts( List<Cpt> cpts )
    
    public final List<Cpt> run() {
        goldStandard.variables.collect { Variable variable ->
            processVariable( variable )
        }
    }

    /**
     * Find all people allocated this variable, then for each of those people, produce a CPT.
     * Then collate those CPTs together.
     */
    private Cpt processVariable( Variable variable ) {
        
        println "Processing variable $variable.label..."
        
        Collection<Variable> parents = goldStandard.getParentsOf variable

        return combineCpts(
            CompletedDasVariable.findByVariable( variable ).collect { CompletedDasVariable completed ->
                processUsersVariable( completed.completedBy, variable, parents )
            }
        )
    }

    private Cpt processUsersVariable( User user, Variable child, Collection<Variable> parents ) {
        
        println "Processing variable $child.label (for user $user.id)..."
        
        List<Probability> probs
        if ( parents.size() == 0 ) {
            probs = service.calcMarginalProbability( child, user )
        } else if ( parents.size() == 1 ) {
            probs = service.singleParentConditional( child, user )
        } else {
            probs = service.calcConditionalProbability( child, parents, user )
        }
        return new Cpt( probabilities: probs )
    }
    
}
