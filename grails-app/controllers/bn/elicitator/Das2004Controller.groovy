package bn.elicitator

import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.algorithms.Das2004Service.AlgorithmException
import bn.elicitator.feedback.Answer
import bn.elicitator.network.BnProbability
import grails.converters.JSON

class Das2004Controller {

	Das2004Service              das2004Service
	UserService                 userService
	AllocateCptQuestionsService allocateCptQuestionsService

	def averageProbabilities() {
		render "<p>Average all probabilities</p>"

		BnProbability.list().each { it.delete() }

		// Group probabilities by the child state they represent...
		Map<State, List<Probability>> probsGroupedByChildState = [:]
		List<Probability> allProbabilities = Probability.list();

		render "<p>...there are ${allProbabilities.size()} probabilities to sift through...</p>"

		allProbabilities.each {
			if (!probsGroupedByChildState.containsKey(it.childState)) {
				probsGroupedByChildState[ it.childState ] = []
			}
			probsGroupedByChildState[ it.childState ].add( it )
		}

		render "<p>... with ${probsGroupedByChildState.size()} distinct child states...</p>"

		probsGroupedByChildState.keySet().each { State childState ->

			render "<p>Processing P( $childState.variable.label = $childState.label | ... )</p>"

			List<Probability> probabilities = probsGroupedByChildState[ childState ]

			render "<p>There are ${probabilities.size()} probabilities for this child state...</p>"

			// Deterministically build a string which represents the parent states passed in...
			Closure idOfStates = { Set<State> states ->
				new ArrayList<State>( states )*.id.sort().join('-')
			}

			// For each child state, group by the parent states, so that we can average.
			Map<String, List<Probability>> probsGroupedByParentStates = [:]
			probabilities.each { Probability prob ->
				String key = idOfStates( prob.parentStates )
				if ( !probsGroupedByParentStates.containsKey( key ) ) {
					render "<p>Parent states: ${prob.parentStates.collect { it.variable.label + " = " + it.label }.join( ", " ) }</p>"
					probsGroupedByParentStates[ key ] = []
				}
				probsGroupedByParentStates[ key ].add( prob )
			}

			render "<p>...and ${probsGroupedByParentStates.size()} distinct combinations of parent states (parameters)...</p>"

			Map<String, Probability> unnormalisedProbsForParentStates = [:]

			double total = 0;

			// Now we are free to sum and average the probabilities for each set of parent states...
			probsGroupedByParentStates.keySet().each { String parentStateIds ->

				List<Probability> probabilitiesGivenParentStates = probsGroupedByParentStates[ parentStateIds ]
				Probability p = probabilitiesGivenParentStates[ 0 ];

				double average = probabilitiesGivenParentStates*.probability.sum() / probabilities.size()

				unnormalisedProbsForParentStates.put( parentStateIds, new Probability(
					childState   : p.childState,
					createdBy    : null,
					parentStates : p.parentStates,
					probability  : average,
				))

				total += average;
				render "<p>"
				renderProbability( p, average )
				render " [ averaged from ${probabilitiesGivenParentStates.size()} responses ]"
				render "</p>"

			}

			double factor = 1 / total;

			render "<p>normalised:<p>"
			unnormalisedProbsForParentStates.values().each {

				double normalised = it.probability * factor

				new BnProbability(
					probability  : normalised,
					childState   : it.childState,
					parentStates : it.parentStates
				).save()

				render "<p>"
				renderProbability( it, normalised )
				render "</p>"
			}

		}

	}

	private def renderProbability( Probability p, double value ) {
		render "P( $p.childState.variable.label = $p.childState.label | ${p.parentStates.collect { p1 -> "$p1.variable.label = $p1.label" }.join(", ")} ) = $value"
	}

	def calc() {
		render "<p>Performing DAS calculations...</p>"
		try {
			das2004Service.performCalculations()
		} catch ( AlgorithmException e ) {
			print e.message
			renderException( exception: e )
		} catch ( Exception e ) {
			renderException( exception: e )
		}
		return
	}

	def index = {

		long completedId = params.getLong( 'id' ) ?: 0
		if ( completedId > 0 ) {
			Variable completed = Variable.get( completedId )
			if ( completed != null ) {
				das2004Service.complete( completed )
				flash.message = "Completed $completed.readableLabel!"
			}
			redirect( action: 'index' )
		}

		if ( das2004Service.hasCompletedAllocation() ) {
			int feedback = Answer.countByAnsweredBy( userService.current )
			if ( feedback > 0 ) {
				forward( action : 'finished' )
			} else {
				forward( controller : 'feedback' )
			}
		} else {
			return []
		}
	}

	def finished = {
		[ user : userService.current ]
	}

	/**
	 * Calculate parent weights.
	 */
	def importance = {
		Variable variable = Variable.get( params.getLong( 'id' ) ?: 0 )
		if ( variable == null || !allocateCptQuestionsService.isAllocated( variable ) ) {
			redirect( controller: 'error', action: 'notFound' )
			return
		}

		return [ variable : variable ]
	}

	def ajaxSaveComparison = {
		long childId         = params.getLong( 'childId' )               ?: 0
		long parentOneId     = params.getLong( 'parentOneId' )           ?: 0
		long parentTwoId     = params.getLong( 'parentTwoId' )           ?: 0
		long mostImportantId = params.getLong( 'mostImportantParentId' ) ?: 0
		int weight           = params.getInt( 'weight' )                 ?: 0

		try {
			das2004Service.saveComparison( childId, parentOneId, parentTwoId, mostImportantId, weight )
			render( [ status : "success" ] as JSON )
		} catch ( IllegalArgumentException e ) {
			response.sendError( 400, e.message )
		}
	}

	/**
	 * Elicit probability distributions for each compatible parent configuration.
	 */
	def likelihood = {
		Variable variable = Variable.get( params.getLong( 'id' ) ?: 0 )
		if ( variable == null || !allocateCptQuestionsService.isAllocated( variable ) ) {
			redirect( controller: 'error', action: 'notFound' )
			return
		}

		return [ variable : variable ]
	}

	def ajaxSaveProbabilityEstimation = {

		long childStateId            = params.getLong( 'childStateId' ) ?: 0
		long parentConfigurationId   = params.getLong( 'parentConfigurationId' ) ?: 0
		double probabilityPercentage = params.getDouble( 'probabilityPercentage' ) ?: 0

		try {
			das2004Service.saveProbabilityEstimation( childStateId, parentConfigurationId, probabilityPercentage / 100 )
			render( [ status : "success" ] as JSON )
		} catch ( IllegalArgumentException e ) {
			response.sendError( 400, e.message )
		}
	}

	/**
	 * Elicit compatible parent configurations
	 */
	def expected = {
		Variable variable = Variable.get( params.getLong( 'id' ) ?: 0 )
		if ( variable == null || !allocateCptQuestionsService.isAllocated( variable ) ) {
			redirect( controller: 'error', action: 'notFound' )
			return
		}

		return [ variable : variable ]
	}

	def ajaxSaveCompatibleParentConfiguration = {

		long parentStateId = params.getLong( 'parentStateId' ) ?: 0
		List<Long> otherParentStateIds = params.getList( 'otherParentStateIds[]' )?.collect {
			def stateId
			try {
				stateId = it as Long
			} catch ( NumberFormatException e ) {
				stateId = 0
			}
			return stateId
		}

		try {
			das2004Service.saveCompatibleParentConfiguration( parentStateId, otherParentStateIds )
			render( [ status : "success" ] as JSON )
		} catch ( IllegalArgumentException e ) {
			response.sendError( 400, e.message )
		}
	}

}
