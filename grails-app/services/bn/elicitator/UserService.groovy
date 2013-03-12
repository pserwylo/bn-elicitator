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

import bn.elicitator.auth.Role
import bn.elicitator.auth.User
import bn.elicitator.auth.UserRole
import grails.plugins.springsecurity.SpringSecurityService

class UserService {

	SpringSecurityService springSecurityService

	User getCurrent() {
		(User)springSecurityService.currentUser
	}

	List<User> getExpertList() {
		getList( Role.expert )
	}

	List<User> getAdminList() {
		getList( Role.admin )
	}

	List<User> getList( Role hasRole ) {
		UserRole.findAllByRole( hasRole )*.user
	}

	/**
	 * Returns the total number of users with the 'expert' role.
	 * Will cache the result so that we don't hit the database each time, keep this in mind if using between adding users.
	 * @return
	 */
	Integer getExpertCount() {
		UserRole.countByRole( Role.expert )
	}

	/**
	 * Get a list of all users who do *not* yet have a 'COMPLETE_FORM' event.
	 * @param delphiPhase
	 * @return
	 */
	List<User> getUsersYetToComplete( Integer delphiPhase = AppProperties.properties.delphiPhase ) {
		List<User> completedUsers = getCompletedUsers( delphiPhase )
		List<User> users = getExpertList()
		return users.findAll { !completedUsers.contains( it ) }
	}

	List<User> getCompletedUsers( Integer delphiPhase = AppProperties.properties.delphiPhase ) {
		return CompletedPhase.findAllByDelphiPhase( delphiPhase )*.completedBy
	}

	void deleteUser( User user ) {
		if ( user && user.username != "admin" ) {
			user.delete()
		}
	}
}
