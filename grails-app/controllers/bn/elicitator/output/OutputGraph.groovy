package bn.elicitator.output

import bn.elicitator.Variable

abstract class OutputGraph {
	abstract void addEdge( Variable parent, Variable child, Float strength );
	abstract String generateGraph();
	abstract String getContentType();
	abstract String getFileExtension();
}
