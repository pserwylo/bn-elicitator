package bn.elicitator

class ErrorController {

	def index() {}

	def notFound() {

		def title = "Not Found"
		def message = params?.toString()
		def error = new ErrorDetails( title: title, message: message )
		renderError( error )

	}

	def serverError( attr ) {

		def title = "System error"
		def message = params?.toString() + "\n\n" + attr?.toString()
		def error = new ErrorDetails( title: title, message: message )
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
	String title
	String message
}