package bn.elicitator.events

import bn.elicitator.Relationship

/**
 * In later rounds, we become interested in how many other people have commented when the person changed their mind.
 */
class SaveRelationshipLaterRoundEvent extends SaveRelationshipEvent {

	Boolean hasChangedMind
	Integer numOthersWhoAgreeNow
	Integer totalOthers
	Integer numExistsComments
	Integer numDoesntExistComments

	static logEvent(
		Relationship relationship,
		Boolean hasChangedMind,
		Integer numOthersWhoAgreeNow,
		Integer totalOthers,
		Integer numExistsComments,
		Integer numDoesntExistComments ) {

		def event = new SaveRelationshipLaterRoundEvent(
			parent                       : relationship.parent,
			child                        : relationship.child,
			comment                      : relationship.comment?.comment,
			doesRelationshipExist        : relationship.exists,
			hasChangedMind               : hasChangedMind,
			numOthersWhoAgreeNow         : numOthersWhoAgreeNow,
			totalOthers                  : totalOthers,
			numExistsComments            : numExistsComments,
			numDoesntExistComments       : numDoesntExistComments,
		)
		saveEvent( event )
	}

	@Override
	String getDescription() {
		String desc        = super.getDescription()
		String changedMind = hasChangedMind ? "changed mind" : "updated comment"
		String now         = hasChangedMind ? "now" : ""
		String others      = "$numOthersWhoAgreeNow of $totalOthers $now agree"

		return "$desc ($others), $changedMind when pondering $numExistsComments+ve/$numDoesntExistComments-ve comments"
	}
}
