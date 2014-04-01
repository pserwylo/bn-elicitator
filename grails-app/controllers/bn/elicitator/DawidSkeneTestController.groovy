package bn.elicitator
import bn.elicitator.network.BnProbability
import bn.elicitator.troia.CptJob

class DawidSkeneTestController {

	def index() {

		CptJob job = new CptJob( "http://localhost:8080/troia" )
		job.run()

		// Currently, I'm doing this in a new request because I can't find out why the MySQL connection
		// is falling over after waiting ages for the TROIA process to finish...
		redirect( action : 'saveCptsToBn', params : [ jobId : job.id ] )

	}

	def saveCptsToBn() {

		CptJob job = new CptJob( "http://localhost:8080/troia", (String)params[ "jobId" ] )
		job.predictions().each {
			BnProbability.fromProbability( it.probability ).save( failOnError : true )
		}
		redirect( action : 'adminDash' )

	}

}
