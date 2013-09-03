package bn.elicitator.output

import bn.elicitator.Variable

class CsvOutputGraph extends OutputGraph {

	StringBuilder rows = new StringBuilder()

	@Override
	void addEdge(Variable parent, Variable child, double strength, int numUsers, int totalUsers) {
		rows.append(numUsers)
		rows.append(',')
		rows.append(totalUsers)
		rows.append(',')
		rows.append(strength)
		rows.append(',')
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
