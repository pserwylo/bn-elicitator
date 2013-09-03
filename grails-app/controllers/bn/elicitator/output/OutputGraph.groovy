package bn.elicitator.output

import bn.elicitator.Cpt
import bn.elicitator.Variable

abstract class OutputGraph {

	private List<Variable> allVariables = []

	public final void setAllVariables( List<Variable> value ) { this.allVariables.addAll( value ) }
	public final List<Variable> getAllVariables() { return allVariables }

	void addEdge( Variable parent, Variable child ) {
		addEdge( parent, child, 1.0, 1, 1 );
	}

	/**
	 * Does nothing by default, because most visual output styles probably don't care about this.
	 * @param cpt
	 */
	void addCpt( Cpt cpt ) {
	}

	abstract void addEdge(Variable parent, Variable child, double strength, int numUsers, int totalUsers);
	abstract String generateGraph();
	abstract String getContentType();
	abstract String getFileExtension();
}
