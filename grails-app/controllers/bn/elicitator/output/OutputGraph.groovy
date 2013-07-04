package bn.elicitator.output

import bn.elicitator.Variable

abstract class OutputGraph {

	private List<Variable> allVariables = []

	public final void setAllVariables( List<Variable> value ) { this.allVariables.addAll( value ) }
	public final List<Variable> getAllVariables() { return allVariables }

	abstract void addEdge(Variable parent, Variable child, Float strength, Integer numUsers, Integer totalUsers);
	abstract String generateGraph();
	abstract String getContentType();
	abstract String getFileExtension();
}
