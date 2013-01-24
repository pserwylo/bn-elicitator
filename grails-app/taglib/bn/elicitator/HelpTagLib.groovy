package bn.elicitator

class HelpTagLib {

	static namespace = "h"

	/**
	 * @attrs for
	 * @attrs title
	 * @attrs location
	 * @attrs width
	 * @attrs height
	 */
	def help = { attrs, body ->

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

		String pointDirectionClass = ""
		if ( location == "right" ) {
			pointDirectionClass = "direction-left"
		} else if ( location == "left" ) {
			pointDirectionClass = "direction-right"
		} else {
			location = ""
		}

		String style = "style='${width ? "width: $width;" : ''} ${height ? "height: $height;" : ''}'"

		out << """
				<div class='help-overlay $pointDirectionClass' $style>
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
