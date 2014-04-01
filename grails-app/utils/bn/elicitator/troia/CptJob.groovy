package bn.elicitator.troia

import bn.elicitator.Probability
import bn.elicitator.State

class CptJob extends ContinuousJob<DawidSkeneProbability> {

	CptJob( String troiaServerAddress, String jobId = null ) {
		super( troiaServerAddress, jobId )
	}

	@Override
	protected List<Assign> getAssigns() {

		def probabilities = Probability.list()

		print "Loading ${probabilities.size()} probabilities into Troia..."

		probabilities.collect { probability ->
			new Assign(
				worker : probability.createdBy.id,
				object : probabilityToObject( probability ),
				label  : probability.probability
			)
		}

	}

	private String probabilityToObject( Probability probability ) {
		"$probability.childState.id|${probability.parentStates*.id.join(',')}"
	}

	private Probability objectToProbability( String object ) {

		Probability probability = new Probability()

		String[] parts = object.split( /\|/ )
		int childStateId = parts[ 0 ] as Integer
		probability.childState = State.get( childStateId )

		if ( parts.size() > 1 ) {
			List<Long> parentStateIds = parts[ 1 ].split( "," ).collect { it as Long }
			probability.parentStates = State.findAllByIdInList( parentStateIds ).toSet()
		}

		return probability

	}


	// est_zeta, est_value, distributionMu, distributionSigma
	// object "1|3,5"
	@Override
	protected DawidSkeneProbability predictionFromData(Object data) {
		Probability probability = objectToProbability( data.object as String )
		probability.probability = data.prediction.est_value;
		new DawidSkeneProbability( probability : probability )
	}
}
