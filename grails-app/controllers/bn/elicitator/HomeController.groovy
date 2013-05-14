package bn.elicitator

import bn.elicitator.auth.User

/**
 * Decides where to send somebody based on the logged in (or not) users status.
 * I couldn't be stuffed putting this logic in UrlMappings or filters or whatever.
 */
class HomeController {

	UserService userService

	def index() { 

		User user = userService.current
		if ( !user ) {
			forward( controller: 'contentEdit', params : [ page : ContentPage.HOME ] )
		} else if ( user.hasConsented ) {
			forward( controller: 'elicit' )
		} else {
			forward( controller: 'explain' )
		}

	}
}
