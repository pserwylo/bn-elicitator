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

	UserService                       userService
	DelphiService                     delphiService
	SpringSecurityService             springSecurityService
	AllocateStructureQuestionsService allocateStructureQuestionsService
	AllocateCptQuestionsService       allocateCptQuestionsService

	def index() {

		route()

	}

	private void route() {
		
		User user = userService.current
		
		if ( AppProperties.properties.arePrizesEnabled() ) {
			
			if ( user.knowIfCanWinPrize() && user.hasConsented ) {
				redirect( controller: 'elicit' )
			} else if ( user.hasConsented ) {
				redirect( action: 'prize' )
			} else {
				redirect( action: 'statement' )
			}
			
		} else {
			
			if ( user.hasConsented ) {
				redirect( controller: 'elicit' )
			} else {
				redirect( action: 'statement' )
			}
			
		}
		
	}

	def prize() {

		if ( delphiService.hasPreviousPhase ) {
			forward( controller: 'contentView', params : [ page : ContentPage.CANT_REGISTER_THIS_ROUND ] )
		} else {
			[
				adminEmail : AppProperties.properties.adminEmail,
				user       : userService.current
			]
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
		} else {
			ContentPage contentPage = ContentPage.findByAlias( ContentPage.EXPLANATORY_STATEMENT )
			return [ explanatoryStatement: contentPage == null ? "" : contentPage.content ]
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
				route()
			} else {
				user.hasConsented  = true
				user.consentedDate = new Date()
				user.save( flush: true )

				Role.consented.addUser( user, true )

				if ( user.roles.contains( Role.expert ) ) {
					if ( AppProperties.properties.elicitationPhase == AppProperties.ELICIT_2_RELATIONSHIPS ) {
						allocateStructureQuestionsService.allocateToUser( user )
					} else if ( AppProperties.properties.elicitationPhase == AppProperties.ELICIT_3_PROBABILITIES ) {
						allocateCptQuestionsService.allocateToUser( user )
					}
				} else {
					new StructureAllocation( user: user, variables: [], totalQuestionCount: 0 ).save()
					new CptAllocation      ( user: user, variables: [], totalQuestionCount: 0 ).save()
				}

				springSecurityService.reauthenticate( user.username )

				route()
			}
		}
		else
		{
			flash.mustCheckRead = true
			redirect( controller: "explain" )
		}

	}
	
	def consentPrizes() {

		User user = userService.current
		boolean hasConsented = params?.containsKey( "consent" ) && params.consent == "1"

		if ( hasConsented ) {
			user.makeEligibleForPrize()
		} else {
			user.makeIneligibleForPrize()
		}

		user.save( flush: true )
		route()
	}

}
