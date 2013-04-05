package bn.elicitator.output

class GraphvizDotOutputGraph extends GraphvizOutputGraph {

	@Override
	String generateGraph() {
		generateDot()
	}

	@Override
	String getContentType() {
		"text/plain"
	}

	@Override
	String getFileExtension() {
		"dot"
	}

}
