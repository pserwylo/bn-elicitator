package bn.elicitator

class TooltipTagLib {

	static namespace = 'help'

	/**
	 * @attr target REQUIRED
	 * @attr title
	 * @attr index REQUIRED
	 */
	def help = { attrs, body ->

		if ( !attrs.containsKey( 'target' ) ) {
			throwTagError( "Tag [help] missing required attribute [target]." )
		}

		if ( !attrs.containsKey( 'index' ) ) {
			throwTagError( "Tag [help] missing required attribute [index]." )
		}

		String target = attrs.target
		int index     = attrs.index as Integer
		String title  = null

		if ( attrs.containsKey( 'title' ) ) {
			title = attrs.title
		}

		String bodyText = body()

		bodyText += """
			<div class='button-wrapper'>
				<button type='button' onclick='bn.help.next( $index )'>Dismiss</button>
			</div>
		"""

		g.javascript() {
			"""
			\$( document ).ready( function() {
				bn.help.queue(
					$index,
					'$target',
					${title ? '"' + title.encodeAsJavaScript() + '"': 'null'},
					'${bodyText.encodeAsJavaScript()}' );
				});
			"""
		}

	}

}
