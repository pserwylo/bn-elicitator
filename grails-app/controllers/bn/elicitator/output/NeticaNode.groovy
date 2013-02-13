package bn.elicitator.output

import bn.elicitator.Variable

import java.awt.Point

class NeticaNodeAttributes {

	private Map<String,String> attributes = [:]

	void addAttribute( String key, String value ) {
		attributes.put( key, value + ";" )
	}

	void addQuotedAttribute( String key, String value ) {
		attributes.put( key, '"' + value + '";' )
	}

	void addListAttribute( String key, List<String> values ) {
		addAttribute( key, "(" + values.join( ", " ) + ")" )
	}

	String toString() {
		attributes.collect { "\t$it.key = $it.value\n" }.join( "" )
	}
}

public class NeticaNode {

	VariableFamily family
	Point          location

	private NeticaNodeAttributes attributes       = new NeticaNodeAttributes()
	private NeticaNodeAttributes visualAttributes = new NeticaNodeAttributes()
	private NeticaNodeAttributes userAttributes   = new NeticaNodeAttributes()

	String toString() {
		attributes.addAttribute( "kind",     "NATURE" )
		attributes.addAttribute( "discrete", "TRUE"   )
		attributes.addAttribute( "chance",   "CHANCE" )
		attributes.addQuotedAttribute( "title",    family.variable.readableLabel )
		attributes.addListAttribute( "states", [ "True", "False" ]    )
		attributes.addListAttribute( "parents", family.parents*.label )

		if ( location ) {
			visualAttributes.addListAttribute( "center", [ location.x, location.y ] )
		}

		return """
node $family.variable.label {

${attributes}

user U1 {
${userAttributes}
};

visual V1 {
${visualAttributes}
};

};
"""

	}

}
