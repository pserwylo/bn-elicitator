package bn.elicitator.output

import bn.elicitator.Cpt
import bn.elicitator.State
import bn.elicitator.Variable

import java.awt.Point
import java.text.DecimalFormat

public class NeticaNode {

	VariableFamily family
	Cpt            cpt
	Point          location

	private NeticaNodeAttributes attributes       = new NeticaNodeAttributes()
	private NeticaNodeAttributes visualAttributes = new NeticaNodeAttributes()
	private NeticaNodeAttributes userAttributes   = new NeticaNodeAttributes()

	def getLongestStateName() {
		int longestStateName = 0
		family.variable.states.each {
			if ( it.label.size() > longestStateName ) {
				longestStateName = it.label.size()
			}
		}

		family.parents.each { Variable parent ->
			parent.states.each {
				if ( it.label.size() > longestStateName ) {
					longestStateName = it.label.size()
				}
			}
		}
		return longestStateName
	}

	def padString( String string, int length ) {
		while ( string.size() < length ) {
			string = string + " "
		}
		string
	}

	def generateProbs() {

		if ( !cpt ) {
			return "()"
		}

		String formatString = "#.######"
		DecimalFormat format = new DecimalFormat( formatString )

		int longest = longestStateName
		if ( longest < formatString.length() ) {
			longest = formatString.length()
		}

		int maxLength = longest + 2

		String probs = "(\n"
		probs += "// "
		family.variable.states.each { probs += padString( it.label, maxLength ) }
		family.parents*.label.each  { probs += padString( it, maxLength ) }
		probs += "\n"

		// If I don't reverse the parents, then I end up with it doing a depth first combination.
		// By doing this, I am forcing it to end up as a breadth first search.
		def parentConfigs = family.parents.reverse()*.states.combinations().collect { it.reverse() }

		parentConfigs.eachWithIndex { List<State> parentConfig, int i ->
			boolean lastRow = ( i == ( parentConfigs.size() - 1 ) )

			family.variable.states.eachWithIndex { State variableState, int j ->
				boolean lastCol = ( j == ( family.variable.states.size() - 1 ) )

				double probability = cpt.getProbabilityFor( variableState, parentConfig )
				String valueString = format.format( probability )
				if ( !lastCol ) {
					valueString += ","
				} else{
					valueString += lastRow ? ");" : ","
				}

				probs += padString( valueString, maxLength )
			}

			probs += "// "
			parentConfig*.label.each { String parentStateLabel ->
				probs += padString( parentStateLabel, maxLength )
			}

			if ( !lastRow ) {
				probs += "\n"
			}
		}

		return probs

	}

	String toString() {

		List<String> states = family.variable.states?.size() > 0 ? family.variable.states*.label : [ "True", "False" ]

		def probs = generateProbs()

		attributes.addAttribute( "kind",     "NATURE" )
		attributes.addAttribute( "discrete", "TRUE"   )
		attributes.addAttribute( "chance",   "CHANCE" )
		attributes.addAttribute( "probs",    probs )
		attributes.addQuotedAttribute( "title", family.variable.readableLabel )
		attributes.addListAttribute( "states",  states )
		attributes.addListAttribute( "parents", family.parents*.label )

		if ( location ) {
			visualAttributes.addListAttribute( "center", [ (int)location.x, (int)location.y ] )
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
