package bn.elicitator

class AllocateQuestionsController {

	UserService userService

	private int countTotalQuestions() {
		int count = 0;
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

			int varsInClass         = categories[ varClass ].size()
			int varsInParentClasses = potentialParents.size()

			count += ( varsInClass * varsInParentClasses )
		}

		return count
	}

	def index() {

		[
			expertCount : userService.expertCount,
			totalQuestions : countTotalQuestions()
		]

	}

}
