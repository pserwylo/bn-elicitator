package bn.elicitator

class AllocateQuestionsController {

	UserService              userService
	AllocateQuestionsService allocateQuestionsService

	def index() {

		[
			expertCount             : userService.expertCount,
			totalQuestions          : allocateQuestionsService.countTotalQuestions(),
			participantsPerQuestion : AppProperties.properties.targetParticipantsPerQuestion
		]

	}

	def allocate( AllocateCmd cmd ) {

		allocateQuestionsService.calcAllocationsForAllUsers( 3 )

		Allocation.list().each { Allocation allocation ->

			render """
				<h3>User $allocation.user.username</h3>
				<p>Total questions: $allocation.totalQuestionCount</p>
				<ul>
					<li>${allocation.variables.collect { "$it" }.join( "</li><li>" )}</li>
				</ul>"""

		}

	}

}

class AllocateCmd {
	public int numParticipantsPerQuestion = -1;
}