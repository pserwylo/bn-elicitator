package bn.elicitator

/**
 * Both the subject and body of an email are templatable.
 * Each template has its own placeholders which may be unique to that template, but also has global placeholders which
 * are available to all emails (such as the recipients name and the link to unsubscribe).
 *
 * It is up to the {@link EmailService} to work closely with this to make sure that the relevant placeholders are
 * available at time of sending.
 *
 * @see EmailService
 */
class EmailTemplate {

	static constraints = {
	}

	static mapping = {
		description( type: "text" )
		body( type: "text" )
	}

	/**
	 * An identifier which will be used to reference this email (e.g. in javascript).
	 */
	String name

	/**
	 * Describes the point of this email, so that even if we delete the default subject and body, we know when and
	 * why the email will be sent.
	 */
	String description

	String subject

	String body

	/**
	 * Placeholders specific to this template. This template will also have {@link EmailTemplate#GLOBAL_PLACEHOLDER_NAMES}
	 * available.
	 */
	List<String> placeholderNames = []

	static final FIRST_PHASE_STARTING = "firstPhaseStarting";
	static final PHASE_COMPLETE = "phaseComplete";
	static final STUDY_COMPLETE = "studyComplete";

	static EmailTemplate getFirstPhaseStarting() { return EmailTemplate.findByName( FIRST_PHASE_STARTING ) }
	static EmailTemplate getPhaseComplete()      { return EmailTemplate.findByName( PHASE_COMPLETE       ) }
	static EmailTemplate getStudyComplete()      { return EmailTemplate.findByName( STUDY_COMPLETE       ) }

	/**
	 * @see EmailTemplate#GLOBAL_PLACEHOLDER_NAMES
	 */
	static final PH_USER = "[User]"

	/**
	 * @see EmailTemplate#GLOBAL_PLACEHOLDER_NAMES
	 */
	static final PH_LINK = "[Link]"

	/**
	 * @see EmailTemplate#GLOBAL_PLACEHOLDER_NAMES
	 */
	static final PH_UNSUBSCRIBE_LINK = "[UnsubscribeLink]"

	/**
	 * @see EmailTemplate#getFirstPhaseStarting()
	 */
	static final PH_START_DATE = "[StartDate]"

	/**
	 * @see EmailTemplate#getPhaseComplete()
	 */
	static final PH_COMPLETED_PHASE = "[CompletedPhase]"

	/**
	 * @see EmailTemplate#getPhaseComplete()
	 */
	static final PH_NEXT_PHASE = "[NextPhase]"

	/**
	 * @see EmailTemplate#getPhaseComplete()
	 */
	static final PH_EXPECTED_PHASE_DURATION = "[ExpectedPhaseDuration]"

	static final List<String> GLOBAL_PLACEHOLDER_NAMES = [ PH_USER, PH_LINK, PH_UNSUBSCRIBE_LINK ]


}
