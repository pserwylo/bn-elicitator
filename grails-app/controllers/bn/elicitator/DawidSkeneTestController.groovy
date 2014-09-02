package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode
import bn.elicitator.network.BnProbability
import bn.elicitator.troia.CptJob
import bn.elicitator.troia.StructureJob
import org.apache.commons.collections.CollectionUtils

class DawidSkeneTestController {

	private final String TROIA_URL = "http://uni.peter.serwylo.com:8080/troia"

	def structure() {

		StructureJob job = new StructureJob( TROIA_URL )

        if (params.containsKey("arcPrior")) {
            job.arcPrior = params.remove("arcPrior") as Double
        }

		job.run()

		def predictions = job.predictions();

		BnArc.list().each {
			it.delete( flush : true )
		}

        int count = 0;
		predictions.each {

			if ( it.relationship.exists ) {

                count ++;

				BnNode parent = BnNode.findByVariable( it.relationship.parent )
				BnNode child  = BnNode.findByVariable( it.relationship.child  )

				if ( !parent ) {
					parent = new BnNode( variable : it.relationship.parent )
					parent.save( flush : true, failOnError : true )
				}

				if ( !child ) {
					child = new BnNode( variable : it.relationship.child )
					child.save( flush : true, failOnError : true )
				}

				BnArc arc = BnArc.findByParentAndChild( parent, child )
				if ( !arc ) {
					arc = new BnArc( parent : parent, child : child )
					arc.save( flush : true, failOnError: true )
				}

			}
		}

        render "<h1>Dawid & Skene completed</h1>"
        if (job.arcPrior >= 0) {
            render "<p><strong>Arc prior:</strong> $job.arcPrior)</p>"
        }
        render "<p><strong>Arcs:</strong> $count / ${predictions.size()}</p>"

        render "<a href='${g.createLink( controller : 'output', action : 'csv', params : [ finalNetwork : true ] )}'>CSV output</a>"


        /*
		def workerQuality = job.estimatedWorkerQuality()
		workerQuality.each {
			User user = User.get( it.key )
			user.estimatedQuality = it.value
			user.save( failOnError : true )
		}

		render "<h1>Worker quality</h1>"

        if (job.arcPrior >= 0) {
            render "<h2>Arc prior: $job.arcPrior</h2>"
        }

		render "<p>(for Delphi phase 1 only)</p>"
		render "<ul>"
		workerQuality.sort { it1, it2 -> it1.value <=> it2.value }.each {
			render "<li>$it.key: $it.value</li>"
		}
		render "</ul>"
        */

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
