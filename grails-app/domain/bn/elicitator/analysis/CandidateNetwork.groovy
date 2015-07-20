package bn.elicitator.analysis

import bn.elicitator.network.Arc
import bn.elicitator.network.Graph
import org.apache.commons.collections.CollectionUtils

class CandidateNetwork extends Graph {

    static mapping = { tablePerHierarchy false }
    
    static hasMany = [ candidateArcs: CandidateArc ]
    
    private static intersection( Collection<CandidateArc> arcs1, Collection<CandidateArc> arcs2 ) {
        def intersect = []
        for ( def a1 : arcs1 ) {
            for ( def a2 : arcs2 ) {
                if ( a2 == a1 && !intersect.contains( a2 ) ) {
                    intersect.add( a2 )
                }
            }
        }
        return intersect
    }

    @Override
    public Collection<Arc> getArcs() {
        return this.candidateArcs
    }
    
    SHD calcShd( CandidateNetwork other ) {

        Collection<CandidateArc> removed  = CollectionUtils.subtract( arcs, other.arcs )
        Collection<CandidateArc> added    = CollectionUtils.subtract( other.arcs, arcs )
        Collection<CandidateArc> reversed = intersection( arcs*.reversed, other.arcs )
        
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

        int getTrueNegatives() {
            totalNumArcs - truePositives - falsePositives - falseNegatives
        }
        
        int getFalseNegatives() {
           removed.size()
        }

        int getNumVariables() {
            def vars = []
            vars.addAll( arcsInOther*.from*.id )
            vars.addAll( arcsInOther*.to*.id )
            return vars.unique().size()
        }

        int getTotalNumArcs() {
            Math.pow( numVariables, 2 )
        }

        double getFalsePositiveRate() {
            falsePositives / ( falsePositives + trueNegatives )
        }

        double getTruePositiveRate() {
            truePositives / ( truePositives + falseNegatives )
        }
        
    }
    
}
