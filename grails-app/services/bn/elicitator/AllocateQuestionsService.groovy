package bn.elicitator

import bn.elicitator.auth.User

abstract class AllocateQuestionsService {

	UserService     userService
	VariableService variableService

	List<User> getOthersAllocatedTo( Variable var ) {
		Closure criteria = {
			variables {
				eq ( 'id', var.id )
			}
		}

		getAllocationsByCriteria( criteria )*.user
	}

	protected abstract def createNew()
	protected abstract def getAllocationsByCriteria( Closure criteria )
	protected abstract def list()

	/**
	 * For "variable", how many questions will be asked of the participant?
	 * We use this in conjunction with {@link AllocateQuestionsService#expectedSecondsPerQuestion()} to decide
	 * how many variables to assign somebody.
	 * @param variable
	 * @return
	 * @see AllocateQuestionsService#expectedSecondsPerQuestion()
	 */
	protected abstract int questionsRequiredFor( Variable variable )

	/**
	 * We use this to decide the maximum number of questions we are willing to assign to each user.
	 * If the number is high (takes a long time to answer questions) then they will be allocated less.
	 * @return
	 * @see AllocateQuestionsService#questionsRequiredFor(Variable)
	 */
	protected abstract int expectedSecondsPerQuestion()

	/**
	 * For each variable class, figure out how many variables there are in it,
	 * and ask how many possible children there are for each of those. This
	 * tally is the total number of questions to be asked.
	 * @return
	 */
	public int countTotalQuestions() {
		int count = 0;
		variableService.eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialChildren ->
			int countVarsInClass           = varsInClass.size()
			int countVarsInChildrenClasses = potentialChildren.size()
			count += ( countVarsInClass * countVarsInChildrenClasses )
		}
		return count
	}

	private List<Variable> getVarsWithLowestAllocation( int maxQuestions ) {

		List<Variable> allVars = Variable.list()
		Collections.shuffle( allVars )

		if ( allVars.size() == 0 ) {
			return []
		}

		Map<Variable, Integer> allocationCount = [:]
		allVars.each { allocationCount[ it ] = 0 }
		def currentAllocations = list()
		currentAllocations.each { Allocation allocation ->
			allocation.variables.each { var ->
				allocationCount[ var ] ++
			}
		}

		int totalQuestionsForLowestCount = 0
		List<Variable> lowestCount       = []
		while ( totalQuestionsForLowestCount < maxQuestions ) {

			Map.Entry<Variable, Integer> lowestEntry = null
			allocationCount.each { Map.Entry<Variable, Integer> entry ->
				boolean used = lowestCount.contains( entry.key )
				if ( ( !lowestEntry || lowestEntry.value > entry.value ) && !used )  {
					lowestEntry = entry
				}
			}

			if ( lowestEntry == null ) {
				break;
			}

			lowestCount.add( lowestEntry.key )
			totalQuestionsForLowestCount += questionsRequiredFor( lowestEntry.key )
		}

		return lowestCount
	}

	public void allocateToUser( User user ) {

		int maxTime      = 150 * 60
		int maxQuestions = maxTime / expectedSecondsPerQuestion() // AppProperties.properties.targetParticipantsPerQuestion

		List<Variable> varsToAllocate = getVarsWithLowestAllocation( maxQuestions )
		def allocation = createNew()
		allocation.user = user
		for ( Variable varToAllocate in varsToAllocate ) {
			int numQuestions = questionsRequiredFor( varToAllocate )
			if ( numQuestions > 0 ) {
				allocation.addVariable( varToAllocate, numQuestions )
				if ( allocation.totalQuestionCount > maxQuestions ) {
					break
				}
			}
		}

		allocation.save( flush: true, failOnError: true )
	}
}
