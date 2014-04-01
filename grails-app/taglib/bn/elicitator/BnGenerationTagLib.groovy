/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bn.elicitator

import bn.elicitator.das2004.ProbabilityEstimation
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode
import bn.elicitator.network.BnProbability

class BnGenerationTagLib {

	static namespace = "bnAdmin"

	VariableService variableService
	AllocateStructureQuestionsService allocateStructureQuestionsService
	AllocateCptQuestionsService allocateCptQuestionsService

	private AllocateQuestionsService getAllocateService() {
		if ( AppProperties.properties.elicitationPhase == AppProperties.ELICIT_2_RELATIONSHIPS ) {
			return allocateStructureQuestionsService
		} else {
			return allocateCptQuestionsService
		}
	}

	/**
	 * Show the relevant functions that help process the survey responses into a valid BN.
	 * The BN can then be downloaded and imported into other software packages that deal
	 * with BNs.
	 */
	def completedBn = {

		int numNodes = BnNode.count()
		int numArcs  = BnArc.count()
		int numCpts  = BnProbability.count()

		if ( numNodes == 0 ) {
			noCompletedBn()
		} else if ( numArcs == 0 ) {
			noCompletedArcs()
		} else if ( numCpts == 0 ) {
			noCompletedCpts()
		} else {
			completeNetwork()
		}

		out << """
<h3>BN statistics</h3>
<p><strong>$numNodes</strong> nodes</p>
<p><strong>$numArcs</strong> arcs</p>
<p><strong>$numCpts</strong> parameters</p>
"""
	}

	private def completeNetwork() {
		out << "<h2>Network complete</h2>"

	}

	private def noCompletedBn() {
		out << """
<h2>No nodes found</h2>
<p>This probably means that we haven't completed the first phase yet (structure elicitation).</p>
"""
	}

	private def noCompletedArcs() {
		out << "<h2>No arcs found</h2>"
	}

	private def noCompletedCpts() {
		out << "<h2>No CPTS found</h2>"

		int numProbabilities = Probability.count()
		int numEstimations   = ProbabilityEstimation.count()

		if ( numEstimations == 0 ) {
			noEstimations()
		} else if ( numProbabilities == 0 ) {
			noProbabilities( numEstimations )
		} else {
			haveProbabilities()
		}
	}

	private def noEstimations() {
		out << """
<h3>No responses yet</h3>
<p>This probably means that the survey is still under way, as we don't have any probability estimations from
the participants yet.</p>
"""

	}

	private def noProbabilities( int numEstimations ) {
		out << """
<h3>Probabilities not yet calculated from responses</h3>
<p>
	There are <strong>$numEstimations</strong> probability estimations avaialble from responses of participants.
	They need to be converted into valid CPT entries in order to parameterise the BN.
</p>
<p>
	${link( [ controller : 'das2004', action : 'calc' ], "Generate CPT entries" )} (this may take some time).
</p>
"""
	}

	private def haveProbabilities() {
		out << """
<h3>Responses from various participants not yet collated</h3>
<p>
	After converting peoples answers in the survey into valid CPT entries, we now need to resolve differences
	between each of the experts.
</p>
<p>
	<input type="button" onclick="document.location='${createLink( [ controller : 'dawidSkeneTest', action : 'index' ] )}'" value="Generate CPT entries" /> (this may take some time).
</p>
"""
	}

}
