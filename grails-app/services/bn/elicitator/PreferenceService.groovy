package bn.elicitator

/**
 * Always makes the asssumption that we are dealing with the {@link ShiroUser#getCurrent()} user.
 */
class PreferenceService {

	List<Preference> getPreferences()
	{
		return Preference.findAllByOwner( ShiroUser.current )
	}

	/**
	 * Attempts to update the existing preference object, and if non-existent, create a new one.
	 * @param key
	 * @param value
	 */
	void save( String key, String value )
	{
		Preference pref = load( key )
		if ( !pref )
		{
			pref = new Preference( key: key, owner: ShiroUser.current )
		}
		pref.value = value
		pref.save()
	}

	Preference load( String key )
	{
		return Preference.findByOwnerAndKey( ShiroUser.current, key )
	}

}
