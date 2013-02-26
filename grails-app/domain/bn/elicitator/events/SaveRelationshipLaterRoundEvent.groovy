package bn.elicitator.events

import bn.elicitator.Relationship

/**
 * In later rounds, we become interested in how many other people have commented when the person changed their mind.
 */
class SaveRelationshipLaterRoundEvent extends SaveRelationshipEvent {

	Boolean hasChangedMind
	Integer numOthersWhoAgreedPreviously
	Integer totalOthers
	Integer numExistsComments
	Integer numDoesntExistComments

	static logEvent(
		Relationship relationship,
		Boolean hasChangedMind,
		Integer numOthersWhoAgreedPreviously,
		Integer totalOthers,
		Integer numExistsComments,
		Integer numDoesntExistComments ) {

		def event = new SaveRelationshipLaterRoundEvent(
			parent                       : relationship.parent,
			child                        : relationship.child,
			comment                      : relationship.comment?.comment,
			doesRelationshipExist        : relationship.exists,
			hasChangedMind               : hasChangedMind,
			numOthersWhoAgreedPreviously : numOthersWhoAgreedPreviously,
			totalOthers                  : totalOthers,
			numExistsComments            : numExistsComments,
			numDoesntExistComments       : numDoesntExistComments,
		)
		saveEvent( event )
	}

	Integer getNumOthersWhoAgreeNow() {
		totalOthers - numOthersWhoAgreedPreviously
	}

	Integer totalComments() {
		numExistsComments + numDoesntExistComments
	}

	@Override
	String getDescription() {
		String desc        = super.getDescription()
		String changedMind = hasChangedMind ? "changed mind" : "updated comment"
		String now         = hasChangedMind ? "now" : ""
		String others      = "$numOthersWhoAgreeNow of $totalOthers $now agree"
		String comments    = "had $numExistsComments exists and $numDoesntExistComments comments to view"

		return "$desc, $changedMind when $comments ($others)"
	}
}
