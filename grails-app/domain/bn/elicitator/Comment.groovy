package bn.elicitator

/**
 * Used to allow participants to specify why they chose what they chose.
 * Includes flags which let administrators moderate discussion.
 */
class Comment {

	static constraints = {
		hiddenBy nullable: true
	}

	String comment

	ShiroUser createdBy

	Date createdDate

	ShiroUser lastModifiedBy

	Date lastModifiedDate

	/**
	 * Allows moderators to hide comments, either because they are innappropriate, or because they are repeating an
	 * argument that others made.
	 */
	boolean isHidden = false

	String reasonForHiding = ""

	ShiroUser hiddenBy = null


}
