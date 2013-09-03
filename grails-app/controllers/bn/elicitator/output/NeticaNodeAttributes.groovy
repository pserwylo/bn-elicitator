package bn.elicitator.output

import bn.elicitator.Cpt
import bn.elicitator.State
import bn.elicitator.Variable

import java.awt.Point
import java.text.DecimalFormat

class NeticaNodeAttributes {

	private Map<String,String> attributes = [:]

	void addAttribute( String key, String value ) {
		attributes.put( key, value + ";" )
	}

	void addQuotedAttribute( String key, String value ) {
		attributes.put( key, '"' + value + '";' )
	}

	void addListAttribute( String key, List<String> values ) {
		addAttribute( key, makeList( values ) )
	}

	String makeList( List<String> values ) {
		"(" + values.join( ", " ) + ")"
	}

	String toString() {
		attributes.collect { "\t$it.key = $it.value\n" }.join( "" )
	}
}
