package bn.elicitator.das2004

import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.auth.User
import org.apache.commons.collections.CollectionUtils

class CompatibleParentConfiguration {

    static constraints = {
    }

	static hasMany = [ otherParentStates : State ]

	State parentState
	User  createdBy

	boolean equals( CompatibleParentConfiguration that ) {
		if ( that == null || this.createdBy != that.createdBy ) {
			return false
		}

		def thisStates = [ this.parentState ]
		thisStates.addAll( this.otherParentStates )

		def thatStates = [ that.parentState ]
		thatStates.addAll( that.otherParentStates )

		CollectionUtils.isEqualCollection( thisStates, thatStates )
	}

	boolean hasState(State state) {
		for ( State s in otherParentStates ) {
			if ( s.id == state.id ) {
				return true
			}
		}
		return false
	}
}
