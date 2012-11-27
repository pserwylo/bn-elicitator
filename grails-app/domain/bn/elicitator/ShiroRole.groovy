package bn.elicitator

/**
 * Used by the Shiro authentication/authorization plugin to keep track of various permissions.
 *
 *  - {@link ShiroRole#getExpert()} users are participants in the study.
 *
 *  - {@link ShiroRole#getConsented()} originally, expert users will only be able to see the explanatory statement until
 *  they have consented via that screen. Then they are able to actually participate in the study.
 *
 *  - {@link ShiroRole#getAdmin()} users are obviously administrators of the system, who can see where everybody is up
 *  to, and also advance the delphi phase of the study.
 *
 * @see ShiroUser
 */
class ShiroRole {

	static belongsTo = ShiroUser

	static hasMany = [
		users: ShiroUser, permissions: String
	]

	static constraints = {
		name(nullable: false, blank: false, unique: true)
	}

    String name

	static final EXPERT = "expert"
	static final CONSENTED = "consented"
	static final ADMIN = "admin"

	static ShiroRole getExpert() { findByName( EXPERT ) }
	static ShiroRole getConsented() { findByName( CONSENTED ) }
	static ShiroRole getAdmin() { findByName( ADMIN ) }

}
