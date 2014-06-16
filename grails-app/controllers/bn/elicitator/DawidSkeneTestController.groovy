package bn.elicitator

import bn.elicitator.network.BnNode
import bn.elicitator.network.BnProbability
import bn.elicitator.troia.CptJob
import bn.elicitator.troia.StructureJob
import org.apache.commons.collections.CollectionUtils

class DawidSkeneTestController {

	private final String TROIA_URL = "http://uni.peter.serwylo.com:8080/troia"

	def structure() {
		StructureJob job = new StructureJob( TROIA_URL )
		job.run()

		render "<h1>Worker quality</h1>"
		render "<p>(for Delphi phase 1 only)</p>"
		render "<ul>"
		new TreeMap<Long, Double>( job.estimatedWorkerQuality() ).sort { it1, it2 -> it1.value <=> it2.value }.each {
			render "<li>$it.key: $it.value</li>"
		}
		render "</ul>"

	}

	def cpt() {

		CptJob job = new CptJob( TROIA_URL )
		job.run()

		// Currently, I'm doing this in a new request because I can't find out why the MySQL connection
		// is falling over after waiting ages for the TROIA process to finish...
		redirect( action : 'saveCptsToBn', params : [ jobId : job.id ] )

	}

	def saveCptsToBn() {

		CptJob job = new CptJob( "http://uni.peter.serwylo.com:8080/troia", (String)params[ "jobId" ] )
		job.predictions().each {
			BnProbability.fromProbability( it.probability ).save( failOnError : true )
		}
		redirect( controller : 'adminDash' )

	}

	def normaliseProbabilities() {

		def nodes = BnNode.list()
		def probs = BnProbability.list()

		nodes.each { BnNode node ->

			def relevantProbs = probs.findAll { BnProbability prob -> prob.childState.variable.id == node.variable.id }
			def parents       = relevantProbs[ 0 ].parentStates*.variable

			if ( parents.size() == 0 ) {

				normalise( relevantProbs )

			} else {

				def parentConfigs = parents*.states.combinations()

				parentConfigs.each { List<State> parentConfig ->
					def probsToNormalise = relevantProbs.findAll { BnProbability prob ->
						CollectionUtils.isEqualCollection(prob.parentStates, parentConfig)
					}
					normalise(probsToNormalise)
				}

			}
		}

		render "<h1>Probabilities normalised...</h1>"

	}

	private void normalise( List<BnProbability> probs ) {

		double sum = probs*.probability.sum() as Double;
		probs.each { BnProbability prob ->
			try {
				print "$prob = $prob.probability / $sum"
				prob.probability /= sum
				prob.save( failOnError : true )
			} catch (ArithmeticException ignored) {
				print "Couldn't normalise probabilities for P($prob.childState.variable.label = $prob.childState.label...) as the total summed to zero."
			}
		}

	}

}
