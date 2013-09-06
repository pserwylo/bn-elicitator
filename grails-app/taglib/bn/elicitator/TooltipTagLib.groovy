package bn.elicitator

class TooltipTagLib {

	static namespace = 'help'

	def userService

	private Boolean hasRead( String uniqueId ) {
		HelpRead.countByUniqueIdAndReadBy( uniqueId, userService.current ) > 0
	}

	/**
	 * The index is incremented by 100, so that we can have values such as -50. This allows the outer layout sitemesh
	 * to have help items, as well as the internal one. The reason we don't crank it up to 100000 or something silly is
	 * because we are creating a JS array of of this size (at least).
	 *
	 * @attr targetId REQUIRED
	 * @attr title
	 * @attr uniqueId REQUIRED
	 * @attr index REQUIRED Use indexes between -100 and -1 for higher priorities - e.g. main layouts.
	 */
	def help = { attrs, body ->

		if ( !attrs.containsKey( 'uniqueId' ) ) {
			throwTagError( "Tag [help] missing required attribute [uniqueId]." )
		}

		if ( !attrs.containsKey( 'index' ) ) {
			throwTagError( "Tag [help] missing required attribute [index]." )
		}

		String uniqueId = attrs.uniqueId
		boolean hasRead = hasRead( uniqueId )
		int index = ( attrs.index as Integer ) + 100

		boolean showAnyway = params.containsKey( 'showHelp' ) || ( index >= 100 && params.containsKey( 'showMostHelp' ) )
		if ( hasRead && !showAnyway ) {
			return
		}

		if ( !attrs.containsKey( 'targetId' ) ) {
			throwTagError( "Tag [help] missing required attribute [targetId]." )
		}

		String targetId = attrs.targetId
		String title    = null
		String bodyText = body()

		if ( attrs.containsKey( 'title' ) ) {
			title = attrs.title
		}

		g.javascript() {
			"""
			\$( document ).ready( function() {
				bn.help.queue(
					$index,
					'$uniqueId',
					'$targetId',
					${title ? '"' + title.encodeAsJavaScript() + '"': 'null'},
					'${bodyText.encodeAsJavaScript()}' );
				});
			"""
		}

	}

}
