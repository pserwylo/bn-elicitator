package bn.elicitator.output

class NeticaOutputGraph extends SerializedBnGraph<NeticaNode> {

    @Override
    NeticaNode createNode() { new NeticaNode() }

	@Override
	String generateGraph() {

		List<NeticaNode> nodes = getNodes()

		NeticaNodeAttributes bnAttributes       = new NeticaNodeAttributes()
		NeticaNodeAttributes bnVisualAttributes = new NeticaNodeAttributes()
		bnAttributes.addQuotedAttribute( "title", "Bayesian Network" )
		bnAttributes.addQuotedAttribute( "comment", "" )
		bnVisualAttributes.addAttribute( "defdispform", "LABELBOX" )

		String output = """// ~->[DNET-1]->~
bnet BayesianNetwork {
	$bnAttributes
		visual V1 {
			$bnVisualAttributes
		};

	${nodes.join( "\n\t" )}
};
"""

		return output
	}

	@Override
	String getContentType() {
		return "text/plain"
	}

	@Override
	String getFileExtension() {
		return ".bne"
	}
}
