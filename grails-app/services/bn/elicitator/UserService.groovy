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

class UserService {

	private List<ShiroUser> cachedExpertList = null;

	private void loadExpertList()
	{
		if ( cachedExpertList == null )
		{
			// Does GORM provide a way to say findAllByRolesContains( ... )?
			cachedExpertList = ShiroUser.findAll().findAll { it -> it.roles.contains( ShiroRole.expert ) }
		}
	}

	List<ShiroUser> getExpertList() {
		loadExpertList()
		return cachedExpertList
	}

	/**
	 * Returns the total number of users with the 'expert' role.
	 * Will cache the result so that we don't hit the database each time, keep this in mind if using between adding users.
	 * @return
	 */
	Integer getExpertCount() {
		loadExpertList()
		return cachedExpertList.size()
	}

	/**
	 * Search for an event which says that the user has completed the appropriate round.
	 * @param user
	 * @param delphiPhase
	 * @return
	 */
	boolean isUserFinished( ShiroUser user = ShiroUser.current, Integer delphiPhase = AppProperties.properties.delphiPhase ) {
		return LoggedEvent.findByTypeAndUserAndDelphiPhase( LoggedEvent.Type.COMPLETE_FORM, user, delphiPhase ) != null
	}

	/**
	 * Get a list of all users who do *not* yet have a 'COMPLETE_FORM' event.
	 * @param delphiPhase
	 * @return
	 */
	List<ShiroUser> getUsersYetToComplete( Integer delphiPhase = AppProperties.properties.delphiPhase ) {
		List<ShiroUser> completedUsers = getCompletedUsers( delphiPhase )
		List<ShiroUser> users = getExpertList()
		return users.findAll { !completedUsers.contains( it ) }
	}

	List<ShiroUser> getCompletedUsers( Integer delphiPhase = AppProperties.properties.delphiPhase ) {
		List<CompletedPhase> completed = CompletedPhase.findAllByDelphiPhase( delphiPhase )*.completedBy
	}
}
