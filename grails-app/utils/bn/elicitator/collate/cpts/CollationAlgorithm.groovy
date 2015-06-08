package bn.elicitator.collate.cpts

import bn.elicitator.Relationship
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.analysis.CandidateNetwork
import bn.elicitator.auth.User

abstract class CollationAlgorithm {

    private Collection<Relationship> toCollate

    public CollationAlgorithm( Collection<Relationship> toCollate ) {
        this.toCollate = toCollate
    }
    
    public final Collection<Relationship> getToCollate() { this.toCollate }

    abstract Collection<CandidateArc> getResultingArcs()
    
    abstract protected void collateArcs()
    
    public final CandidateNetwork run() {
        collateArcs()
        new CandidateNetwork( arcs : resultingArcs ).save( flush : true, failOnError : true )
    }
    
    public Map<User, Double> getExpertWeights() {
        User.list().collectEntries {
            new MapEntry( it, 1.0 )
        }
    }
    
}
