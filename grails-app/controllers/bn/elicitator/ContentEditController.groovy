package bn.elicitator

class ContentEditController {

	def index = {
		if ( params.id ) {
			edit( params.id as Integer )
		}

		[ pages : ContentPage.list() ]
	}

	def edit( int id ) {
		ContentPage page = ContentPage.get( id );
		if ( !page ) {

		} else {
			displayForm( page )
		}
	}

	def create() {
		displayForm()
	}

	private def displayForm( ContentPage page = null ) {
		render( view: "edit", model: [ page : page ] )
	}

	private def notFound( def id ) {
		response.sendError( 404, "Could not find page with id '$id'" )
	}

	def delete() {
		ContentPage page = ContentPage.get( params.id as Integer )
		if ( !page ) {
			notFound( params.id )
			return
		}

		if ( !page.canDelete ) {
			flash.error = "Can't delete page '$page.label'"
		} else {
			page.delete()
		}
		redirect( action: "index" )
	}

	def save() {
		ContentPage page
		if ( params.id ) {
			page = ContentPage.get( params.id as Integer )
			if ( !page ) {
				notFound( params.id )
				return
			}
		} else {
			page = new ContentPage();
		}

		page.label   = params.label;
		page.alias   = params.alias;
		page.content = params.content;

		List<String> errors = []

		if ( page.alias == "admin" ) {
			errors.add( "Cannot use 'admin' as an alias." )
		}

		ContentPage duplicate = ContentPage.findByAlias( page.alias )
		if ( duplicate && duplicate.id != page.id ) {
			errors.add( "Another page already exists at this alias." )
		}

		if ( errors.size() > 0 ) {
			render errors
		} else {
			page.save( flush: true, failOnError : true )
			redirect( action: "index" )
		}

	}

}
