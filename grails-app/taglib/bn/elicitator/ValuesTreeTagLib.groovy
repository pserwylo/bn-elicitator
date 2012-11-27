package bn.elicitator

class ValuesTreeTagLib {

	/**
	 * @attr values REQUIRED
	 */
	def valuesTree = { attrs, body ->

		def values = attrs.values
		out << "<ul>"
		renderBranch( values, out )
		out << "<ul/>"

	}

	private renderBranch( List items, out ) {

		for ( def item in items )
		{
			out << "<li>"
			out << item.label

			if ( item.children?.size() )
			{
				out << "<ul>"

				for ( def child in item.children )
				{
					renderBranch( child, out )
				}

				out << "</ul>"
			}
			out << "</li>"
		}
	}
}
