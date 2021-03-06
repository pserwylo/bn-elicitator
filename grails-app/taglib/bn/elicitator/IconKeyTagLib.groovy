package bn.elicitator

class IconKeyTagLib {

	static namespace = "bnIcons"

	def key = { attrs, body ->

		out << """
			<div class='icon-keys'>
				<span class="description">
					Icon descriptions:
				</span>
				<ul>
					${body()}
				</ul>
			</div>
			"""
	}

	/**
	 * The body of this tag will contain the help text to show when the user hovers over the label.
	 * @attr label    REQUIRED
	 * @attr iconPath REQUIRED
	 * @attr classes
	 * @attr display
	 */
	def icon = { attrs, body ->
		String label    = attrs.label
		String iconPath = attrs.iconPath
		String classes  = ""
		Boolean display = true

		if ( attrs.containsKey( "display" ) ) {
			display = attrs.remove( "display" )
		}

		if ( attrs.containsKey( "classes" ) ) {
			classes = attrs.remove( "classes" )
		}

		if ( display ) {
			out << """
				<li style='background: transparent url($iconPath) 0 50% no-repeat;' class='$classes'>
					$label
					${bn.tooltip( [:], body )}
				</li>
				"""
		}
	}

}
