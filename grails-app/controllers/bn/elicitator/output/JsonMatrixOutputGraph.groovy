package bn.elicitator.output

import grails.converters.JSON

class JsonMatrixOutputGraph extends MatrixOutputGraph {

	private List<List<Float>> generateCountMatrix() {
		allVariables.collect { parent -> allVariables.collect { child -> getCount( parent, child ) } }
	}

	@Override
	String generateGraph() {

		def graph = [
			variables: allVariables*.label,
			matrix: generateCountMatrix()
		]

		return graph as JSON
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
