package bn.elicitator

class ErrorController {

	EmailService emailService

	def index() {
		handleError( 500, request.exception )
	}

	def notFound() {
		handleError( 404 )
	}

	def invalidInput() {
		handleError( 400 )
	}

	private void handleError( Integer errorCode, exception = null ) {
		String       title   = message( code: "general.error-title", args: [ errorCode.toString() ] )
		String       message = message( code: "general.error-message" )
		ErrorDetails error   = new ErrorDetails( title: title, message: message, exception: exception )
		emailService.sendError( error )
		renderError( error )
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
