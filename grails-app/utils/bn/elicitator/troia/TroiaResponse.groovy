package bn.elicitator.troia

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.codehaus.groovy.grails.web.json.JSONObject

class TroiaResponse {

	static class MalformedResponseException extends RuntimeException {

		private TroiaResponse response

		def MalformedResponseException( TroiaResponse response, String message ) {
			super(message)
			this.response = response
		}

		TroiaResponse getResponse() { this.response }
	}

	String timestamp
	String redirect
	String result
	HttpResponseDecorator response
	String path
	double executionTime = -1
	boolean responseReady

	public TroiaResponse( String path, HttpResponseDecorator response ) {

		this.path     = path
		this.response = response

		if ( response.data == null )
			throw new MalformedResponseException( this, "No data present in response from server" )

		if ( !( response.data instanceof Map ) )
			throw new MalformedResponseException( this, "Invalid data returned by server (expected a Map)" )

		Map data = (Map)response.data

		if ( !data.containsKey( "status" ) )
			throw new MalformedResponseException( this, "Expected 'status' to be returned from server" )

		if ( data.status == "OK" )
			responseReady = true
		else if ( data.status == "NOT_READY" )
			responseReady = false
		else
			throw new MalformedResponseException( this, "Status is not $data.status (expected 'OK' or 'NOT_READY'): $data.result" )

		if ( data.containsKey( "redirect" ) )
			redirect = data.redirect

		if ( data.containsKey( "executionTime" ) )
			executionTime = data.executionTime

		if ( data.containsKey( "result" ) )
			result = data.result

		timestamp = data.timestamp

	}

}


