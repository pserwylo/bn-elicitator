package bn.elicitator.output

import bn.elicitator.Variable

public class GraphvizEdge extends GraphvizItem{
	Variable parent
	Variable child
	Float strength

	String getColour() {
		String hex = "123456789ab"
		Integer i = strength * ( hex.length() - 1 )
		String num = ( [ hex.charAt( i ) ] * 6 ).join()
		return "#" + num
	}

	String toString() {
		addAttribute( "weight", ((Integer)( strength * 10 )).toString() )
		addQuotedAttribute( "color", colour )
		return "\t$parent.label -> $child.label $attributesString\n"
	}
}
