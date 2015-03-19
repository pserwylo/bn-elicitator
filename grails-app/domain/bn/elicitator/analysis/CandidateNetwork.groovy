package bn.elicitator.analysis

import bn.elicitator.Variable
import bn.elicitator.network.Arc
import bn.elicitator.network.Graph
import org.apache.commons.collections.CollectionUtils

class CandidateNetwork implements Graph {

    static hasMany = [ arcs : CandidateArc ]
    
    SHD calcShd( Graph other ) {

        Collection<CandidateArc> removed  = CollectionUtils.subtract( arcs, other.arcs )
        Collection<CandidateArc> added    = CollectionUtils.subtract( other.arcs, arcs )
        Collection<CandidateArc> reversed = CollectionUtils.intersection( arcs*.reversed, other.arcs )
        
        new SHD(
            arcsInOther : other.arcs,
            arcsInThis  : this.arcs,
            added       : added,
            removed     : removed,
            reversed    : reversed,
        )

    }
    
    String toString() {
        return "Candidate Network (${arcs.size()} arcs)"
        
    }
    
    static class SHD {
        
        Collection<Arc> arcsInThis
        Collection<Arc> arcsInOther
        Collection<CandidateArc> added
        Collection<CandidateArc> removed
        Collection<CandidateArc> reversed
        
        int getShd() {
            added.size() + removed.size() + reversed.size()
        }

        int getTruePositives() {
            def fromThis     = arcsInThis.collect { "$it.from.id-$it.to.id" }
            def fromThat     = arcsInOther.collect { "$it.from.id-$it.to.id" }
            def intersection = fromThis.intersect( fromThat )
            return intersection.size()
        }

        int getFalsePositives() {
           added.size()
        }

        int getFalseNegatives() {
           removed.size()
        }
        
        int getConditionPositive() {
            arcsInOther.size()
        }

        int getConditionNegative() {
            def vars = []
            vars.addAll( arcsInOther*.from*.id )
            vars.addAll( arcsInOther*.to*.id )
            def numVariables = vars.unique().size()
            
            return numVariables * numVariables - conditionPositive
        }

        double getFalsePositiveRate() {
            falsePositives / conditionNegative
        }

        double getTruePositiveRate() {
            truePositives / conditionPositive
        }
        
    }
    
}
