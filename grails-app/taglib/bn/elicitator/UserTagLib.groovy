/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2013 Peter Serwylo (peter.serwylo@monash.edu)
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

class UserTagLib {

	static namespace = "bnUser"

	def variableService
	def delphiService
	def userService
	def allocateStructureQuestionsService
	def allocateCptQuestionsService

	def realName = {
		out << userService.current?.realName ?: ""
	}

    /**
     * @attr user REQUIRED 
     */
    def sessions = { attrs ->
        User user = attrs.remove( 'user' )
        List<UserSession> sessions = userService.calcSessions( user )
        out << "<ul class='user-session-list'>"
        sessions.each { UserSession session ->
            int secs = session.durationInSecs % 60
            int mins = session.durationInSecs / 60
            out << """
                <li>
                    <span class='duration'>$mins:$secs</span>
                    <span class='phase'>Phase $session.delphiPhase</span>
                    <span class='date'>$session.startDate</span>
                </li>
                """
        }
        out << "</ul>"
    }
    
	/**
	 * @attr user REQUIRED
	 */
	def completedInfo = { attrs ->
		User user             = attrs.user
		Allocation a = getAllocation( user )
		if (!a) {
			out << "None allocated"
		} else {
			int completed = variableService.completedCount( user )
			int total = a.variables?.size()

			if ( total == completed )
			{
				out << "Completed phase $delphiService.phase "
			}
			out << "($completed / $total)"
		}
	}

	private Allocation getAllocation( User user ) {
		if ( AppProperties.properties.elicitationPhase == AppProperties.ELICIT_2_RELATIONSHIPS ) {
			return allocateStructureQuestionsService.getAllocation( user )
		} else {
			return allocateCptQuestionsService.getAllocation( user )
		}
	}

}
