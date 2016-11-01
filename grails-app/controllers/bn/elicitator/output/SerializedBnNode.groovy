package bn.elicitator.output

import bn.elicitator.analysis.cpt.Cpt

import java.awt.Point

abstract class SerializedBnNode {

	VariableFamily family
	Cpt            cpt
	Point          location

    abstract String toString()

}
