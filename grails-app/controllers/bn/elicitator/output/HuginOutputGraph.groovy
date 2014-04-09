package bn.elicitator.output
import bn.elicitator.State
import groovy.xml.MarkupBuilder

class HuginOutputGraph extends SerializedBnGraph<GeNIeNode> {

	@Override
	GeNIeNode createNode() { new GeNIeNode() }

	@Override
	String generateGraph() {

		def bnNodes = getNodes()

		def writer = new StringWriter()
		def xml    = new MarkupBuilder( writer )
		xml.smile( version : '1.0', id : 'V1' ) {
			nodes {

				bnNodes.each() { GeNIeNode n ->

					cpt( id : n.family.variable.label ) {

						n.family.variable.states.each() { State s ->
							state( id : s.label )
						}

						if ( n.family.parents.size() > 0 ) {
							parents( n.family.parents*.label.join( ' ' ) )
						}

						probabilities( n.generateProbs() )

					}

				}

			}

			extensions {
				genie( version : '1.0', app : 'bn-elicitator', name : 'Bayesian Network', faultnameformat : 'nodestate' ) {

					bnNodes.each() { GeNIeNode n ->

						node( id : n.family.variable.label ) {
							name( n.family.variable.readableLabel )
							interior( color : "ffffff" )
							outline( color : "444444" )
							font( color : "333333", name : "Arial", size : 8 )
							position( "${(int)n.location.x} ${(int)n.location.y} ${(int)n.location.x + 76} ${(int)n.location.y + 36}")
						}

					}

				}
			}
		}

		writer.toString()
	}

	@Override
	String getContentType() {
		return "text/xml"
	}

	@Override
	String getFileExtension() {
		return ".xdsl"
	}
}
