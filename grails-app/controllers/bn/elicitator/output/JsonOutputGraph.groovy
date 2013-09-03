package bn.elicitator.output

import bn.elicitator.Variable
import grails.converters.JSON

class JsonOutputGraph extends OutputGraph {

	private Integer totalEdges = 0
	Set<Variable> variables = []

	@Override
	void addEdge(Variable parent, Variable child, double strength, int numUsers, int totalUsers) {
		totalEdges ++
		variables.add( parent )
		variables.add( child )
	}

	@Override
	String generateGraph() {
		return [ totalNodes: variables.size(), totalEdges : totalEdges ] as JSON
	}

	@Override
	String getContentType() {
		return "application/json"
	}

	@Override
	String getFileExtension() {
		return ".json"
	}
}
