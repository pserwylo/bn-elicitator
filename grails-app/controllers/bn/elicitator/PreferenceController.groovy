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

class PreferenceController {

	PreferenceService preferenceService

	def index() { }

	def save( PreferenceCommand cmd )
	{
		preferenceService.save( cmd.key, cmd.value )
		render cmd.value
	}

	def load( PreferenceCommand cmd )
	{
		Preference pref = preferenceService.load( cmd.key )
		render pref ? pref.value : ''
	}

}

class PreferenceCommand
{
	String key
	String value
}