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

    protected Map<String, Double> getCategoryPriors() { [:] }

	@Override
	protected void appendDataForJobCreation( JSONObject data ) {
		data.put( "categories", new JSONArray( categories ) )

        def priors = categoryPriors.collect { Map.Entry<String, Double> it ->
            new JSONObject( [ "categoryName" : it.key, "value" : it.value ] )
        }

        if ( priors.size() > 0 ) {
            data.put( "categoryPriors", new JSONArray( priors ) )
        }

	}

	@Override
	protected Object prepareAssignLabel( Object label ) {
		label
	}

	protected abstract Collection getCategories();

}
