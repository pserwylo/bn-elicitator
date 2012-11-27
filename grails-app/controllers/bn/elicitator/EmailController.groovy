package bn.elicitator

import grails.converters.JSON

class EmailController {

	static defaultAction = "list"

	def list() {

		[
			emailTemplateList : EmailTemplate.list()
		]

	}

	private EmailTemplate getTemplate( EmailTemplateCommand cmd ) {
		return EmailTemplate.findByName( cmd.emailName )
	}

	def template( EmailTemplateCommand cmd ) {

		EmailTemplate template = getTemplate( cmd )
		if ( template )
		{
			render template as JSON
		}
		else
		{
			notFound( cmd )
		}

	}

	def save( SaveEmailTemplateCommand cmd ) {

		EmailTemplate template = getTemplate( cmd )
		if ( template )
		{
			template.body = cmd.body
			template.subject = cmd.subject
			template.save()
		}
		else
		{
			notFound( cmd )
		}

	}

	private void notFound( EmailTemplateCommand cmd )
	{
		response.status = 404
		render cmd.emailName + " Not Found"
	}

}

class EmailTemplateCommand
{

	String emailName

}

class SaveEmailTemplateCommand extends EmailTemplateCommand
{
	String subject
	String body
}
