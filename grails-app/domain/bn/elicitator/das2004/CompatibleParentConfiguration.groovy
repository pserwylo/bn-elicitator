package bn.elicitator.das2004

import bn.elicitator.State
import bn.elicitator.Variable

class CompatibleParentConfiguration {

    static constraints = {
    }

	static hasMany = [ parentStates : State ]

	Variable child

}
