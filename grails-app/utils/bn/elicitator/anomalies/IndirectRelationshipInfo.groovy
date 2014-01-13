package bn.elicitator.anomalies

import bn.elicitator.BnService

class IndirectRelationshipInfo implements AnomalyInfo {

	protected final BnService.IndirectRelationship relationship

	private String encodedImage = null;

	public IndirectDiagram(BnService.IndirectRelationship relationship) {
		this.relationship = relationship
	}

	@Override
	String getDescription() {
		"""
		There is more than one way in which <em>$relationship.redundantParent.readableLabel</em>
		influences <em>$relationship.child.readableLabel</em>.
		Please choose one of the following options:
		"""
	}

	@Override
	String getEncodedProblemImage() {
		if (encodedImage == null) {
			encodedImage = new Renderer(relationship).imageData()
		}
	}

	List<ProposedAnomalySolution> getSolutions() {
		List<IndirectDiagramSolution> possibleSolutions = [
				new KeepAllDirectAndIndirect(relationship)
		]
	}

	private static class Renderer extends IndirectDiagramRenderer {
		Renderer(BnService.IndirectRelationship relationship) {
			super(relationship)
		}

		protected String getDot() {

		}
	}
}
