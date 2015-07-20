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

	/**
	 * Didn't want to use the "equals" method, because two configurations which are equivalent feels slightly different
	 * than in the coding sense, where we consider two objects "equal".
	 * @param that
	 * @return
	 */
	boolean equivalentTo( CompatibleParentConfiguration that ) {
		if ( that == null || this.createdBy.id != that.createdBy.id ) {
			return false
		}

		def thisStates = [ this.parentState ]
		thisStates.addAll( this.otherParentStates )

		def thatStates = [ that.parentState ]
		thatStates.addAll( that.otherParentStates )

		CollectionUtils.isEqualCollection( thisStates*.id, thatStates*.id )
	}

	Set<State> allParentStates() {
		Set<State> set = [ parentState ]
		set.addAll( otherParentStates )
		return set
	}

	boolean hasState(State state) {
		for ( State s in otherParentStates ) {
			if ( s.id == state.id ) {
				return true
			}
		}
		return false
	}

	String getReadableDescription() {
		"$parentState.readableLabel, ${otherParentStates.join( ", ")}"
	}
    
    String toString() {
        "CPC( ${parentState.toConciseString()} :: ${otherParentStates.collect { it.toConciseString() }.join( ', ' ) } )"
        
    }
}
