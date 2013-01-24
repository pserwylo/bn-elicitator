package bn.elicitator

/**
 * Provides AJAX access to mark a help message as read, so that we don't have to show it subsequent page loads when
 * rendering the Help taglib.
 */
class HelpReadController {

	def read() {

		String messageHash = params["messageHash"]
		HelpRead msgRead = HelpRead.findByMessageHashAndReadBy( messageHash, ShiroUser.current )
		if ( msgRead == null ) {
			new HelpRead( messageHash: messageHash, readBy: ShiroUser.current ).save( flush: true )
		}

		render "Read: " + messageHash

	}

}
