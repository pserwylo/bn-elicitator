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

class AdminDashController {

	def emailService
	def adminService
	def delphiService
	def userService
	def mailService

	/**
	 * Show some summary statistics, including the current state of the app (e.g. delphi phase), the list of users
	 * and how many have completed the current phase.
	 * @return
	 */
	def index() {

		List<ShiroUser> userList = ShiroUser.list()
		Integer totalExperts = userList.count { it.roles.contains( ShiroRole.expert ) }

		[
			appProperties         : AppProperties.properties,
			userList              : userList,
			totalExperts          : totalExperts,
			completedCurrentRound : delphiService.completedCurrentRound,
		]

	}

	/**
	 * If there are users who have  not completed the round, ask the admin to confirm, otherwise just redirect to
	 * the 'advanceDelphiPhase' action.
	 * @return
	 */
	def confirmAdvanceDelphiPhase() {

		List<ShiroUser> yetToComplete = userService.getUsersYetToComplete( delphiService.phase )

		[ appProperties: AppProperties.properties, yetToComplete: yetToComplete ]

	}

	def yesAdvanceDelphiPhase() {

		flash.confirmed = true
		advanceDelphiPhase()

	}

	def advanceDelphiPhase() {

		// The browser was probably refreshed, or navigated here manually.
		// In this case, can we just shown them what we did last time?
		if ( flash.confirmed )
		{
			this.emailService.sendPhaseComplete()
			this.adminService.advanceDelphiPhase()
			flash.messages = [ "Delphi phase is now " + delphiService.phase + ". The participants have been notified via email." ]
		}

		redirect( action: 'index' )

	}

}
