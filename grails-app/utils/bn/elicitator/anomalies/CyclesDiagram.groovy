
package bn.elicitator.anomalies

import bn.elicitator.BnService.CyclicalRelationship
import bn.elicitator.Variable

abstract class CycleDiagramRenderer {

	abstract protected String getDot()

}

class CycleDiagram extends CycleDiagramRenderer {

	private static final String COLOUR_HIGHLIGHT = "#ff2222";
	private static final String COLOUR_NORMAL    = "#222222";

	private CyclicalRelationship relationship

	public CycleDiagram(CyclicalRelationship relationship) {
		this.relationship = relationship
	}

	protected String getDot() {
		String dot = "digraph {"

		dot += " $relationship.redundantParent -> $relationship.child; "

		relationship.chains.each {
			dot += chain( it ) + "; ";
		}

		dot += "}"
		return dot
	}

	private String highlightedChain( List<Variable> vars ) {
		chainDot( vars, COLOUR_HIGHLIGHT )
	}

	private String chain( List<Variable> vars ) {
		chainDot( vars, COLOUR_NORMAL );
	}

	private String chainDot( List<Variable> vars, String colour ) {
		Integer.toString(1, 16)
		"${vars.join( ' -> ' )} [color=$colour; penwidth=3]"
	}

}
