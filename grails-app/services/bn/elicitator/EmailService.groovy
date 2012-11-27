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

		new EmailLog( template: template, subject: replacedSubject, body: replacedBody ).save()

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
