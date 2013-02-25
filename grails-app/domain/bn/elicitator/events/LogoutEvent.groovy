package bn.elicitator.events

class LogoutEvent extends LoggedEvent {

	static void logEvent() {
		saveEvent( new LogoutEvent( description: "Logged out" ) )
	}

}
