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

import bn.elicitator.auth.User
import bn.elicitator.events.FinishedVariableEvent
import bn.elicitator.events.LoggedEvent

class AdminTagLib {

	static namespace = "bnAdmin"

	VariableService variableService

	/**
	 * Output a list of variables, and say how many participants they've been allocated to.
	 * The variables are grouped based on how many participants they're allocated to, and each group
	 * has a h2 explaining this. There will also be a tooltip which you can hover over to see who they
	 * are allocated to.
	 */
	def allocationList = {
		List<Variable> allVars          = Variable.list()
		List<Allocation> allAllocations = Allocation.list()
		Map<Variable, Integer> allocationCounts = [:]
		int maxCount = Integer.MIN_VALUE
		int minCount = Integer.MAX_VALUE
		allVars.each { var ->
			int count = allAllocations.count { it.variables.contains( var ) }
			allocationCounts.put( var, count )
			if ( count > maxCount ) {
				maxCount = count
			}
			if ( count < minCount ) {
				minCount = count
			}
		}

		for ( int i in minCount..maxCount ) {

			String header = i + " participant" + ( i == 1 ? '' : 's' )
			out << "<h2>$header</h2>"

			// All variables who are allocated to $i participants...
			List<Variable> iVars = allocationCounts.collect { it.value == i ? it.key : null }.findAll { it != null }
			if ( iVars.size() == 0 ) {

				out << "<p>None</p>"

			} else {

				out << "<ul class='variable-list'>"

				iVars.each { var ->
					List<User> users   = allAllocations.findAll { it.variables.contains( var ) }*.user
					String usersString = users*.username.join( "\n" )
					int completedCount = variableService.completedCount( var )

					out << """
						<li class='variable-item'>
							$var.readableLabel
							${bn.tooltip( [content : "$completedCount done" ] ) { usersString }}
						</li>
"""
				}

				out << "</ul>"
			}
		}
	}

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
