package bn.elicitator.output

import bn.elicitator.Variable

class GraphvizFormat {
	String cliSwitch
	String fileExtension
	String contentType
}

class GraphvizOutputGraph extends OutputGraph {

	static final FORMAT_SVG      = new GraphvizFormat( cliSwitch : "-Tsvg", contentType : "image/svg+xml", fileExtension : ".svg" )
	static final FORMAT_GRAPHVIZ = new GraphvizFormat( cliSwitch : "",      contentType : "text/plain",    fileExtension : ".dot" )

	private TreeSet<Variable> variables = []
	private List<GraphvizEdge> edges = []

	GraphvizFormat format = FORMAT_SVG

	@Override
	void addEdge(Variable parent, Variable child, Float strength, Integer numUsers, Integer totalUsers) {
		edges.add( new GraphvizEdge( parent : parent, child : child, strength : strength ) )
		variables.add( parent )
		variables.add( child )
	}

	@Override
	String generateGraph() {
		String svg = ""
		File file = null
		try {
			file = File.createTempFile( "bn-dot-", ".dot" )
			file.write( generateDot() );

			Process dotProcess = new ProcessBuilder().command( "dot", format.cliSwitch, file.absolutePath ).start()
			BufferedInputStream input = new BufferedInputStream( dotProcess.inputStream )
			dotProcess.waitFor()
			svg = input.readLines().join( "\n" )
		} catch ( Exception ioe ) {
			println ioe
		} finally {
			file?.delete()
		}
		return svg
	}

	@Override
	String getContentType() {
		return format.contentType
	}

	@Override
	String getFileExtension() {
		return format.fileExtension
	}

	private String generateDot() {
		String dot = "digraph BayesianNetwork {\n";
		dot += "\n"
		variables.each { dot += new GraphvizNode( variable: it ) }
		dot += "\n"
		edges.each { dot += it }
		dot += "\n"
		dot += "}";
		return dot
	}

}
