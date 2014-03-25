package bn.elicitator

import grails.util.Environment


// Anyone can see errors, so no securing required.
class ErrorController {

}

class ErrorDetails {
	String    title
	String    message
	Exception exception
}
