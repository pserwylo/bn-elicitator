package bn.elicitator

class AllocateQuestionsController {

	UserService              userService
	AllocateQuestionsService allocateQuestionsService

	def index() {

		[
			expertCount : userService.expertCount,
			totalQuestions : allocateQuestionsService.countTotalQuestions()
		]

	}

	def allocate( AllocateCmd cmd ) {

		List<AllocateQuestionsService.Allocation> allocations = allocateQuestionsService.calcAllocations( 3 )
		allocations.each { AllocateQuestionsService.Allocation allocation ->

			render """
				<h3>User $allocation.user.username</h3>
				<ul>
					<li>${allocation.questions.collect { "$it.parent -> $it.child" }.join( "</li><li>" )}</li>
				</ul>"""

		}

	}

}

class AllocateCmd {
	public int numParticipantsPerQuestion = -1;
}