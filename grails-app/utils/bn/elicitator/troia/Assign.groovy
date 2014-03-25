package bn.elicitator.troia

import org.codehaus.groovy.grails.web.json.JSONObject

class Assign {

	String worker
	String object
	Object label

	public JSONObject toJSON() {
		new JSONObject(
			worker : worker,
			object : object,
			label  : new JSONObject( value : label ),
		)
	}
}


