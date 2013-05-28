/*
 * Copyright 2012 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bn.elicitator.auth

import grails.plugin.springsecurity.oauth.FacebookOAuthToken
import grails.plugin.springsecurity.oauth.GoogleOAuthToken
import grails.plugin.springsecurity.oauth.OAuthToken
import grails.plugin.springsecurity.oauth.TwitterOAuthToken
import grails.plugin.springsecurity.oauth.YahooOAuthToken
import org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.savedrequest.DefaultSavedRequest

import bn.elicitator.auth.User
import bn.elicitator.auth.Role
import bn.elicitator.auth.UserRole
import bn.elicitator.auth.OAuthID

/**
 * Simple helper controller for handling OAuth authentication and integrating it
 * into Spring Security.
 */
class SpringSecurityOAuthController {

	public static final String SPRING_SECURITY_OAUTH_TOKEN = 'springSecurityOAuthToken'

	def grailsApplication
	def oauthService
	def springSecurityService

	/**
	 * This can be used as a callback for a successful OAuth authentication
	 * attempt. It logs the associated user in if he or she has an internal
	 * Spring Security account and redirects to <tt>targetUri</tt> (provided as a URL
	 * parameter or in the session). Otherwise it redirects to a URL for
	 * linking OAuth identities to Spring Security accounts. The application must implement
	 * the page and provide the associated URL via the <tt>oauth.registration.askToLinkOrCreateAccountUri</tt>
	 * configuration setting.
	 */
	def onSuccess = {
		// Validate the 'provider' URL. Any errors here are either misconfiguration
		// or web crawlers (or malicious users).
		if (!params.provider) {
			renderError 400, "The Spring Security OAuth callback URL must include the 'provider' URL parameter."
			return
		}

		def sessionKey = oauthService.findSessionKeyForAccessToken(params.provider)
		if (!session[sessionKey]) {
			renderError 500, "No OAuth token in the session for provider '${params.provider}'!"
			return
		}

		// Create the relevant authentication token and attempt to log in.
		OAuthToken oAuthToken = createAuthToken(params.provider, session[sessionKey])

		if (oAuthToken.principal instanceof GrailsUser) {
			authenticateAndRedirect(oAuthToken, defaultTargetUrl)
		} else {

			if ( delphiService.hasPreviousPhase ) {
				redirect( controller: 'contentView', params: [ page: ContentPage.CANT_REGISTER_THIS_ROUND ] )
				return
			}

			// This OAuth account hasn't been registered against an internal
			// account yet. Give the oAuthID the opportunity to create a new
			// internal account or link to an existing one.
			session[SPRING_SECURITY_OAUTH_TOKEN] = oAuthToken

			def redirectUrl = SpringSecurityUtils.securityConfig.oauth.registration.askToLinkOrCreateAccountUri
			assert redirectUrl, "grails.plugins.springsecurity.oauth.registration.askToLinkOrCreateAccountUri" +
					" configuration option must be set!"
			log.debug "Redirecting to askToLinkOrCreateAccountUri: ${redirectUrl}"
			redirect(redirectUrl instanceof Map ? redirectUrl : [uri: redirectUrl])
		}
	}

	def onFailure = {
		authenticateAndRedirect(null, defaultTargetUrl)
	}

	def getUsername(OAuthToken token) {
		token.providerName + "_" + token.socialId
	}

	def askToLinkOrCreateAccount = {
		OAuthToken token = (OAuthToken) session[SPRING_SECURITY_OAUTH_TOKEN]
		String username = getUsername(token)
		Map<String, String> parts = token.credentials.split( "&" ).collectEntries { it.split( '=' ) as List }
		String password = parts[ 'access_token' ]
		create(token, username, password)
	}

	def createAccount = { OAuthCreateAccountCommand command ->
		OAuthToken oAuthToken = (OAuthToken) session[SPRING_SECURITY_OAUTH_TOKEN]
		assert oAuthToken, "There is no auth token in the session!"

		if (request.post && command.validate()) {
			create(oAuthToken, command.username, command.password1)
		}

		render view: 'askToLinkOrCreateAccount', model: [createAccountCommand: command]
	}

	private void create(OAuthToken token, String username, String password) {
		if (!springSecurityService.loggedIn) {
			def config = SpringSecurityUtils.securityConfig

			boolean created = User.withTransaction { status ->
				User user = new User(username: username, password: password, enabled: true)
				user.addToOAuthIDs(provider: token.providerName, accessToken: token.socialId, user: user)

				updateUser(user, token)

				if (!user.validate() || !user.save()) {
					status.setRollbackOnly()
					return false
				}

				for (roleName in config.oauth.registration.roleNames) {
					UserRole.create user, Role.findByName(roleName)
				}

				token = updateOAuthToken(token, user)
				return true
			}

			if (created) {
				authenticateAndRedirect(token, defaultTargetUrl)
			}
		}
	}

	// utils

	protected renderError(code, msg) {
		log.error msg + " (returning ${code})"
		render status: code, text: msg
	}

	protected OAuthToken createAuthToken(providerName, scribeToken) {
		def providerService = grailsApplication.mainContext.getBean("${providerName}SpringSecurityOAuthService")
		OAuthToken oAuthToken = providerService.createAuthToken(scribeToken)

		def oAuthID = OAuthID.findByProviderAndAccessToken(oAuthToken.providerName, oAuthToken.socialId)
		if (oAuthID) {
			updateOAuthToken(oAuthToken, oAuthID.user)
		}

		return oAuthToken
	}

	protected OAuthToken updateOAuthToken(OAuthToken oAuthToken, User user) {
		def conf = SpringSecurityUtils.securityConfig

		// user

		String usernamePropertyName = conf.userLookup.usernamePropertyName
		String passwordPropertyName = conf.userLookup.passwordPropertyName
		String enabledPropertyName = conf.userLookup.enabledPropertyName
		String accountExpiredPropertyName = conf.userLookup.accountExpiredPropertyName
		String accountLockedPropertyName = conf.userLookup.accountLockedPropertyName
		String passwordExpiredPropertyName = conf.userLookup.passwordExpiredPropertyName

		String username = user."${usernamePropertyName}"
		String password = user."${passwordPropertyName}"
		boolean enabled = enabledPropertyName ? user."${enabledPropertyName}" : true
		boolean accountExpired = accountExpiredPropertyName ? user."${accountExpiredPropertyName}" : false
		boolean accountLocked = accountLockedPropertyName ? user."${accountLockedPropertyName}" : false
		boolean passwordExpired = passwordExpiredPropertyName ? user."${passwordExpiredPropertyName}" : false

		// authorities

		String authoritiesPropertyName = conf.userLookup.authoritiesPropertyName
		String authorityPropertyName = conf.authority.nameField
		Collection<?> userAuthorities = user."${authoritiesPropertyName}"
		def authorities = userAuthorities.collect { new GrantedAuthorityImpl(it."${authorityPropertyName}") }

		oAuthToken.principal = new GrailsUser(username, password, enabled, !accountExpired, !passwordExpired,
				!accountLocked, authorities ?: GormUserDetailsService.NO_ROLES, user.id)
		oAuthToken.authorities = authorities
		oAuthToken.authenticated = true

		return oAuthToken
	}


	private def updateUser(User user, OAuthToken oAuthToken) {
		if (!user.validate()) {
			return
		}

		if (oAuthToken instanceof TwitterOAuthToken) {
			TwitterOAuthToken twitterOAuthToken = (TwitterOAuthToken) oAuthToken

			if (!user.username) {
				user.username = twitterOAuthToken.twitterProfile.screenName
				if (!user.validate()) {
					user.username = null
				}
			}

		} else if (oAuthToken instanceof FacebookOAuthToken) {
			FacebookOAuthToken facebookOAuthToken = (FacebookOAuthToken) oAuthToken

			if (!user.username) {
				user.username = facebookOAuthToken.facebookProfile.username
				if (!user.validate()) {
					user.username = null
				}
			}

			if (!user.realName || user.realName == user.username) {
				user.realName = facebookOAuthToken.facebookProfile.firstName
				if (!user.validate()) {
					user.realName = null
				}
			}

			if (!user.email) {
				user.email = facebookOAuthToken.facebookProfile.email
				if (!user.validate()) {
					user.email = null
				}
			}

		} else if (oAuthToken instanceof GoogleOAuthToken) {
			GoogleOAuthToken googleOAuthToken = (GoogleOAuthToken) oAuthToken

			if (!user.email) {
				user.email = googleOAuthToken.email
				if (!user.validate()) {
					user.email = null
				}
			}
		} else if (oAuthToken instanceof YahooOAuthToken) {
			YahooOAuthToken yahooOAuthToken = (YahooOAuthToken) oAuthToken

			if (!user.username) {
				user.username = yahooOAuthToken.profile.nickname
				if (!user.validate()) {
					user.username = null
				}
			}

		}
	}


	protected Map getDefaultTargetUrl() {
		def config = SpringSecurityUtils.securityConfig
		def savedRequest = session[DefaultSavedRequest.SPRING_SECURITY_SAVED_REQUEST_KEY]
		def defaultUrlOnNull = '/'

		if (savedRequest && !config.successHandler.alwaysUseDefault) {
			return [url: (savedRequest.redirectUrl ?: defaultUrlOnNull)]
		} else {
			return [uri: (config.successHandler.defaultTargetUrl ?: defaultUrlOnNull)]
		}
	}

	protected void authenticateAndRedirect(OAuthToken oAuthToken, redirectUrl) {
		session.removeAttribute SPRING_SECURITY_OAUTH_TOKEN

		SecurityContextHolder.context.authentication = oAuthToken
		redirect(redirectUrl instanceof Map ? redirectUrl : [uri: redirectUrl])
	}

}

class OAuthCreateAccountCommand {

	String username
	String password1
	String password2

	static constraints = {
		username blank: false, validator: { String username, command ->
			User.withNewSession { session ->
				if (username && User.countByUsername(username)) {
					return 'OAuthCreateAccountCommand.username.error.unique'
				}
			}
		}
		password1 blank: false, minSize: 8, maxSize: 64, validator: { password1, command ->
			if (command.username && command.username.equals(password1)) {
				return 'OAuthCreateAccountCommand.password.error.username'
			}

			if (password1 && password1.length() >= 8 && password1.length() <= 64 &&
					(!password1.matches('^.*\\p{Alpha}.*$') ||
							!password1.matches('^.*\\p{Digit}.*$') ||
							!password1.matches('^.*[!@#$%^&].*$'))) {
				return 'OAuthCreateAccountCommand.password.error.strength'
			}
		}
		password2 nullable: true, blank: true, validator: { password2, command ->
			if (command.password1 != password2) {
				return 'OAuthCreateAccountCommand.password.error.mismatch'
			}
		}
	}
}

class OAuthLinkAccountCommand {

	String username
	String password

	static constraints = {
		username blank: false
		password blank: false
	}

}
