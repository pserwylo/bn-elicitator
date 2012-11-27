package bn.elicitator

class ParentClassTagLib {

	/**
	 * @attr variables REQUIRED
	 * @attr matrix REQUIRED
	 * @attr x REQUIRED
	 * @attr y REQUIRED
	 */
	def hasParentClass = { attrs, body ->

		def variables = attrs.variables
		def matrix = attrs.matrix
		Integer x = attrs.x
		Integer y = attrs.y

		String parent = "";
		if ( x > 0 )
		{
			parent = "has-parent";
			for ( int i = 0; i < x; i ++ )
			{
				parent += "-" + variables[ i ].label + "-" + matrix[ y ][ i ]
			}
		}

		out << parent

	}

}
