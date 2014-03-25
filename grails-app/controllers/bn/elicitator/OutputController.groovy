package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode
import bn.elicitator.output.CptOutputSpreadsheet
import bn.elicitator.output.CsvOutputGraph
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
	def bnService

	/**
	 * Outputs csv's of each users probability estimations for each question (child + parent states) they were allocated.
	 */
	def cpts = {
		render new CptOutputSpreadsheet()
	}

	def jsonStats = { OutputCommand cmd ->
		outputGraph( new JsonOutputGraph(), cmd )
	}

	def jsonMatrix = { OutputCommand cmd ->
		outputGraph( new JsonMatrixOutputGraph(), cmd )
	}

	def csv = { OutputCommand cmd ->
		outputGraph( new CsvOutputGraph(), cmd )
	}

	def htmlMatrix = { HtmlMatrixOutputCommand cmd ->
		outputGraph( new HtmlMatrixOutputGraph( cellSize: cmd.cellSize ), cmd )
	}

	def svgDiagram = { OutputCommand cmd ->
		outputGraph( new GraphvizOutputGraph(), cmd )
	}

	def graphviz = { OutputCommand cmd ->
		outputGraph( new GraphvizOutputGraph( format : GraphvizOutputGraph.FORMAT_GRAPHVIZ ), cmd )
	}

	def netica = { OutputCommand cmd ->
		outputGraph( new NeticaOutputGraph(), cmd )
	}

	def comments = { OutputCommentsCommand cmd ->

		StringBuilder output = new StringBuilder()
		List<Relationship> relationships
		if ( cmd.phase > 0 ) {
			relationships = Relationship.findAllByDelphiPhase( cmd.phase )
		} else {
			relationships = Relationship.list()
		}

		relationships.sort { rel1, rel2 ->
			if ( rel1.delphiPhase == rel2.delphiPhase ) {
				rel1.createdBy.id <=> rel2.createdBy.id
			} else {
				rel1.delphiPhase <=> rel2.delphiPhase
			}
		}

		List<VisitedVariable> visitedVariables = VisitedVariable.list()

		relationships.each {
			boolean hasSeen = visitedVariables.find { v ->
				v.visitedBy   == it.createdBy   &&
				v.delphiPhase == it.delphiPhase &&
				v.variable    == it.child
			}
			if ( hasSeen ) {
				Comment comment = it.getMostRecentComment( it.delphiPhase )
				String commentText = ( comment?.comment?.length() > 0 ) ? ( '"""' + comment.comment + '"""' ) : ""

				// If it is a later round, they should have been forced to see a comment.
				// Therefore we assume that if there is no comment, then they weren't ever shown
				// this relationship...
				boolean wasShown = it.delphiPhase == 1 || commentText != ""

				if ( wasShown ) {
					output.append "$it.delphiPhase,$it.createdById,$it.exists,$it.parent.label,$it.child.label,$commentText\n"
				}
			}
		}

		response.contentType   = "text/csv"
		response.contentLength = output.size()
		render output.toString()
	}

	private outputGraph( OutputGraph output, OutputCommand cmd ) {

		List<Variable> variables = Variable.list().sort()
		output.allVariables = variables

		if ( cmd.finalNetwork ) {

			List<BnArc> arcs = BnArc.list()
			arcs.each { BnArc arc ->
				output.addEdge( arc.parent.variable, arc.child.variable )
			}

			BnNode.list().each { BnNode node ->
				Cpt cpt = bnService.getCptFor( node.variable, null )
				output.addCpt( cpt )
			}

		} else if ( cmd.phase > 0 && cmd.phase <= delphiService.phase ) {

			List<Relationship> relationships       = Relationship.findAllByExistsAndDelphiPhase( true, cmd.phase ).sort()
			List<VisitedVariable> visitedVariables = VisitedVariable.findAllByDelphiPhase( cmd.phase )

			if ( cmd.username ) {

				User user = User.findByUsername( cmd.username )
				if ( !user ) {
					render "Could not find user $cmd.username."
					return
				}
				relationships = relationships.findAll { it.createdBy = user }

			} else if ( cmd.onlyCompleted ) {
				relationships = relationships.findAll { relationship ->
					visitedVariables.find { visited ->
						visited.visitedBy.id == relationship.createdBy.id && visited.variable.id == relationship.child.id
					} ? true : false
				}
			}

			List<StructureAllocation> allocations = StructureAllocation.list()
			variables.each { child ->
				List<User> childVisitedBy = visitedVariables.findAll { it.variable == child }*.visitedBy
				int totalUsers
				if ( cmd.onlyCompleted ) {
					totalUsers = allocations.count { allocation-> allocation.variables.contains( child ) }
				} else {
					totalUsers = visitedVariables.count { it.delphiPhase == cmd.phase && it.variable == child }
				}
				variables.each { parent ->
					if ( parent != child ) {
						Integer count = relationships.count {
							it.parent == parent &&
							it.child  == child  &&
							childVisitedBy.contains( it.createdBy )
						}
						if ( count >= cmd.minUsers ) {
							Float strength
							if ( totalUsers == 0 ) {
								strength = 0
							} else {
								strength = Math.min( (Float)( count / totalUsers ), 1.0f )
							}
							output.addEdge(parent, child, strength, count, totalUsers)
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

	Boolean finalNetwork = false

	Integer phase = AppProperties.properties.delphiPhase

	Integer minUsers = 1

	String username = null

	Boolean onlyCompleted = false

}

class OutputCommentsCommand {

	Integer phase = 0
	String username = null

}

class HtmlMatrixOutputCommand extends OutputCommand{

	Integer cellSize = 5

}
