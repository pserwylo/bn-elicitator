package bn.elicitator.output

class HuginOutputGraph extends SerializedBnGraph<HuginNode> {

	@Override
	HuginNode createNode() { new HuginNode() }

	@Override
	String generateGraph() {

		def bnNodes = getNodes()

		String output = """net
{
	node_size = (76 36)
}
"""

		bnNodes.each {
			output += it.nodeString
		}

		bnNodes.each {
			output += it.probabilityString
		}

		return output
	}

	@Override
	String getContentType() {
		return "text/plain"
	}

	@Override
	String getFileExtension() {
		return ".net"
	}
}
