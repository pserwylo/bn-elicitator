package bn.elicitator.output

import bn.elicitator.Variable

abstract class MatrixOutputGraph extends OutputGraph {

	protected Map<String, Float> matrix = [:]

	protected static String generateKey( Variable parent, Variable child ) {
		parent.label + "-" + child.label
	}

	protected Float getCount( Variable parent, Variable child ) {
		String key = generateKey( parent, child )
		return matrix.containsKey( key ) ? matrix.get( key ) : 0
	}

	@Override
	void addEdge(Variable parent, Variable child, double strength, int numUsers, int totalUsers) {
		matrix.put( generateKey( parent, child ), strength )
	}

}
