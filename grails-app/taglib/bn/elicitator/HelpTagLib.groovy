package bn.elicitator

class HelpTagLib {

	static namespace = "h"

	private static String generateHash( title, message ) {
		title = title.trim()
		message = message.trim()
		return DigestUtils.md5Hex( title + message )
	}

	private static Boolean hasRead( title, message ) {
		String hash = generateHash( title, message )
		return HelpRead.findByMessageHashAndReadBy( hash, ShiroUser.current ) != null
	}

	/**
	 * @attrs index    REQUIRED Relative to other help messages on the page, what order do you want this one to show up?
	 * @attrs title    The title string for the message. Shown above the body and more prominently.
	 * @attrs for      The ID of a DOM element on the screen.
	 * @attrs location Either to the "left" or "right" of the DOM element specified by the "for" attribute.
	 * @attrs width    Sets the max-width  proeprty on the overlay (therefore, you should append units to this to, like "500px")
	 * @attrs height   Sets the max-height property on the overlay (therefore, you should append units to this to, like "500px")
	 * @attrs persist  Don't remove the message once viewed. Persist for each page load.
	 */
	def help = { attrs, body ->

		def index       = attrs.index

		String helpFor  = ""
		String title    = ""
		String location = ""
		String width    = ""
		String height   = ""
		def    persist  = false

		if ( attrs.containsKey( "for"      ) ) helpFor  = attrs.for
		if ( attrs.containsKey( "title"    ) ) title    = attrs.title
		if ( attrs.containsKey( "location" ) ) location = attrs.location
		if ( attrs.containsKey( "width"    ) ) width    = attrs.width
		if ( attrs.containsKey( "height"   ) ) height   = attrs.height
		if ( attrs.containsKey( "persist"  ) ) persist  = attrs.persist

		if ( !params.containsKey( "showHelp" ) && !persist && hasRead( title, body() ) ) {
			return;
		}

		String pointDirectionClass = ""
		if ( location == "right" ) {
			pointDirectionClass = "direction-left"
		} else if ( location == "left" ) {
			pointDirectionClass = "direction-right"
		} else {
			location = ""
		}

		String style = "style='${width ? "max-width: $width;" : ''} ${height ? "max-height: $height;" : ''}'"

		String globalClass = ( helpFor == "" ) ? "global" : ""

		out << """
			<div class='help-overlay $globalClass $pointDirectionClass' $style>
				<input type='hidden' name='index' value='$index' />
				<input type='hidden' name='hash' value='${generateHash( title, body() )}' />
				${helpFor  == "" ? "" : "<input type='hidden' name='for' value='$helpFor' />"}
				${location == "" ? "" : "<input type='hidden' name='location' value='$location' />"}
				${location == "" ? "" : "<div class='pointer'></div>"}
				<div class='title'>$title</div>
				<div class='body'>${body()}</div>
				<div class='button-wrapper'>
					<button class='dismiss'>Dismiss</button>
				</div>
			</div>
			"""
	}

}


import org.apache.commons.codec.digest.DigestUtils
