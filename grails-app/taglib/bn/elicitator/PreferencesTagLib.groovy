package bn.elicitator

/**
 * Renders JavaScript to do with saving and retrieving preferences.
 */
class PreferencesTagLib {

	PreferenceService preferenceService

	def preferencesJs = {

		String preferences = "";
		for ( Preference pref in preferenceService.preferences )
		{
			preferences += "'${pref.key}': '${pref.value}', "
		}
		if ( preferences.size() >=2 )
		{
			preferences = preferences.substring( 0, preferences.size() - 2 )
		}

		out << """
		<script type='text/javascript'>
			function Preferences() {

				var preferences = {
					${preferences}
				};

				this.getValue = function( key ) {
					return \$.get( '${createLink( controller: 'preference', action:  'load' )}' );
				};

				this.setValue = function( key, value ) {
					preferences[ key ] = value;
					\$.post( '${createLink( controller: 'preference', action: 'save' )}', { key: key, value: value } );
				};

			};

			var prefs = new Preferences();
		</script>
		"""

	}


	/**
	 * Outputs javascript statement saves a preference using ajax.
	 * NOTE: This JS is NOT wrapped in a script tag, so you should only use this TagLib from within a script tag.
	 * @attr key REQUIRED
	 * @attr value REQUIRED
	 */
	def setPreference = { attrs ->
		String key = attrs.key
		String value = attrs.value

		out << "prefs.setValue( '${key}', '${value}' );"
	}

	/**
	 * Output the value of a preference (identified by its key).
	 * NOTE: The otuputted value will NOT have quotes around it.
	 * @attr key REQUIRED
	 */
	def preferenceValue = { attrs ->
		String key = attrs.key

		Preference pref = preferenceService.load( key )
		String value = pref ? pref.value : ''
		out << value
	}

}