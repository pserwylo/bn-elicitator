package bn.elicitator

class AllocateQuestionsController {

	UserService              userService
	AllocateStructureQuestionsService allocateStructureQuestionsService

	def index() {

		[
			expertCount             : userService.expertCount,
			totalQuestions          : allocateStructureQuestionsService.countTotalQuestions(),
			participantsPerQuestion : AppProperties.properties.targetParticipantsPerQuestion
		]

	}

}
