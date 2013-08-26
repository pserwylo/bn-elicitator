package bn.elicitator

class AllocateCptQuestionsService extends AllocateQuestionsService {

	BnService bnService

	@Override
	protected def createNew() {
		new CptAllocation()
	}

	@Override
	protected def getAllocationsByCriteria(Closure criteria) {
		CptAllocation.withCriteria( criteria )
	}

	@Override
	protected def list() {
		CptAllocation.list()
	}

	@Override
	protected int expectedSecondsPerQuestion() {
		15
	}

	/**
	 * For each state of each parent, we have one question: "What are the compatible parent configurations?"
	 * For each compatible parent configuration, we have X questions: "For this configuration, what is the probability
	 *   of this child state?", where X is the number of states of the child.
	 * For each parent, we have one question: "How do you weigh this?"
	 * @param variable
	 * @return
	 */
	protected int questionsRequiredFor( Variable variable ) {
		List<Variable> parents = bnService.getArcsByChild( variable )*.parent*.variable
		int numParentStates = parents.size() == 0 ? 0 : parents.sum { it.states.size() }

		int countCompatibleParentConfigurations      = 0
		int countConditionalProbabilityDistributions = 0
		int countAHP                                 = 0

		if ( parents.size() > 1 ) {
			countCompatibleParentConfigurations      = numParentStates
			countConditionalProbabilityDistributions = numParentStates * variable.states.size()
			countAHP                                 = ( ( parents.size() * parents.size() - parents.size() ) / 2 )
		} else if ( parents.size() == 1 ) {
			countConditionalProbabilityDistributions = variable.states.size() * numParentStates
		} else {
			countConditionalProbabilityDistributions = variable.states.size()
		}
		return countCompatibleParentConfigurations + countConditionalProbabilityDistributions + countAHP
	}
}
