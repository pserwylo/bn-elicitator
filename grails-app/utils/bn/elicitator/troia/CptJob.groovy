package bn.elicitator.troia

import bn.elicitator.Probability

class CptJob extends ContinuousJob {

	CptJob( String troiaServerAddress ) {
		super( troiaServerAddress )
	}

	@Override
	protected List<Assign> getAssigns() {

		def probabilities = Probability.list()

		print "Loading ${probabilities.size()} probabilities into Troia..."

		probabilities.collect { probability ->
			new Assign(
				worker : probability.createdBy.id,
				object : "$probability.childState.id|${probability.parentStates*.id.join(',')}",
				label  : probability.probability
			)
		}

	}

}
