package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.auth.Role

/**
 * Decides where to send somebody based on the logged in (or not) users status.
 * I couldn't be stuffed putting this logic in UrlMappings or filters or whatever.
 */
class HomeController {

	UserService userService
	DelphiService delphiService

	def index() { 

		User user = userService.current

		boolean hasConsented = user?.roles?.contains( Role.consented )

		if ( !user ) {
			forward( controller: 'contentView', params : [ page : ContentPage.HOME ] )
		} else if ( !delphiService.hasPreviousPhase ) {
			if ( !hasConsented ) {
				forward( controller: 'explain' )
			} else {
				forward( controller: 'elicit' )
			}
		} else if ( delphiService.hasPreviousPhase ) {
			if ( !hasConsented ) {
				forward( controller: 'contentView', params : [ page : ContentPage.CANT_REGISTER_THIS_ROUND ] )
			} else {
				forward( controller: 'elicit' )
			}
		}

	}
}
