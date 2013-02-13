package bn.elicitator.output

import bn.elicitator.Variable

/**
 * The parents and children of a variable are represented in this class.
 */
class VariableFamily {

	List<Variable> parents  = []
	Variable variable       = null
	List<Variable> children = []
}
