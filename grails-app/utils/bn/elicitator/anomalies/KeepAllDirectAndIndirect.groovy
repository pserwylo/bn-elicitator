package bn.elicitator.anomalies

import bn.elicitator.BnService

class KeepAllDirectAndIndirect extends IndirectDiagramSolution {

	KeepAllDirectAndIndirect(BnService.IndirectRelationship relationship) {
		super(relationship)
	}

	@Override
	String getDescription() {
		"Keep both the direct relationship, and all indirect relationships"
	}

	@Override
	String getEncodedSolutionImage() {
		new Renderer().imageData()
	}

	private static class Renderer extends IndirectDiagramRenderer {

		Renderer(BnService.IndirectRelationship relationship) {
			super(relationship)
		}

		protected String getDot() {

		}
	}
}
