package bn.elicitator

/**
 * Keeps track of each time a user is emailed.
 * We keep a reference to the template which was used so that we can filter based on this, but we also store the
 * {@link EmailLog#subject} and {@link EmailLog#body} because they will be customized (with placeholders) for specific
 * users before they are sent. This way, we can see exactly what was sent (not what was supposed to be sent). This may
 * help with debugging placeholder problems (e.g. not getting replaced properly).
 *
 * I'm aware that this would grow quite large in a heavily used system, but I don't think it is an issue for a BN
 * elicitation task. Even if there were 1000 participants and we emailed each 100 times, we should probably be fine in
 * most environments.
 */
class EmailLog {

	static constraints = {
		body maxSize: EmailTemplate.CONSTRAINT_BODY_MAX_SIZE
	}

	ShiroUser recipient

	String subject

	String body

	EmailTemplate template

}
