package bn.elicitator.network

import bn.elicitator.Variable
import org.apache.commons.lang.builder.EqualsBuilder

class BnNode {

	static constraints = {
	}

	Variable variable

	boolean equals( Object obj ) {
		if ( obj == null ) {
			return false
		}
		if ( !( obj instanceof BnNode ) ) {
			return false
		}

		BnNode rhs = (BnNode)obj;
		rhs.variable.equals(variable)
	}

	String toString() {
		return variable.toString()
	}

}
