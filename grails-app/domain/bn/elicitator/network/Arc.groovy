package bn.elicitator.network

class Arc {

	static constraints = {
		strength nullable: true
	}

	Node parent
	Node child

	float strength

}
