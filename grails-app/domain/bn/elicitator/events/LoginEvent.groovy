package bn.elicitator.events

class LoginEvent extends LoggedEvent {

	static void logEvent() {
		saveEvent( new LoginEvent() )
	}

	@Override
	String getDescription() {
		"Logged in"
	}
}
