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

import bn.elicitator.events.LoggedEvent

class AdminTagLib {

	static namespace = "bnAdmin"

	def noneFound = {
		out << "None found..."
	}

	/**
	 * @attr list REQUIRED
	 * @attr id REQUIRED
	 */
	def logItemList = { attrs->
		List<String> list = attrs.list
		String id         = attrs.id
		if ( list.size() == 0 )
		{
			out << bnAdmin.noneFound()
		}
		else
		{
			out << """
				<ul id='$id' class='log-item-list'>
					${list.join( '' )}
				</ul>
			"""
		}

	}

	/**
	 * @attr emailLogList REQUIRED
	 */
	def emailLogList = { attrs ->
		List<EmailLog> emailLogList = attrs.emailLogList
		List<String> list = emailLogList.collect {
			"""
			<li class='email-log-item'>
				${bn.date( [ date: it.date ] )} <span class='log'>$it.subject</span>
			</li>
			"""
		}
		out << bnAdmin.logItemList( [ list: list, id: "emailLog" ] )
	}

	/**
	 * @attr eventList REQUIRED
	 */
	def eventList = { attrs ->
		List<LoggedEvent> eventList = attrs.eventList
		List<String> list = eventList.collect {
			"""
			<li class='event-log-item ${it.class.name}'>
				${bn.date( [ date: it.date ] )} <span class='log'>$it.description</span>
			</li>
			"""
		}
		out << bnAdmin.logItemList( [ list: list, id: "eventLog" ] )
	}

}
