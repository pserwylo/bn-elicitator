package bn.elicitator

import bn.elicitator.auth.User
import grails.plugins.springsecurity.ui.RegisterCommand


class RegisterController extends grails.plugins.springsecurity.ui.RegisterController {

	public RegisterController() {
		grails.plugins.springsecurity.ui.RegisterController.metaClass.static.checkPasswordRegex = { String password, command ->
			password
		}
	}

	def index = {
		if ( params.email ) {
			def chained = flash.chainedParams ?: [:]
			chained.put( "email", params.email )
			flash.chainedParams = chained
		}
		super.index()
	}

	def register = { RegisterCommand command ->
		command.username = command.email
		super.register( command )
	}
}