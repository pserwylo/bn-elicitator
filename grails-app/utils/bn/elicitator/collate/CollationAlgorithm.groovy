package bn.elicitator.collate

import bn.elicitator.Relationship
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.analysis.CandidateNetwork

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
    
}
