package bn.elicitator

import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.feedback.Answer
import grails.converters.JSON

class Das2004Controller {

	Das2004Service              das2004Service
	UserService                 userService
	AllocateCptQuestionsService allocateCptQuestionsService

	def calc = {
		das2004Service.performCalculations()
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
