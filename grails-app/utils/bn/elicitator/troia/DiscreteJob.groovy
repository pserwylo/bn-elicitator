package bn.elicitator.troia

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

abstract class DiscreteJob<T> extends Job<T> {

	DiscreteJob( String troiaServerAddress, String jobId ) {
		super( troiaServerAddress, jobId )
	}

	@Override
	protected String getAlgorithm() {
		"BDS"
	}

	@Override
	protected void appendDataForJobCreation( JSONObject data ) {
		data.put( "categories", new JSONArray( categories ) )
	}

	@Override
	protected Object prepareAssignLabel( Object label ) {
		label
	}

	protected abstract Collection getCategories();

}
