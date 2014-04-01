package bn.elicitator.troia

abstract class ContinuousJob<T> extends Job<T> {

	ContinuousJob( String troiaServerAddress, String jobId ) {
		super( troiaServerAddress, jobId )
	}

	@Override
	protected String getAlgorithm() {
		"GALC"
	}

}
