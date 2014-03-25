package bn.elicitator.troia

import bn.elicitator.Probability
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

abstract class ContinuousJob extends Job {

	ContinuousJob( String troiaServerAddress ) {
		super( troiaServerAddress )
	}

	@Override
	protected String getAlgorithm() {
		"GALC"
	}

}
