package bn.elicitator.network

import bn.elicitator.Variable

abstract class Graph {
    
    public abstract Collection<Arc> getArcs()

    Collection<Variable> getVariables() {
        List<Variable> vars = []
        arcs.each { Arc arc ->
            if ( !vars.contains( arc.from ) ) {
                vars.add( arc.from )
            }

            if ( !vars.contains( arc.to ) ) {
                vars.add( arc.to )
            }
        }
        return vars
    }

    boolean contains( Arc arc ) {
        arcs.find {
            it.from.id == arc.from.id &&
                    it.to.id   == it.from.id
        } != null
    }
    
    Collection<Variable> getParentsOf( Variable child ) {
        arcs.findAll { it.to.id == child.id }*.from
    }

}
