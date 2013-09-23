package bn.elicitator.auth

import bn.elicitator.events.LoginEvent
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class User {

	transient springSecurityService

	private static int PRIZE_YES = 1;
	private static int PRIZE_NO  = 2;

	static constraints = {
		consentedDate nullable: true, blank: true
		eligibleForPrize nullable: true

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
	Integer eligibleForPrize;
	boolean accountLocked = false

	private boolean enabled = true
	boolean getEnabled() { enabled }
	void    setEnabled( boolean value ) {}

	private boolean accountExpired = false
	boolean getAccountExpired() { accountExpired }
	void    setAccountExpired( boolean value ) {}

	private boolean passwordExpired = false
	boolean getPasswordExpired() { passwordExpired }
	void    setPasswordExpired( boolean value ) {}

	boolean hasConsented = false
	Date consentedDate = null

	boolean knowIfCanWinPrize()   { eligibleForPrize != null      }
	boolean canWinPrize()         { eligibleForPrize == PRIZE_YES }

	void makeEligibleForPrize()   { eligibleForPrize =  PRIZE_YES }
	void makeIneligibleForPrize() { eligibleForPrize =  PRIZE_NO  }


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

	void setEmail( String value ) {
		this.email = value
		if ( !this.realName && this.email ) {
			this.realName = this.email.substring( 0, this.email.indexOf( '@' ) )
		}
	}

	void setUsername( String value ) {
		this.username = value.replace( ' ', '_' )
	}

	Date getLastLoginDate() {
		LoginEvent.findByUser( this )?.date
	}

}
