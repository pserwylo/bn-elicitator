package bn.elicitator

import bn.elicitator.algorithms.Das2004Service
import grails.converters.JSON

class Das2004Controller {

	Das2004Service das2004Service

	def index = {

		long completedId = params.getLong( 'completedId' ) ?: 0

		Variable completed = null
		if ( completedId > 0 ) {
			// TODO: Save a 'completed' flag against the allocation so that we can remove it from the list of
			// questions to answer
			completed = Variable.get( completedId )
		}

		return [ completedVariable : completed ]
	}

	/**
	 * Calculate parent weights.
	 */
	def importance = {
		Variable variable = Variable.get( params.getLong( 'id' ) ?: 0 )
		if ( variable == null ) {
			throw new Exception( "Not found: $params.id" )
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
		if ( variable == null ) {
			throw new Exception( "Not found: $params.id" )
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
		if ( variable == null ) {
			throw new Exception( "Not found: $params.id" )
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
