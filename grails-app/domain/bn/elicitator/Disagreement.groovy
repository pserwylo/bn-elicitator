package bn.elicitator

/**
 * Caches information about how many relationships somebody disagrees with (in regards to the rest of the participants).
 * This is because it took too long to iterate over every single relationship on each page load to see where people
 * disagreed. It will be updated when somebody updates a relationship.
 */
class Disagreement {

	static constraints = {
	}

	Variable child

	Integer disagreeCount

	Integer totalCount

	ShiroUser createdBy

	Integer delphiPhase

}
