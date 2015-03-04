package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode
import bn.elicitator.network.BnProbability
import bn.elicitator.troia.CptJob
import bn.elicitator.troia.DawidSkeneRelationship
import bn.elicitator.troia.DegradedStructureJob
import bn.elicitator.troia.StructureJob
import org.apache.commons.collections.CollectionUtils

class DawidSkeneTestController {

	private final String TROIA_URL = "http://uni.peter.serwylo.com:8080/troia"

	def degradeStructure() {
		def prior = 0.1
		for ( def i = 1; i <= 10; i++ ) {
			println "# Iteration $i (prior $prior)#"
			performDegradeStructure( new File( "/tmp/degraded.ds.structure.$i-${prior}.tsv" ), prior )
			println "Sleeping for 5 seconds"
			sleep( 5000 )
		}
	}

	private void performDegradeStructure( File file, def prior ) {

		file.withWriter { Writer out ->

			out.println([
				"Removal Percent",
				"Parent",
				"Child",
				"Uncertainty"
			].join("\t"))

		}

		for ( def i = 0; i <= 90; i += 10 ) {

			println "* Removing $i% of responses *"

			DegradedStructureJob job = new DegradedStructureJob( TROIA_URL)
			job.arcPrior       = prior
			job.removalPercent = i
			job.run()

			file.withWriterAppend { Writer out ->

				def predictions = job.predictions()
				def existingPredictions = predictions.findAll { it.relationship.exists }
				existingPredictions.each { DawidSkeneRelationship rel ->
					out.println([
							i,
							rel.relationship.parent.label,
							rel.relationship.child.label,
							rel.uncertainty
					].join("\t"))
				}
			}
		}

	}

	def analysePriors() {

		def file = new File( "/tmp/prior.ds.structure.tsv" )

		file.withWriter { Writer out ->

			out.println([
				"Prior",
				"Parent",
				"Child",
			].join("\t"))

		}

		for ( def i = 0.05; i <= 0.95; i += 0.05 ) {

			println "* Calculating with prior of $i *"

			StructureJob job = new StructureJob( TROIA_URL)
			job.arcPrior     = i
			job.run()

			file.withWriterAppend { Writer out ->

				def predictions = job.predictions()
				def existingPredictions = predictions.findAll { it.relationship.exists }
				existingPredictions.each { DawidSkeneRelationship rel ->
					out.println([
						i,
						rel.relationship.parent.label,
						rel.relationship.child.label,
					].join("\t"))
				}
			}
		}

	}

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
