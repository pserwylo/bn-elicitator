package bn.elicitator

import bn.elicitator.auth.User

class ReviewedRelationship {

	static constraints = {
	}

	Relationship relationship
	User         reviewedBy
	Integer      delphiPhase

}
