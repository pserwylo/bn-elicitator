package bn.elicitator.anomalies

import bn.elicitator.BnService

abstract class IndirectDiagramSolution extends ProposedAnomalySolution {

	protected final BnService.IndirectRelationship relationship

	IndirectDiagramSolution(BnService.IndirectRelationship relationship) {
		this.relationship = relationship
	}

}
