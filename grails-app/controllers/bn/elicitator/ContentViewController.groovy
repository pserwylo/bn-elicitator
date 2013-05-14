package bn.elicitator

class ContentViewController {

	def index() {

		String alias = params.page ?: ContentPage.HOME
		ContentPage page = ContentPage.findByAlias( alias )
		String error = null
		if ( !page ) {
			error = "Could not find the page: " + alias
			page = ContentPage.findByAlias( ContentPage.HOME );
		}

		[ page : page, error : error ]
	}
}
