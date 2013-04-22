package bn.elicitator

class ContentViewController {

	def index() {

		String alias = params.page ?: "index"
		ContentPage page = ContentPage.findByAlias( alias )
		if ( !page ) {
			response.sendError( 404, "Could not find any content at this address" )
			return
		}

		[ page : page ]
	}
}
