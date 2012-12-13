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
