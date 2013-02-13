package bn.elicitator.output

import bn.elicitator.Variable
import bn.elicitator.VariableClass

public class GraphvizNode extends GraphvizItem {

	Variable variable
	Integer maxStringLength = 10

	String generateLabel() {
		List<String> lines = []
		List<String> words = variable.readableLabel.split( " " )
		String currentLine = ""
		words.each {
			if ( currentLine.length() + it.length() < maxStringLength ) {
				if ( currentLine.length() != 0 ) {
					currentLine += " "
				}
				currentLine += it
			} else {
				lines.add( currentLine )
				currentLine = it
			}
		}
		lines.add( currentLine )
		return lines.join( '\\n' )
	}

	String toString() {
		addQuotedAttribute( "label", generateLabel() )
		if ( variable.variableClass == VariableClass.problem ) {
			addAttribute( "style", "filled" )
			addQuotedAttribute( "color", "#333333" )
			addQuotedAttribute( "fillcolor", "#DDDDDD" )
		}
		return "\t$variable.label $attributesString\n"
	}

}
