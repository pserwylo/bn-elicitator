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
}
