package bn.elicitator

import grails.plugins.springsecurity.ui.RegisterCommand
import org.codehaus.groovy.grails.plugins.springsecurity.NullSaltSource
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.springsecurity.ui.RegistrationCode


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

	def register = { BnRegisterCommand command ->

		if (command.hasErrors()) {
			render view: 'index', model: [command: command]
			return
		}

		String salt = saltSource instanceof NullSaltSource ? null : command.username
		def user = lookupUserClass().newInstance(email: command.email, username: command.username,
				accountLocked: true, enabled: true)

		RegistrationCode registrationCode = springSecurityUiService.register(user, command.password, salt)
		if (registrationCode == null || registrationCode.hasErrors()) {
			// null means problem creating the user
			flash.error = message(code: 'spring.security.ui.register.miscError')
			flash.chainedParams = params
			redirect action: 'index'
			return
		}

		String url = generateLink('verifyRegistration', [t: registrationCode.token])

		def conf = SpringSecurityUtils.securityConfig
		def body = conf.ui.register.emailBody
		if (body.contains('$')) {
			body = evaluate(body, [user: user, url: url])
		}
		mailService.sendMail {
			to command.email
			from conf.ui.register.emailFrom
			subject conf.ui.register.emailSubject
			html body.toString()
		}

		render view: 'index', model: [emailSent: true]
	}

	/**
	 * Didn't want to copy this from the base class to here, but there is a bug
	 * in the role creation, where it doesn't respect the "Authority" property
	 * from the configuration. In the config, I've set the property to "name", but
	 * here it just presumes it is "Authority".
	 */
	def verifyRegistration = {

		def conf = SpringSecurityUtils.securityConfig
		String defaultTargetUrl = conf.successHandler.defaultTargetUrl

		String token = params.t

		def registrationCode = token ? RegistrationCode.findByToken(token) : null
		if (!registrationCode) {
			flash.error = message(code: 'spring.security.ui.register.badCode')
			redirect uri: defaultTargetUrl
			return
		}

		def user
		RegistrationCode.withTransaction { status ->
			user = lookupUserClass().findByUsername(registrationCode.username)
			if (!user) {
				return
			}
			user.accountLocked = false
			user.save(flush:true)
			def UserRole = lookupUserRoleClass()
			def Role = lookupRoleClass()
			def authority = lookupAuthorityNameField()
			def Authority = authority[ 0 ].toUpperCase() + authority.substring( 1 )
			for (roleName in conf.ui.register.defaultRoleNames) {
				UserRole.create user, Role."findBy$Authority"(roleName)
			}
			registrationCode.delete()
		}

		if (!user) {
			flash.error = message(code: 'spring.security.ui.register.badCode')
			redirect uri: defaultTargetUrl
			return
		}

		springSecurityService.reauthenticate user.username

		flash.message = message(code: 'spring.security.ui.register.complete')
		redirect uri: conf.ui.register.postRegisterUrl ?: defaultTargetUrl
	}

	protected String lookupAuthorityNameField() {
		SpringSecurityUtils.securityConfig.authority.nameField
	}

}

class BnRegisterCommand {

	def grailsApplication

	String username
	String email
	String password
	String password2

	void setEmail( String value ) {
		String[] parts = value?.split( "@" )
		if ( parts?.length > 0 ) {
			username = parts[ 0 ]
		}
		this.email = value
	}

	static constraints = {
		username blank: false, nullable: false, validator: { value, command ->
			if (value) {
				def User = command.grailsApplication.getDomainClass(
						SpringSecurityUtils.securityConfig.userLookup.userDomainClassName).clazz
				if (User.findByUsername(value)) {
					return 'bnRegisterCommand.username.unique'
				}
			}
		}
		email blank: false, nullable: false, email: true
		password blank: false, nullable: false
		password2 validator: RegisterController.password2Validator
	}

}