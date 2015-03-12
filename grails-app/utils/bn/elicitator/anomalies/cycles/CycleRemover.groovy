package bn.elicitator.anomalies.cycles

import bn.elicitator.Variable
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.analysis.CandidateNetwork
import org.jgrapht.DirectedGraph
import org.jgrapht.alg.cycle.JohnsonSimpleCycles
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

public class CycleRemover {

    public static class DecisionSummary {
        
        int iteration
        int totalCycles
        CandidateArc worst
        int inNumCycles
        Resolution resolution
        
        enum Resolution {
            REVERSED,
            REMOVED
        }
        
        public String toString() {
            def res = resolution == Resolution.REVERSED ? "reversed" : "removed"
            return "Decision: [ Iteration: $iteration, Cycles: $totalCycles, $worst in $inNumCycles cycles so $res]"
            
        }
    }
    
    private final DirectedGraph<Variable, DefaultEdge> graph
    private final CandidateNetwork originalNetwork
    private final List<DecisionSummary> summary = []

    public CycleRemover( CandidateNetwork network ) {
        originalNetwork = network
        graph = new SimpleDirectedGraph<>( DefaultEdge.class )
        network.arcs.each { CandidateArc arc ->
            if ( !graph.containsVertex( arc.from ) ) graph.addVertex( arc.from )
            if ( !graph.containsVertex( arc.to   ) ) graph.addVertex( arc.to )
            graph.addEdge( arc.from, arc.to )
        }
    }

    private List<List<Variable>> getCycles() {
        new JohnsonSimpleCycles<Variable, DefaultEdge>( graph ).findSimpleCycles()
    }

    private void remove( Edge edge ) {
        graph.removeEdge( edge.from, edge.to )
    }

    private void reverse( Edge edge ) {
        remove( edge )
        graph.addEdge( edge.to, edge.from )
    }
    
    public List<DecisionSummary> getSummary() { this.summary }
    
    public CandidateNetwork getDag() {
        List<CandidateArc> arcs = new ArrayList( originalNetwork.arcs )
        summary.each {
            if ( it.resolution == DecisionSummary.Resolution.REMOVED ) {
                arcs.remove( it.worst )
            } else if ( it.resolution == DecisionSummary.Resolution.REVERSED ) {
                arcs.remove( it.worst )
                arcs.add( it.worst )
            }
        }
        return new CandidateNetwork( arcs : arcs ).save( flush : true, failOnError : true )
    }

    public CycleRemover removeCycles() {

        def cycles = getCycles()
        def count  = 0

        while ( cycles.size() > 0 ) {

            DecisionSummary decision = new DecisionSummary()
            decision.iteration = ++ count
            decision.totalCycles = cycles.size()
            
            int oldCount = cycles.size()
            Edge worst   = getWorstEdge( cycles )

            decision.worst       = CandidateArc.getOrCreate( worst.from, worst.to )
            decision.inNumCycles = worst.count

            reverse( worst )

            cycles = getCycles()

            if ( cycles.size() >= oldCount ) {

                println "  Whoops, that resulted in ${cycles.size()} cycles."
                println "  Removing: $worst.from -> $worst.to"
                remove( worst.reverse() )

                cycles = getCycles()

                decision.resolution = DecisionSummary.Resolution.REMOVED
            } else {
                decision.resolution = DecisionSummary.Resolution.REVERSED
            }
            
            summary.add( decision )
            
        }
        
        return this
    }

    private static class Edge {
        public Variable from
        public Variable to
        public int count

        public Edge reverse() {
            new Edge( from: to, to: from )
        }
    }

    private static Edge getWorstEdge( List<List<Variable>> cycles ) {

        Map<Variable, Map<Variable, Integer>> counter = new HashMap<>()

        def increment = { Variable from, Variable to ->
            if ( !counter.containsKey( from ) ) {
                counter[ from ] = new HashMap<>()
            }

            if ( !counter[ from ].containsKey( to ) ) {
                counter[ from ][ to ] = 1
            } else {
                counter[ from ][ to ] ++
            }
        }

        cycles.each { List<Variable> cycle ->

            for ( int i = 0; i < cycle.size() - 1; i ++ ) {
                increment( cycle[ i + 1 ], cycle[ i ] )
            }

            increment( cycle[ 0 ], cycle[ cycle.size() - 1 ] )

        }

        Variable worstFrom  = null
        Variable worstTo    = null
        int      worstCount = 0

        counter.entrySet().each { Map.Entry<Variable, Map<Variable, Integer>> outer ->
            Variable from = outer.key
            outer.value.each { Map.Entry<Variable, Integer> inner ->
                Variable to = inner.key
                int count   = inner.value

                if ( count > worstCount ) {
                    worstCount = count
                    worstFrom  = from
                    worstTo    = to
                }

            }
        }

        return new Edge( from: worstFrom, to: worstTo, count: worstCount )

    }

}
