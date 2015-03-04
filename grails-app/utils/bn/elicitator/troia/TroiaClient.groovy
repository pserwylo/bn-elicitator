package bn.elicitator.troia

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Connects to the Troia server which is an implementation of Dawid Skene.
 */
class TroiaClient {

	private RESTClient restClient

	public TroiaClient( String address ) {
		restClient = new RESTClient( address + "/"	 )
	}

	TroiaResponse get( String path ) {
		doGet( path )
	}

	TroiaResponse getFollowRedirects( String path ) {
		def response = doGet( path )
		while ( response.redirect ) {
			response = doGet( response.redirect )
		}
		return response
	}

	TroiaResponse ensureResponseIsReady( TroiaResponse response ) {
		int tryCount = 1
		while ( !response.responseReady ) {
			int timeToSleep = Math.min( tryCount * 1 * 1000, 30000 )
			// print "Response at '$response.path' not ready. Sleeping for ${timeToSleep / 1000} seconds, then trying again..."
			sleep( timeToSleep )
			tryCount ++;
			response = doGet( response.path )
		}
		return response
	}

	TroiaResponse getUntilReady( String path ) {
		ensureResponseIsReady( doGet( path ) )
	}

	TroiaResponse post( String path, JSONObject data = null ) {
		doPost( path, data )
	}

	private TroiaResponse doPost( String path, JSONObject data ) {
		try {
			return new TroiaResponse(
				path,
				(HttpResponseDecorator) restClient.post(
					path               : path,
					requestContentType : "application/json",
					body               : data ? data.toString() : "",
				)
			)
		} catch ( HttpResponseException e ) {
			// print "Exception loading '$path': $e.message"
			throw e
		}
	}

	private TroiaResponse doGet( String path ) {
		new TroiaResponse(
			path,
			(HttpResponseDecorator)restClient.get(
				path : path,
			)
		)
	}

}


