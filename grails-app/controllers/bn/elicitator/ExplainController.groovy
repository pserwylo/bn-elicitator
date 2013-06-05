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

import bn.elicitator.auth.*
import grails.plugins.springsecurity.SpringSecurityService

class ExplainController {

	UserService              userService
	DelphiService            delphiService
	SpringSecurityService    springSecurityService
	AllocateQuestionsService allocateQuestionsService

	def index() {

		if ( userService.current.hasConsented ) {
			redirect( controller: 'elicit' )
		} else {
			redirect( action: 'statement' )
		}

	}

	/**
	 * Dumps the explanatory statement from {@link AppProperties#getProperties()}, then automatically makes a TOC from
	 * all of the H2 elements, with appropriate navigation.
	 * Will also keep track of whether a use has indeed checked the "I have read and understood..." box.
	 * @return
	 */
    def statement() {

		if ( delphiService.hasPreviousPhase ) {
			forward( controller: 'contentView', params : [ page : ContentPage.CANT_REGISTER_THIS_ROUND ] )
			return
		} else {
			return [ explanatoryStatement: AppProperties.properties.explanatoryStatement ]
		}

    }

	/**
	 * Save the fact that the user has consented, but if for some reason they didn't (e.g. JavaScript was stuffed) then
	 * we will redirect them back to the explanatory statement with an error in the flash scope
	 * (flash.mustCheckRead = true).
	 * @return
	 */
	def consent() {

		if ( params["readStatement"] == "1" )
		{
			User user = userService.current
			if ( user.hasConsented ) {
				redirect( controller: 'elicit' )
			} else {
				user.hasConsented  = true
				user.consentedDate = new Date()
				user.save( flush: true )

				Role.consented.addUser( user, true )

				if ( user.roles.contains( Role.expert ) ) {
					allocateQuestionsService.allocateToUser( user )
				} else {
					new Allocation( user: user, variables: [], totalQuestionCount: 0 ).save()
				}

				springSecurityService.reauthenticate( user.username )

				redirect( controller: "elicit" )
			}
		}
		else
		{
			flash.mustCheckRead = true
			redirect( controller: "explain" )
		}

	}

}
