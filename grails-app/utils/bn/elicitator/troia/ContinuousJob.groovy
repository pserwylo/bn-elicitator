package bn.elicitator.troia

import org.codehaus.groovy.grails.web.json.JSONObject

abstract class ContinuousJob<T> extends Job<T> {

	ContinuousJob( String troiaServerAddress, String jobId ) {
		super( troiaServerAddress, jobId )
	}

	@Override
	protected String getAlgorithm() {
		"GALC"
	}

	@Override
	protected void appendDataForJobCreation( JSONObject data ) {

	}

	@Override
	protected Object prepareAssignLabel( Object label ) {
		new JSONObject( value : label )
	}

	Map<Long, Double> estimatedWorkerQuality() {
		throw new UnsupportedOperationException( "Continuous jobs don't support estimations of worker quality." )
	}
}
