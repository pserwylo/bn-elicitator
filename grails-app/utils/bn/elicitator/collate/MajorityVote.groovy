package bn.elicitator.collate

import bn.elicitator.Relationship
import bn.elicitator.Variable
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.network.Arc
import groovy.transform.EqualsAndHashCode
import org.codehaus.groovy.util.HashCodeHelper

class MajorityVote extends CollationAlgorithm {

    private int threshold

    private Map<CandidateArc, Integer> arcCounts = [ : ]
    
    public MajorityVote( int threshold, Collection<Relationship> toCollate) {
        super( toCollate )
        this.threshold = threshold
    }

    public Collection<CandidateArc> getResultingArcs() {

        arcCounts.entrySet().findAll { it.value >= threshold }*.key
        
    }

    private Map<Variable, Map<Variable, CandidateArc>> cache

    private CandidateArc load( Arc arc ) {
        
        if ( !cache ) {
            cache = [ : ]
            CandidateArc.list().each { CandidateArc toCache ->
                if ( !cache.containsKey( toCache.from ) ) {
                    cache[ toCache.from ] = [ : ]
                }
                
                cache[ toCache.from ][ toCache.to ] = toCache
            }
        }

        if ( !cache[ arc.from ].containsKey( arc.to ) ) {
            cache[ arc.from ][ arc.to ] = CandidateArc.getOrCreate( arc )
        }
        
        return cache[ arc.from ][ arc.to ]
    }
    
    protected void collateArcs() {

        arcCounts = [ : ]
        
        print "Collating arcs..."
        
        toCollate.each { Relationship arc ->
            
            if ( arc.exists ) {
                
                CandidateArc candidateArc = load( arc )

                if ( !arcCounts.containsKey( candidateArc ) ) {
                    arcCounts[ candidateArc ] = 0
                }

                arcCounts[ candidateArc ]++
                
            }
            
        }
        
        println " Done!"
    }

    @EqualsAndHashCode
    private static class HashableArc {

        public Arc arc
    }

}
