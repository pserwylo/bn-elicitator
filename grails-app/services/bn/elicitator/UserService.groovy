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
		return Event.findByTypeAndUserAndDelphiPhase( Event.Type.COMPLETE_FORM, user, delphiPhase ) != null
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
