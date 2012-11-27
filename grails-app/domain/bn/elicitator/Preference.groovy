package bn.elicitator

/**
 * Simple data store for keeping user preferences across sessions.
 * @see Preference
 */
class Preference {


	static mapping = {
		table 'userPreference'
		key column: 'preferenceKey'
		value column: 'preferenceValue'
	}

	ShiroUser owner

	String key

	String value

}
