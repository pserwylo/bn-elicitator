package bn.elicitator

import bn.elicitator.auth.Role
import bn.elicitator.auth.User
import bn.elicitator.output.GraphvizDotOutputGraph
import bn.elicitator.output.GraphvizOutputGraph
import bn.elicitator.output.HtmlMatrixOutputGraph
import bn.elicitator.output.JsonMatrixOutputGraph
import bn.elicitator.output.JsonOutputGraph
import bn.elicitator.output.NeticaOutputGraph
import bn.elicitator.output.OutputGraph

class OutputController {

	def variableService
	def delphiService
	def userService

	def jsonStats = { OutputCommand cmd ->
		outputGraph( new JsonOutputGraph(), cmd )
	}

	def jsonMatrix = { OutputCommand cmd ->
		outputGraph( new JsonMatrixOutputGraph(), cmd )
	}

	def htmlMatrix = { HtmlMatrixOutputCommand cmd ->
		outputGraph( new HtmlMatrixOutputGraph( cellSize: cmd.cellSize ), cmd )
	}

	def svgDiagram = { OutputCommand cmd ->
		outputGraph( new GraphvizOutputGraph(), cmd )
	}

	def graphviz = { OutputCommand cmd ->
		outputGraph( new GraphvizDotOutputGraph(), cmd )
	}

	def netica = { OutputCommand cmd ->
		outputGraph( new NeticaOutputGraph(), cmd )
	}

	private outputGraph( OutputGraph output, OutputCommand cmd ) {

		List<Variable> variables         = Variable.list().sort()
		List<Relationship> relationships = Relationship.findAllByExistsAndDelphiPhase( true, cmd.phase ).sort()
		Integer totalUsers               = userService.expertCount

		if ( cmd.username ) {
			User user = User.findByUsername( cmd.username )
			if ( !user ) {
				render "Could not find user $cmd.username."
				return
			}
			relationships.findAll { it.createdBy = user }
			totalUsers = 1
		}

		output.allVariables = variables

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

class HtmlMatrixOutputCommand extends OutputCommand{

	Integer cellSize = 5

}
