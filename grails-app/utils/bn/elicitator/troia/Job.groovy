package bn.elicitator.troia

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

abstract class Job<T> {

	private TroiaClient client
	private String id

	public Job( String troiaServerAddress, String jobId ) {
		client = new TroiaClient( troiaServerAddress )
		id     = jobId
	}

	abstract protected String getAlgorithm()
	abstract protected List<Assign> getAssigns()
	abstract protected T predictionFromData(Object data)

	public String getId() {
		this.id
	}

	public void run() {
		start()
		loadAssigns()
		compute()
	}

	List<T> predictions() {
		def response = client.getFollowRedirects( "jobs/$id/objects/prediction" )
		response = client.ensureResponseIsReady( response )
		response.response.result.collect { predictionFromData( it ) }
	}

	private void start() {
		def response = client.post( "jobs", new JSONObject( 'algorithm': algorithm ) )
		id = extractJobId( response )
	}

	private void compute() {
		def response = client.post( "jobs/$id/compute" )

		// Block until computation completed...
		client.getUntilReady( response.redirect )
	}

	private String extractJobId( TroiaResponse response ) {
		def matcher = response.result =~ /New job created with ID: (.*)/
		if ( matcher.count != 1 )
			throw new TroiaResponse.MalformedResponseException( response, 'Could not find Job ID in result from server. Expected "New job created with ID: blah", but got "' + response.result + '".' )
		return matcher[ 0 ][ 1 ]
	}

	private void loadAssigns() {

		// TODO: Change to "collect" instead of create + .each + .add ...
		JSONArray jsonAssigns = new JSONArray()
		assigns.each { Assign it -> jsonAssigns.add( it.toJSON() ) }

		def response = client.post( "jobs/$id/assigns", new JSONObject( assigns : jsonAssigns ) )

		// We aren't interested in the response, other than the fact we successfully managed to
		// block until it was completed. Also, it will have thrown an exception along the way if we got an error
		// status back at any point.
		client.getUntilReady( response.redirect )

	}
}
