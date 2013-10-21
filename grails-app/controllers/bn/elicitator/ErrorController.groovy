package bn.elicitator

import grails.util.Environment


// Anyone can see errors, so no securing required.
class ErrorController {

	EmailService emailService

	def index() {
		handleError( 500, request.exception )
	}

	def notFound() {
		flash.message = "Uh oh, we couldn't find what you were looking for."
		response.status = 404
		forward( controller: 'home' )
	}

	def jsError() {
		String message = "";
		String url     = "";
		String line    = "";

		if ( params.containsKey( "message" ) ) {
			message = params.remove( "message" );
		}

		if ( params.containsKey( "url" ) ) {
			url = params.remove( "url" );
		}

		if ( params.containsKey( "line" ) ) {
			line = params.remove( "line" );
		}

		String formattedMessage = "$message - $url @ line $line";
		ErrorDetails details = new ErrorDetails( title : "JavaScript error occurred", message : formattedMessage );
		emailService.sendErrorEmail( details )

		if ( url ) {
			redirect( url : url )
		} else {
			redirect( controller: 'home' )
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
			layout: 'main',
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
