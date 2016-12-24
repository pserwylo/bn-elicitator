package bn.elicitator

class AllocateStructureQuestionsService extends AllocateQuestionsService {

	@Override
	protected def createNew() {
		new StructureAllocation()
	}

	@Override
	protected def getAllocationsByCriteria(Closure criteria) {
		StructureAllocation.withCriteria( criteria )
	}

	@Override
	protected def list() {
		StructureAllocation.list()
	}

	@Override
	protected int expectedSecondsPerQuestion() {
		30
	}

	/**
	 * We produce one question for each potential child.
	 * @param variable
	 * @return
	 */
	@Override
	protected int questionsRequiredFor( Variable variable ) {
		variableService.countPotentialChildren( variable )
	}

	/**
	 * When an admin adds a new variable, or changes the variable class of a variable, then the list of eligible questions changes.
	 * In response, we need to take the drastic step of removing all allocated questiosn, and reallocating questions anew.
	 */
	void reassignQuestions() {
		def allocations = StructureAllocation.list()
		def relationships = Relationship.list()
		def completedPhases = CompletedPhase.list()
		def completedVariables = VisitedVariable.list()

		allocations*.delete(flush: true, failOnError: true)
		relationships*.delete(flush: true, failOnError: true)
		completedVariables*.delete(flush: true, failOnError: true)
		completedPhases*.delete(flush: true, failOnError: true)

		userService.expertList.each { user ->
			if (AppProperties.properties.elicitationPhase == AppProperties.ELICIT_2_RELATIONSHIPS) {
				allocateToUser(user)
			}
		}
	}
}
