package bn.elicitator

import grails.converters.JSON

class Das2004Controller {

	def index = {
		return []
	}

	/**
	 * Compatible parent configurations
	 */
	def expected = {
		Variable variable = Variable.get( params.getLong( 'id' ) ?: 0 )
		if ( variable == null ) {
			throw new Exception( "Not found: $params.id" )
		}

		return [
			variable : variable
		]
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
		} catch ( Exception e ) {
			response.sendError( 500, e.message )
		}
	}

}
