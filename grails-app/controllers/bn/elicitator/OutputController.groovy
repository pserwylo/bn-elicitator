package bn.elicitator

import bn.elicitator.output.GraphvizOutputGraph
import bn.elicitator.output.JsonOutputGraph
import bn.elicitator.output.NeticaOutputGraph
import bn.elicitator.output.OutputGraph

class OutputController {

	def variableService
	def delphiService

	def graphStats = { OutputCommand cmd ->
		outputGraph( new JsonOutputGraph(), cmd )
	}

	def graph = { OutputCommand cmd ->
		outputGraph( new GraphvizOutputGraph(), cmd )
	}

	def graphviz = { OutputCommand cmd ->
		outputGraph( new GraphvizOutputGraph( format : GraphvizOutputGraph.FORMAT_GRAPHVIZ ), cmd )
	}

	def netica = { OutputCommand cmd ->
		outputGraph( new NeticaOutputGraph(), cmd )
	}

	private outputGraph( output, OutputCommand cmd ) {

		List<Variable> variables         = Variable.list().sort()
		List<Relationship> relationships = Relationship.findAllByExistsAndDelphiPhase( true, cmd.phase ).sort()
		Integer totalUsers               = ShiroUser.list().findAll { it.roles.contains( ShiroRole.expert ) }.size()

		if ( cmd.minUsers > 0 && cmd.phase > 0 && cmd.phase <= delphiService.phase ) {
			variables.each { parent ->
				variables.each { child ->
					if ( parent != child ) {
						Integer count = relationships.count { it.parent == parent && it.child == child }
						if ( count >= cmd.minUsers ) {
							Float strength = Math.min( (Float)( count / totalUsers ), 1.0f )
							output.addEdge( parent, child, strength )
						}
					}
				}
			}
		}

		String outputString    = output.generateGraph()
		response.contentType   = output.contentType
		response.contentLength = outputString.size()
		render outputString
	}
}

class OutputCommand {

	Integer phase = AppProperties.properties.delphiPhase

	Integer minUsers = 1

	String username = null

}
