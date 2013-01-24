package bn.elicitator

import org.apache.commons.codec.digest.DigestUtils


class HelpTagLib {

	static namespace = "h"

	private static String generateHash( String title, String message ) {
		title = title.trim()
		message = message.trim()
		return DigestUtils.md5Hex( title + message )
	}

	private static Boolean hasRead( String title, String message ) {
		String hash = generateHash( title, message )
		return HelpRead.findByMessageHashAndReadBy( hash, ShiroUser.current ) != null
	}

	/**
	 * @attrs index    REQUIRED
	 * @attrs for
	 * @attrs title
	 * @attrs location
	 * @attrs width
	 * @attrs height
	 */
	def help = { attrs, body ->

		def index       = attrs.index

		String helpFor  = ""
		String title    = ""
		String location = ""
		String width    = ""
		String height   = ""

		if ( attrs.containsKey( "for"      ) ) helpFor  = attrs.for
		if ( attrs.containsKey( "title"    ) ) title    = attrs.title
		if ( attrs.containsKey( "location" ) ) location = attrs.location
		if ( attrs.containsKey( "width"    ) ) width    = attrs.width
		if ( attrs.containsKey( "height"   ) ) height   = attrs.height

		if ( hasRead( title, body() ) ) {
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
