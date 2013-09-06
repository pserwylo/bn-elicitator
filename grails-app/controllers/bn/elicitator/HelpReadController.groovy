package bn.elicitator

import grails.converters.JSON

/**
 * Provides AJAX access to mark a help message as read, so that we don't have to show it subsequent page loads when
 * rendering the Help taglib.
 */
class HelpReadController {

	def userService

	def read() {

		if ( params.containsKey( 'uniqueId' ) ) {

			String uniqueId = params.uniqueId
			HelpRead msgRead = HelpRead.findByUniqueIdAndReadBy( uniqueId, userService.current )
			if ( msgRead == null ) {
				new HelpRead( uniqueId : uniqueId, readBy: userService.current ).save( flush: true )
			}

			render ( [ read : true, uniqueId : uniqueId ] as JSON )

		}

	}

}
