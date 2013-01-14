/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bn.elicitator

import org.apache.log4j.spi.LoggingEvent
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

	Date getLastLoginDate()
	{
		LoggedEvent lastLoginEvent = LoggedEvent.findByTypeAndUser( LoggedEventType.LOGIN, this )
		return lastLoginEvent?.date
	}

	static ShiroUser getCurrent()
	{
		return ShiroUser.findByUsername( (String)SecurityUtils.subject?.principal )
	}
	
}
