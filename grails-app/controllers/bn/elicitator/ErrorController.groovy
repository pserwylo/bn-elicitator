package bn.elicitator

import grails.util.Environment


// Anyone can see errors, so no securing required.
class ErrorController {

	EmailService emailService

	def index() {
		if ( Environment.current.equals( Environment.DEVELOPMENT ) ) {
			render renderException( exception : request.exception )
		} else {
			handleError( 500, request.exception )
		}
	}

	private void handleError( Integer errorCode, exception = null ) {
		try {
			String       title   = message( code: "general.error-title", args: [ errorCode.toString() ] )
			String       message = message( code: "general.error-message" )
			ErrorDetails error   = new ErrorDetails( title: title, message: message, exception: exception )
			renderError( error )
			emailService.sendErrorEmail( error )
		} catch ( Exception e ) {
			render "Error occurred"
			render "<div style='display: none;'>"
			g.renderException( exception: e )
			render "</div>"
		}
	}

	private void renderError( ErrorDetails error ) {
		render(
			view  : 'error',
			model : [ error : error ]
		)
	}

}

class ErrorDetails {
	String    title
	String    message
	Exception exception
}
