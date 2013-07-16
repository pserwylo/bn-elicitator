package bn.elicitator

import bn.elicitator.auth.User

class AllocateQuestionsService {

	UserService     userService
	VariableService variableService

	List<User> getOthersAllocatedTo( Variable var ) {
		List<Allocation> allocations = Allocation.withCriteria {
			variables {
				eq ( 'id', var.id )
			}
		}
		allocations*.user
	}

	/**
	 * For each variable class, figure out how many variables there are in it,
	 * and ask how many possible children there are for each of those. This
	 * tally is the total number of questions to be asked.
	 * @return
	 */
	int countTotalQuestions() {
		int count = 0;
		variableService.eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialChildren ->
			int countVarsInClass         = varsInClass.size()
			int countVarsInChildrenClasses = potentialChildren.size()
			count += ( countVarsInClass * countVarsInChildrenClasses )
		}
		return count
	}

	List<Allocation> calcAllocationsForAllUsers( int participantsPerQuestion ) {

		List<User> experts = userService.expertList
		List<Allocation> allocations = experts.collect { new Allocation( user: it ) }

		if ( experts.size() < participantsPerQuestion ) {
			throw new IllegalArgumentException( "Not enough experts to allocate $participantsPerQuestion per question (only have ${experts.size()})." )
		}

		// Find the people with the least amount of variables allocated to
		// them (at this point in time) and then pick one randomly...
		Closure getSmallestAllocation = { List<User> excludingUsers ->
			int min = Integer.MAX_VALUE

			List<Allocation> validAllocations = allocations.findAll { !excludingUsers.contains( it.user ) }
			validAllocations.each {
				if ( it.totalQuestionCount < min ) {
					min = it.totalQuestionCount;
				}
			}
			List<Allocation> smallest = validAllocations.findAll { alloc -> alloc.totalQuestionCount == min }
			int index = Math.random() * ( smallest.size() - 1 )
			return smallest[ index ]
		}

		variableService.eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialChildren ->
			for ( Variable parent in varsInClass ) {
				// Find a bunch of users (who are at the bottom of the
				// pecking order so far) to assign this question to...
				List<User> beenAllocated = []
				while ( beenAllocated.size() < participantsPerQuestion && beenAllocated.size() <= experts.size() ) {
					Allocation smallest = getSmallestAllocation( beenAllocated )
					smallest.addVariable( parent, potentialChildren )
					beenAllocated.add( smallest.user )
				}
			}
		}

		return allocations
	}

	public List<Variable> getVarsWithLowestAllocation( int numVars ) {

		List<Variable> allVars = Variable.list()
		Collections.shuffle( allVars )

		if ( allVars.size() < numVars ) {
			return []
		}

		Map<Variable, Integer> allocationCount = [:]
		allVars.each { allocationCount[ it ] = 0 }
		List<Allocation> currentAllocations = Allocation.list()
		currentAllocations.each { allocation ->
			allocation.variables.each { var ->
				allocationCount[ var ] ++
			}
		}

		List<Variable> lowestCount = []
		while ( lowestCount.size() < numVars ) {

			Map.Entry<Variable, Integer> lowestEntry = null
			allocationCount.each { Map.Entry<Variable, Integer> entry ->
				boolean used = lowestCount.contains( entry.key )
				if ( ( !lowestEntry || lowestEntry.value > entry.value ) && !used )  {
					lowestEntry = entry
				}
			}

			lowestCount.add( lowestEntry.key )
		}

		return lowestCount
	}

	public void allocateToUser( User user ) {
		List<Variable> varsToAllocate = getVarsWithLowestAllocation( AppProperties.properties.targetParticipantsPerQuestion )
		Allocation allocation = new Allocation( user: user )
		variableService.eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialChildren ->
			varsToAllocate.each { varToAllocate ->
				if ( varsInClass.contains( varToAllocate ) ) {
					allocation.addVariable( varToAllocate, potentialChildren )
				}
			}
		}
		allocation.save( flush: true, failOnError: true )
	}
}
