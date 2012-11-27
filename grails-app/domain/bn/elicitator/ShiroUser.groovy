package bn.elicitator

import org.apache.shiro.*

/**
 * Used by the Shiro authentication/authorization plugin to keep track of users.
 * @see ShiroRole
 */
class ShiroUser {

	static hasMany = [
			roles: ShiroRole, permissions: String
	]

	static constraints = {
		username( nullable: false, blank: false, unique: true )
		consentedDate( nullable: true, blank: true )
	}

	/**
	 * Used for email templates (so we can personalise the email) and also a bit of candy on the screen where we can
	 * personalise the page a little with their name.
	 */
	String realName

	String email

    String username

    String passwordHash

	boolean hasConsented = false

	Date consentedDate = null

	void setUsername( String value )
	{
		this.username = value.replace( ' ', '_' )
	}
	
	static ShiroUser getCurrent()
	{
		return ShiroUser.findByUsername( (String)SecurityUtils.subject?.principal )
	}
	
}
