package bn.elicitator.analysis.cpt

import bn.elicitator.State

class CandidateProbability {
    
    static hasMany = [ parentStates : State ]
    
    State childState
    
    double probability
    
}
