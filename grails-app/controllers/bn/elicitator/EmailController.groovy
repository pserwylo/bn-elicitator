/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		throw new Exception( "Not found: $cmd.emailName" )
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
