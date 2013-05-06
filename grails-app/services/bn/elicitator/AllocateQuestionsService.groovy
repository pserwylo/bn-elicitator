package bn.elicitator

import bn.elicitator.auth.User

class AllocateQuestionsService {

	UserService userService

	/**
	 * For each variable class, figure out how many variables there are in it,
	 * and ask how many possible parents there are for each of those. This
	 * tally is the total number of questions to be asked.
	 * @return
	 */
	int countTotalQuestions() {
		int count = 0;
		eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialParents ->
			int countVarsInClass         = varsInClass.size()
			int countVarsInParentClasses = potentialParents.size()
			count += ( countVarsInClass * countVarsInParentClasses )
		}
		return count
	}

	private void eachVariableClass( Closure closure ) {

		Map<VariableClass,List<Variable>> categories = [:]

		List<VariableClass> classes = VariableClass.list();
		List<Variable> variables    = Variable.list();

		classes.each { categories.put( it, [] ) }

		variables.each { var ->
			categories[ var.variableClass ].add( var )
		}

		classes.each { VariableClass varClass ->

			List<Variable> potentialParents = []
			varClass.potentialParents.each { VariableClass parentClass ->
				potentialParents.addAll( categories[ parentClass ] )
			}

			closure( varClass, categories[ varClass ], potentialParents )
		}
	}

	class Allocation {
		User user
		List<Relationship> questions = []

		String toString() {
			return "User $user allocated ${questions.size()} questions."
		}
	}

	List<Allocation> calcAllocations( int participantsPerQuestion ) {

		List<User> experts = userService.expertList
		List<Allocation> allocations = experts.collect { new Allocation( user: it ) }

		if ( experts.size() < participantsPerQuestion ) {
			throw new IllegalArgumentException( "Not enough experts to allocate $participantsPerQuestion per question (only have ${experts.size()})." )
		}

		// Find the people with the least amount of questions allocated to
		// them (at this point in time) and then pick one randomly...
		Closure getSmallestAllocation = { List<User> excludingUsers ->
			int min = Integer.MAX_VALUE

			List<Allocation> validAllocations = allocations.findAll { !excludingUsers.contains( it.user ) }
			validAllocations.each {
				if ( it.questions.size() < min ) {
					min = it.questions.size();
				}
			}
			List<Allocation> smallest = validAllocations.findAll { alloc -> alloc.questions.size() == min }
			int index = Math.random() * ( smallest.size() - 1 )
			return smallest[ index ]
		}

		eachVariableClass { VariableClass varClass, List<Variable> varsInClass, List<Variable> potentialParents ->
			for ( Variable child in varsInClass ) {
				for ( Variable parent in potentialParents ) {

					// Find a bunch of users (who are at the bottom of the
					// pecking order so far) to assign this question to...
					List<User> beenAllocated = []
					while ( beenAllocated.size() < participantsPerQuestion && beenAllocated.size() <= experts.size() ) {
						Allocation smallest = getSmallestAllocation( beenAllocated )
						smallest.questions.add( new Relationship( child : child, parent : parent ) )
						beenAllocated.add( smallest.user )
					}
				}
			}
		}

		return allocations
	}
}
