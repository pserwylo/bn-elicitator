package bn.elicitator.network

class BnArc {

	static constraints = {
		strength nullable: true
	}

	BnNode parent
	BnNode child

	float strength

}
