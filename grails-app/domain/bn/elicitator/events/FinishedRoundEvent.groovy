package bn.elicitator.events

class FinishedRoundEvent extends LoggedEvent {

	static logEvent() {
		saveEvent( new FinishedRoundEvent() )
	}

	@Override
	String getDescription() {
		"Finished round $delphiPhase"
	}
}
