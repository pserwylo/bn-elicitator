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

import org.apache.shiro.crypto.hash.Sha256Hash

class ExplainController {

	def index() {

		if ( ShiroUser.current.hasConsented )
		{
			redirect( controller: 'elicit' )
		}
		else
		{
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

		[ explanatoryStatement: AppProperties.properties.explanatoryStatement ]

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
			ShiroUser.current.hasConsented = true;
			ShiroUser.current.consentedDate = new Date();
			ShiroUser.current.addToRoles( ShiroRole.findByName( ShiroRole.CONSENTED ) )
			ShiroUser.current.save();

			redirect( controller: "elicit" )
		}
		else
		{
			flash.mustCheckRead = true
			redirect( controller: "explain" )
		}

	}

}
