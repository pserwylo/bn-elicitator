package bn.elicitator.output

import bn.elicitator.Cpt
import bn.elicitator.Variable
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild

import java.awt.*
import java.util.List

abstract class SerializedBnGraph<T extends SerializedBnNode> extends OutputGraph {

	// Use this to calculate the layout of our nodes (it has better algorithms than most BN software...
	GraphvizOutputGraph graphvizOutput = new GraphvizOutputGraph()

	Map<Variable,VariableFamily> variableFamilies = [:]

	Map<Variable,Cpt> cpts = [:]

	protected T createNode() { return new T() }

	private void putVar( Variable var ) {
		if ( !variableFamilies.containsKey( var ) ) {
			variableFamilies.put( var, new VariableFamily( variable: var ) )
		}
	}

	List<T> getNodes() {

		List<Variable> variableOrder = calcVariableOrder()

		List<T> nodes = variableOrder.collect { variable ->
			VariableFamily family = variableFamilies.get( variable )
			Cpt cpt               = cpts.containsKey( variable ) ? cpts[ variable ] : null
			T n = createNode()
			n.family = family
			n.cpt = cpt;
			return n
		}

		String graphvizSvg = graphvizOutput.generateGraph()
		GPathResult svg = new XmlSlurper(false, true, true).parseText( graphvizSvg )
		Double totalHeight = svg.@height.toString()[ 0..-3 ] as Double // Remove the "pt" from the height value.
		svg.depthFirst().toList().each { NodeChild node ->
			if ( node.@class == "node" ) {
				String label = node.title
				T n = nodes.find { it.family.variable.label == label }
				n.location = new Point(
						x : ( node.ellipse.@cx.toString() as Double ) + ( node.ellipse.@rx.toString() as Double ) / 2,
						y : ( node.ellipse.@cy.toString() as Double ) + ( node.ellipse.@ry.toString() as Double ) / 2 + totalHeight
				)
			}
		}

		return nodes

	}

	@Override
	void addCpt( Cpt cpt ) {
		cpts.put( cpt.variable, cpt )
	}

	@Override
	void addEdge(Variable parent, Variable child, double strength, int numUsers, int totalUsers) {
		putVar( parent )
		putVar( child  )
		variableFamilies.get( child  ).parents.add( parent )
		variableFamilies.get( parent ).children.add( child )
		graphvizOutput.addEdge(parent, child, strength, numUsers, totalUsers)
	}

	/**
	 * Uses the Kahn (1962) algorithm explained at https://en.wikipedia.org/wiki/Topological_sorting.
	 */
	List<Variable> calcVariableOrder() {

		Map<Variable,VariableFamily> allVariableFamilies = [:]
		variableFamilies.each { entry ->
			VariableFamily familyCopy = new VariableFamily( variable : entry.key )
			familyCopy.parents.addAll( entry.value.parents )
			familyCopy.children.addAll( entry.value.children )
			allVariableFamilies.put( entry.key, familyCopy )
		}

		List<Variable> L = []
		List<Variable> S = allVariableFamilies.findAll { entry -> entry.value.parents.isEmpty() }.collect { entry -> entry.key  }
		while ( !S.isEmpty() ) {
			Variable n = S.pop()
			L.add( n )
			VariableFamily nFamily = allVariableFamilies.get( n )
			for ( Variable m in nFamily.children ) {
				VariableFamily mFamily = allVariableFamilies.get( m )
				mFamily.parents.remove( n )
				if ( mFamily.parents.isEmpty() ) {
					S.add( m )
				}
			}
		}

		return L
	}


}
