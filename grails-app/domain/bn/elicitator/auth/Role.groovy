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

package bn.elicitator.auth

/**
 * Used by the Spring security authentication/authorization plugin to keep track of various permissions.
 *
 *  - {@link Role#getExpert()} users are participants in the study.
 *
 *  - {@link Role#getConsented()} originally, expert users will only be able to see the explanatory statement until
 *  they have consented via that screen. Then they are able to actually participate in the study.
 *
 *  - {@link Role#getAdmin()} users are obviously administrators of the system, who can see where everybody is up
 *  to, and also advance the delphi phase of the study.
 *
 * @see User
 */
class Role {

	String name

	static mapping = {
		table "shiro_role"
		cache true
	}

	static constraints = {
		name blank: false, unique: true
	}

	UserRole addUser( User user, boolean flush = false ) {
		if ( !user.roles.contains( this ) ) {
			UserRole.create( user, this, flush )
		} else {
			null
		}
	}

	String toString() {
		"Role: $name"
	}

	static final EXPERT    = "ROLE_EXPERT"
	static final CONSENTED = "ROLE_CONSENTED"
	static final ADMIN     = "ROLE_ADMIN"

	static Role getExpert()    { findByName( EXPERT )    }
	static Role getConsented() { findByName( CONSENTED ) }
	static Role getAdmin()     { findByName( ADMIN )     }

}
