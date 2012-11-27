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

		[
			appProperties: AppProperties.properties,
			userList: ShiroUser.list(),
			completedCurrentRound: delphiService.completedCurrentRound
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
