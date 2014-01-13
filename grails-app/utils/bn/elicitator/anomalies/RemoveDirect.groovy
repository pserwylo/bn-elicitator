package bn.elicitator.anomalies

import bn.elicitator.BnService

class RemoveDirect extends IndirectDiagramSolution {

	RemoveDirect(BnService.IndirectRelationship relationship) {
		super(relationship)
	}

	@Override
	String getDescription() {
		"Remove the direct relationship, leaving the indirect relationships"
	}

	@Override
	String getEncodedSolutionImage() {
		renderer.imageData()
	}

	private static class Renderer extends IndirectDiagramRenderer {
		protected String getDot() {

		}
	}

}
