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

}
