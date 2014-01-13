package bn.elicitator.anomalies

import bn.elicitator.BnService.IndirectRelationship
import bn.elicitator.Variable

class KeepSingleChainDiagram extends IndirectDiagramRenderer {

	private List<Variable> chainToKeep

	public KeepSingleChainDiagram(IndirectRelationship relationship, List<Variable> chainToKeep) {
		super(relationship)
		this.chainToKeep = chainToKeep
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

}