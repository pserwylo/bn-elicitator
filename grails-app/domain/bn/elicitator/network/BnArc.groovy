package bn.elicitator.network

import bn.elicitator.Variable

class BnArc implements Arc {

	static constraints = {
		strength nullable: true
	}

	BnNode parent
	BnNode child

	float strength

    @Override
    Variable getFrom() { parent.variable }

    @Override
    Variable getTo() { child.variable }


    boolean equals( Object that ) {
        if ( that != null && that instanceof Arc ) {
            Arc arc = (Arc)that
            return arc.from.id == this.from.id && arc.to.id == this.to.id
        } else {
            return false
        }
    }
}
