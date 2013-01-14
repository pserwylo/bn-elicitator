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

class EmailService {

	def userService
	def delphiPhase
	def mailService

	private String getUnsubscribeLink()
	{
		AppProperties.properties.url + "/unsubscribe"
	}

	private String getLink()
	{
		AppProperties.properties.url
	}

	private Map<String,String> createDefaultValues()
	{
		Map<String,String> values = [:]
		values[ EmailTemplate.PH_UNSUBSCRIBE_LINK ] = unsubscribeLink
		values[ EmailTemplate.PH_LINK ] = link
		return values
	}

	/**
	 * Ping each user and tell them that the first round is about to begin.
	 */
	void sendFirstPhaseStarting( Date startDate ) {

		EmailTemplate template = EmailTemplate.firstPhaseStarting

		Map<String,String> values = createDefaultValues()
		values.put( EmailTemplate.PH_START_DATE, startDate.toString() )

		List<ShiroUser> expertList = userService.expertList
		expertList.each { user -> send( user, template, values ) }

	}

	/**
	 * Ping each user and tell them that the current phase has just been completed.
	 * This should be called *BEFORE* incrementing the phase.
	 */
	void sendPhaseComplete() {

		EmailTemplate template = EmailTemplate.phaseComplete

		Map<String,String> values = createDefaultValues()
		values.put( EmailTemplate.PH_COMPLETED_PHASE, (String)( AppProperties.properties.delphiPhase ) )
		values.put( EmailTemplate.PH_NEXT_PHASE, (String)( AppProperties.properties.delphiPhase + 1 ) )
		values.put( EmailTemplate.PH_EXPECTED_PHASE_DURATION, "one day" )

		List<ShiroUser> expertList = userService.expertList
		for ( ShiroUser user in expertList )
		{
			send( user, template, values )
		}

	}

	private void send( ShiroUser user, EmailTemplate template, Map<String,String> values ) {

		Map<String,String> userValues = [:]
		userValues.putAll( values )
		userValues.put( EmailTemplate.PH_USER, user.realName )

		String replacedSubject = replacePlaceholders( template.subject, userValues )
		String replacedBody = replacePlaceholders( template.body, userValues )

		mailService.sendMail{
			to( user.email )
			from( AppProperties.properties.adminEmail )
			subject( replacedSubject )
			body( replacedBody )
		}

		new EmailLog( template: template, subject: replacedSubject, body: replacedBody, date: new Date() ).save()

	}

	private static String replacePlaceholders( String template, Map<String,String> values ) {

		String newString = template;
		for( entry in values )
		{
			String key = entry.key
			String value = entry.value
			newString = newString.replace( key, value )
		}
		return newString

	}

}
