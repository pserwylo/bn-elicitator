package bn.elicitator.analysis

import bn.elicitator.network.Arc
import bn.elicitator.Variable

class CandidateArc implements Arc {
    
    Variable from
    Variable to
    
    static CandidateArc getOrCreate( Variable from, Variable to ) {
        findByFromAndTo( from, to ) ?: new CandidateArc( from : from, to : to ).save( flush : true, failOnError : true )
    }

    static CandidateArc getOrCreate( Arc arc ) {
        getOrCreate( arc.from, arc.to )
    }
    
    String toString() { "${from?.label} -> ${to?.label}" }
    
    boolean equals( Object that ) {
        if ( that != null && that instanceof CandidateArc ) {
            CandidateArc arc = (CandidateArc)that
            return arc.from.id == this.from.id && arc.to.id == this.to.id
        } else {
            return false
        }
    }
    
    CandidateArc getReversed() {
        getOrCreate( to, from )
    }
    
}
