package bn.elicitator

import bn.elicitator.troia.ContinuousJob
import bn.elicitator.troia.CptJob
import bn.elicitator.troia.Job
import bn.elicitator.troia.TroiaClient

class DawidSkeneTestController {

	def index() {

		CptJob job = new CptJob( "http://localhost:8080/troia" )

		job.run()

		def response = job.predictions()

		render response

	}
}
