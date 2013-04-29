package bn.elicitator.auth

import bn.elicitator.events.LoginEvent
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class User {

	transient springSecurityService

	static constraints = {
		consentedDate nullable: true, blank: true

		// Need to allow null so that validate() works during oauth stuff...
		email         nullable: true, blank: true
		realName      nullable: true, blank: true
		password      nullable: true, blank: true
	}

	static mapping = {
		table "shiro_user"
		username blank: false, unique: true
		password column: "password_hash"
	}

	static hasMany = [ oAuthIDs: OAuthID ]

	String realName
	String email
	String username
	String password

	private boolean enabled = true
	boolean getEnabled() { enabled }
	void    setEnabled( boolean value ) {}

	private boolean accountExpired = false
	boolean getAccountExpired() { accountExpired }
	void    setAccountExpired( boolean value ) {}

	private boolean accountLocked = false
	boolean getAccountLocked() { accountLocked }
	void    setAccountLocked( boolean value ) {}

	private boolean passwordExpired = false
	boolean getPasswordExpired() { passwordExpired }
	void    setPasswordExpired( boolean value ) {}

	boolean hasConsented = false
	Date consentedDate = null


	Set<Role> getRoles() {
		UserRole.findAllByUser( this )*.role as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}

	void setUsername( String value ) {
		this.username = value.replace( ' ', '_' )
	}

	Date getLastLoginDate() {
		LoginEvent.findByUser( this )?.date
	}

}
