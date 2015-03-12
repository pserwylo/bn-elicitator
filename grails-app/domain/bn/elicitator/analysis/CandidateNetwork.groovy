package bn.elicitator.analysis

import bn.elicitator.network.Graph
import org.apache.commons.collections.CollectionUtils

class CandidateNetwork implements Graph {

    static hasMany = [ arcs : CandidateArc ]
    
    SHD calcShd( Graph other ) {

        Collection<CandidateArc> removed  = CollectionUtils.subtract( arcs, other.arcs )
        Collection<CandidateArc> added    = CollectionUtils.subtract( other.arcs, arcs )
        Collection<CandidateArc> reversed = CollectionUtils.intersection( arcs*.reversed, other.arcs )
        
        new SHD(
            added    : added,
            removed  : removed,
            reversed : reversed,
        )

    }
    
    static class SHD {
        
        Collection<CandidateArc> added
        Collection<CandidateArc> removed
        Collection<CandidateArc> reversed
        
        int getShd() {
            added.size() + removed.size() + reversed.size()
        }
        
    }
    
}
