package bn.elicitator.output

import bn.elicitator.Variable
import grails.converters.JSON

class CsvOutputGraph extends OutputGraph {

	StringBuilder rows = new StringBuilder()

	@Override
	void addEdge(Variable parent, Variable child, Float strength) {
		rows.append(parent.label)
		rows.append(',')
		rows.append(child.label)
		rows.append('\n');
	}

	@Override
	String generateGraph() {
		return rows.toString()
	}

	@Override
	String getContentType() {
		return "text/csv"
	}

	@Override
	String getFileExtension() {
		return ".csv"
	}
}
