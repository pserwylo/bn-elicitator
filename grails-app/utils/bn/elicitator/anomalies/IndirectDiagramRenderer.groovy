package bn.elicitator.anomalies

import bn.elicitator.BnService.IndirectRelationship
import bn.elicitator.Variable
import bn.elicitator.graphics.GraphvizPngImage

abstract class IndirectDiagramRenderer {

	private static final String COLOUR_HIGHLIGHT = "#ff2222";
	private static final String COLOUR_NORMAL    = "#222222";

	protected final IndirectRelationship relationship;

	IndirectDiagramRenderer(IndirectRelationship relationship) {
		this.relationship = relationship
	}

	abstract protected String getDot()

	public final String imageData() {
		new GraphvizPngImage(dot).generate()
	}

	protected final String highlightedChain( List<Variable> vars ) {
		chainDot( vars, COLOUR_HIGHLIGHT )
	}

	protected final String chain( List<Variable> vars ) {
		chainDot( vars, COLOUR_NORMAL );
	}

	private String chainDot( List<Variable> vars, String colour ) {
		"${vars.join( ' -> ' )} [color=$colour; penwidth=3]"
	}
}
