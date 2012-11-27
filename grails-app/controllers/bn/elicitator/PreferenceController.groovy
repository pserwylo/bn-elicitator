package bn.elicitator

class PreferenceController {

	PreferenceService preferenceService

	def index() { }

	def save( PreferenceCommand cmd )
	{
		preferenceService.save( cmd.key, cmd.value )
		render cmd.value
	}

	def load( PreferenceCommand cmd )
	{
		Preference pref = preferenceService.load( cmd.key )
		render pref ? pref.value : ''
	}

}

class PreferenceCommand
{
	String key
	String value
}