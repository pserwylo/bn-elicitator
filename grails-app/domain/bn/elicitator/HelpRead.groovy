package bn.elicitator

/**
 * Keeps track of whether or not the logged in user has read a specific help message.
 */
class HelpRead {

	static constraints = {
	}

	String messageHash
	ShiroUser readBy

}
