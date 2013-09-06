package bn.elicitator

class TooltipTagLib {

	static namespace = 'help'

	/**
	 * The index is incremented by 100, so that we can have values such as -50. This allows the outer layout sitemesh
	 * to have help items, as well as the internal one. The reason we don't crank it up to 100000 or something silly is
	 * because we are creating a JS array of of this size (at least).
	 *
	 * @attr targetId REQUIRED
	 * @attr title
	 * @attr index REQUIRED Use indexes between -100 and -1 for higher priorities - e.g. main layouts.
	 */
	def help = { attrs, body ->

		if ( !attrs.containsKey( 'targetId' ) ) {
			throwTagError( "Tag [help] missing required attribute [targetId]." )
		}

		if ( !attrs.containsKey( 'index' ) ) {
			throwTagError( "Tag [help] missing required attribute [index]." )
		}

		String targetId = attrs.targetId
		int index     = attrs.index as Integer
		String title  = null

		if ( attrs.containsKey( 'title' ) ) {
			title = attrs.title
		}

		index += 100

		String bodyText = body()

		g.javascript() {
			"""
			\$( document ).ready( function() {
				bn.help.queue(
					$index,
					'$targetId',
					${title ? '"' + title.encodeAsJavaScript() + '"': 'null'},
					'${bodyText.encodeAsJavaScript()}' );
				});
			"""
		}

	}

}
