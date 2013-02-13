package bn.elicitator.output

abstract class GraphvizItem {

	private Map<String,String> attributes = [:]

	protected addAttribute( String key, String value ) {
		attributes.put( key, value )
	}

	protected addQuotedAttribute( String key, String value ) {
		addAttribute( key, '"' + value + '"' )
	}

	protected String getAttributesString() {
		return "[" + attributes.collect { it.key + "=" + it.value }.join( "," ) + "]"
	}

}
