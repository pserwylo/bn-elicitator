package bn.elicitator

import grails.converters.JSON

class OutputController {

	def variableService
	def delphiService

	def graphStats = { OutputCommand cmd ->
		output( new JsonOutputGraph(), cmd )
	}

	def graph = { OutputCommand cmd ->
		output( new GraphvizOutputGraph(), cmd )
	}

	private output( OutputGraph output, OutputCommand cmd ) {

		List<Variable> variables         = Variable.list().sort()
		List<Relationship> relationships = Relationship.findAllByExistsAndDelphiPhase( true, cmd.phase ).sort()
		Integer totalUsers               = ShiroUser.list().findAll { it.roles.contains( ShiroRole.expert ) }.size()

		if ( cmd.minUsers > 0 && cmd.phase > 0 && cmd.phase <= delphiService.phase ) {
			variables.each { parent ->
				variables.each { child ->
					if ( parent != child ) {
						Integer count    = relationships.count { it.parent == parent && it.child == child }
						if ( count >= cmd.minUsers ) {
							Float   strength = (Float)( count / totalUsers )
							output.addEdge( parent, child, strength )
						}
					}
				}
			}
		}

		String outputString = output.generateGraph()
		response.contentType = output.contentType
		response.contentLength = outputString.size()
		render outputString

	}

	private void saveImage( OutputGraph graph ) {

	}

}

abstract class OutputGraph {
	abstract void addEdge( Variable parent, Variable child, Float strength );
	abstract String generateGraph();
	abstract String getContentType();
	abstract String getFileExtension();
}

class JsonOutputGraph extends OutputGraph {

	private Integer totalEdges = 0
	Set<Variable> variables = []

	@Override
	void addEdge(Variable parent, Variable child, Float strength) {
		totalEdges ++
		variables.add( parent )
		variables.add( child )
	}

	@Override
	String generateGraph() {
		return [ totalNodes: variables.size(), totalEdges : totalEdges ] as JSON
	}

	@Override
	String getContentType() {
		return "application/json"
	}

	@Override
	String getFileExtension() {
		return ".json"
	}
}

class GraphvizOutputGraph extends OutputGraph {

	private TreeSet<Variable> variables = []
	private List<GraphvizEdge> edges = []

	@Override
	void addEdge( Variable parent, Variable child, Float strength ) {
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

			Process dotProcess = new ProcessBuilder().command( "dot", "-Tsvg", file.absolutePath ).start()
			BufferedInputStream input = new BufferedInputStream( dotProcess.inputStream )
			dotProcess.waitFor()
			svg = input.readLines().join( "\n" )
		} catch ( Exception ioe ) {
		} finally {
			file?.delete()
		}
		return svg
	}

	@Override
	String getContentType() {
		return "image/svg+xml"
	}

	@Override
	String getFileExtension() {
		return ".svg"
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

abstract class GraphvizItem {

	private Map<String,String> attributes = [:]

	protected addAttribute( String key, String value ) {
		attributes.put( key, value )
	}

	protected addQuotedAttribute( String key, String value ) {
		addAttribute( key, '"' + value + '"' )
	}

	protected String getAttributesString() {
		return "[" + attributes.collect { it.key + "=" + it.value }.join( "," ) + "]"
	}

}

public class GraphvizEdge extends GraphvizItem{
	Variable parent
	Variable child
	Float strength

	String getColour() {
		String hex = "123456789ab"
		Integer i = strength * ( hex.length() - 1 )
		String num = ( [ hex.charAt( i ) ] * 6 ).join()
		return "#" + num
	}

	String toString() {
		addAttribute( "weight", ((Integer)( strength * 10 )).toString() )
		addQuotedAttribute( "color", colour )
		return "\t$parent.label -> $child.label $attributesString\n"
	}
}

public class GraphvizNode extends GraphvizItem {

	Variable variable
	Integer maxStringLength = 10

	String generateLabel() {
		List<String> lines = []
		List<String> words = variable.readableLabel.split( " " )
		String currentLine = ""
		words.each {
			if ( currentLine.length() + it.length() < maxStringLength ) {
				if ( currentLine.length() != 0 ) {
					currentLine += " "
				}
				currentLine += it
			} else {
				lines.add( currentLine )
				currentLine = it
			}
		}
		lines.add( currentLine )
		return lines.join( '\\n' )
	}

	String toString() {
		addQuotedAttribute( "label", generateLabel() )
		if ( variable.variableClass == VariableClass.problem ) {
			addAttribute( "style", "filled" )
			addQuotedAttribute( "color", "#333333" )
			addQuotedAttribute( "fillcolor", "#DDDDDD" )
		}
		return "\t$variable.label $attributesString\n"
	}

}

class OutputCommand {

	Integer phase = AppProperties.properties.delphiPhase

	Integer minUsers = 1

	String username = null

}
