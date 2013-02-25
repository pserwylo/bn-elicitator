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

package bn.elicitator.events

import bn.elicitator.AppProperties
import bn.elicitator.ShiroUser

/**
 * Logs events performed by users, e.g. logging in, filling in the form, etc.
 * Because this is first and foremost a research project, we're interested in keeping track of how the system is used.
 */
abstract class LoggedEvent {

	ShiroUser       user
	Date            date
	Integer         delphiPhase
	String          description

	String toString() {
		date.format( 'dd/MM/yyyy hh:mm' ) + ": " + description
	}

	protected static saveEvent( LoggedEvent event ) {
		event.user        = ShiroUser.current
		event.date        = new Date()
		event.delphiPhase = AppProperties.properties.delphiPhase
		event.save()
	}
}
