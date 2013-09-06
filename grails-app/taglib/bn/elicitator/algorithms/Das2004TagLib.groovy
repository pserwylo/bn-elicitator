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

package bn.elicitator.algorithms

import bn.elicitator.CptAllocation
import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.das2004.CompatibleParentConfiguration
import bn.elicitator.network.BnArc

class Das2004TagLib {

	static namespace = "das2004"

	def bnService
	def userService
	def das2004Service

	final Map<String,Double> PROBABILITIES = [
		"Impossible"  : 1,
		"Improbable"  : 15,
		"Uncertain"   : 25,
		"Fifty-Fifty" : 50,
		"Expected"    : 75,
		"Probable"    : 85,
		"Certain"     : 99,
	]

	final Map<Double, String> WEIGHTS = [
		(2) : "",
		(3) : "Only a little",
		(4) : "",
		(5) : "Strongly",
		(6) : "",
		(7) : "Demonstratably",
		(8) : "",
		(9) : "Absolutely",
	]



	// ===================================================================
	//                           Misc stuff
	// ===================================================================

	def listSummaryProbabilities = { attrs ->

		CptAllocation allocation = CptAllocation.findByUser( userService.current )
		List<BnArc> relevantArcs = bnService.getArcsByChildren( allocation.variables.toList() )
		List<Long> completedIds  = das2004Service.completed*.id

		out << "<ul id='all-children-list' class='variable-list'>\n"
		allocation.variables.eachWithIndex { Variable var, int i ->
			int parentCount = relevantArcs.count { it.child.variable.id == var.id }
			String action = ( parentCount > 1 ) ? "expected" : "likelihood"
			String clazz  = completedIds.contains( var.id ) ? "doesnt-need-review" : "needs-review"
			String id     = i == 0 ? "id='first-variable'" : ""
			out << """
				<li class='variable-item $clazz'>
					<a $id href='${createLink( controller: 'das2004', action: action, params: [ id : var.id ] )}'>${bn.variable( [ var: var, includeDescription: false ] )}</a>
				</li>
				"""
		}
		out << "</ul>\n"

	}


	// ===================================================================
	//                           Parent Weights
	// ===================================================================

	/**
	 * @attr variable REQUIRED
	 */
	def importance = { attrs ->

		Variable variable = attrs.variable
		List<Variable> parents = bnService.getArcsByChild( variable )*.parent*.variable

		if ( parents?.size() > 1 ) {
			out << das2004.weightElicitation( [ child : variable, parents : parents ] )
		} else {
			clientRedirect( 'index' )
		}
	}

	/**
	 * @attr child REQUIRED
	 * @attr parents REQUIRED
	 */
	def weightElicitation = { attrs ->

		if ( !attrs.containsKey( 'child' ) ) {
			throwTagError( "Tag [weightElicitation] missing required [child] attribute." )
		}

		if ( !attrs.containsKey( 'parents' ) ) {
			throwTagError( "Tag [weightElicitation] missing required [parents] attribute." )
		}

		Variable child         = attrs.child
		List<Variable> parents = attrs.parents

		for ( int y = 0; y < parents.size() - 1; y ++ ) {
			for ( int x = y + 1; x < parents.size(); x ++ ) {

				Variable parentOne = parents.get( x )
				Variable parentTwo = parents.get( y )

				out << das2004.pairwiseComparison( [ child : child, parentOne : parentOne, parentTwo : parentTwo ])
			}
		}
	}

	/**
	 * @attr child     REQUIRED
	 * @attr parentOne REQUIRED
	 * @attr parentTwo REQUIRED
	 */
	def pairwiseComparison = { attrs ->

		if ( !attrs.containsKey( 'child' ) ) {
			throwTagError( "Tag [pairwiseComparison] missing required [child] attribute." )
		}

		if ( !attrs.containsKey( 'parentOne' ) ) {
			throwTagError( "Tag [pairwiseComparison] missing required [parentOne] attribute." )
		}

		if ( !attrs.containsKey( 'parentTwo' ) ) {
			throwTagError( "Tag [pairwiseComparison] missing required [parentTwo] attribute." )
		}

		Variable child     = attrs.child
		Variable parentOne = attrs.parentOne
		Variable parentTwo = attrs.parentTwo

		// TODO: In the future, may want to let people go back and edit responses, but for now - just ignore already answered questions...
		def existingComparison = das2004Service.getPairwiseComparison( child, parentOne, parentTwo )
		if ( existingComparison ) {
			return
		}

		String name   = "childId=$child.id,parentOneId=$parentOne.id,parentTwoId=$parentTwo.id"
		String idOne  = "$name,this=one"
		String idSame = "$name,this=same"
		String idTwo  = "$name,this=two"

		out << """
			<div class='question comparison hidden'>
				<div class='which'>
					<div class='more-impotant'>
						${bn.htmlMessage( [ code : 'elicit.probabilities.importance.which', args : [ child.readableLabel ] ])}
					</div>
					<div class='most-important'>
						<div class='large'>
							<input type='radio' name='$name' id='$idOne' value='$parentOne.id' />
							<label for='$idOne'>$parentOne</label>
						</div>
						<div class='small'>
							<input type='radio' name='$name' id='$idSame' value='0' />
							<label for='$idSame'>About the same</label>
						</div>
						<div class='large'>
							<input type='radio' name='$name' id='$idTwo' value='$parentTwo.id' />
							<label for='$idTwo'>$parentTwo</label>
						</div>
					</div>
				</div>
				<div class='how-much hidden'>
					${bn.htmlMessage( [ code : 'elicit.probabilities.importance.how-much' ] )}
					<div class='weights'>
					"""

		WEIGHTS.each {
			int weight   = it.key

			String label = "$weight times"
			if ( it.value ) {
				label += " <span class='label'>($it.value)</span>"
			}


			String weightName = "$name,weight"
			String weightId   = "$weightName,weight=$weight"

			out << """
				<input type='radio' name='$weightName' value='$weight' id='$weightId' />
				<label for='$weightId'>$label</label>
			"""

		}

		out << """
					</div>
				</div>
			</div>
			"""

	}

	// ===================================================================
	//               Probability distribution elicitation
	// ===================================================================

	/**
	 * @attr variable REQUIRED
	 */
	def likelihood = { attrs ->

		Variable variable = attrs.variable
		List<Variable> parents = bnService.getArcsByChild( variable )*.parent*.variable

		if ( parents?.size() >= 1 ) {
			out << das2004.distributionElicitation( [ child : variable, parents : parents ] )
		} else {
			if ( parents.size() == 1 ) {
				das2004Service.populateSingleParentConfigurations( parents[ 0 ] )
			}

			variable.states.each { State state ->
				out << das2004.distributionOverParents( [ childState : state ] )
			}
		}
	}

	/**
	 * @attr variable REQUIRED
	 */
	def afterLikelihood = { attrs ->

		if ( !attrs.containsKey( 'variable' ) ) {
			throwTagError( "Tag [afterLikelihood] missing required [variable] attribute." )
		}

		Variable variable = attrs.variable

		int count = bnService.getArcsByChild( variable ).size()
		String action = ( count > 1 ) ? 'importance' : 'index'
		out << createLink( [ controller : 'das2004', action : action, params : [ id : variable.id ] ] )
	}

	/**
	 * @attr child REQUIRED
	 * @attr parents REQUIRED
	 */
	def distributionElicitation = { attrs ->

		if ( !attrs.containsKey( 'child' ) ) {
			throwTagError( "Tag [distributionElicitation] missing required [child] attribute." )
		}

		if ( !attrs.containsKey( 'parents' ) ) {
			throwTagError( "Tag [distributionElicitation] missing required [parents] attribute." )
		}

		Variable child         = attrs.child
		List<Variable> parents = attrs.parents

		def parentConfigurations = das2004Service.getParentConfigurationsForChild( child, parents )
		child.states.each { State childState ->
			parentConfigurations.eachWithIndex { CompatibleParentConfiguration parentConfiguration, int i ->

				// Compare to all previous items and decide if we really need to ask this question...
				boolean ignore = false
				for ( int j = 0; j < i; j ++ ) {
					if ( parentConfigurations[ j ].equivalentTo( parentConfiguration ) ) {
						ignore = true
						break
					}
				}

				if ( !ignore ) {
					out <<  das2004.distributionOverParents( [ childState : childState, parentConfiguration : parentConfiguration ] )
				}
			}
		}
	}

	/**
	 * @attr childState REQUIRED
	 * @attr parentConfiguration
	 */
	def distributionOverParents = { attrs ->

		if ( !attrs.containsKey( 'childState' ) ) {
			throwTagError( "Tag [distributionOverParents] missing required [childState] attribute." )
		}

		State childState                           = attrs.childState
		CompatibleParentConfiguration parentConfig = attrs.containsKey( 'parentConfiguration' ) ? attrs.parentConfiguration : null

		// TODO: In the future, may want to let people go back and edit responses, but for now - just ignore already answered questions...
		def existingEstimation = das2004Service.getProbabilityEstimation( childState, parentConfig )
		if ( existingEstimation ) {
			return
		}

		out << "<div class='question likelihood hidden'>"

		String messageThenProbability
		if ( parentConfig ) {
			parentConfig.allParentStates().eachWithIndex { State parentState, int i ->
				String ifAnd = i > 0 ? " and " : "If "
				String messageIfParentState = bn.htmlMessage( [ code : 'elicit.probabilities.likelihood.if-state', args : [ parentState.description ] ] )
				out << "<span class='if-state $ifAnd'>$ifAnd $messageIfParentState</span>"
			}
			messageThenProbability = bn.htmlMessage( [ code : 'elicit.probabilities.likelihood.then-probability' ] )
		} else {
			messageThenProbability = bn.htmlMessage( [ code : 'elicit.probabilities.likelihood.marginal-probability' ] )
		}

		String messageThenState = bn.htmlMessage( [ code : 'elicit.probabilities.likelihood.then-state', args : [ childState.description ] ] )
		out << """
			<span class='then-probability'>$messageThenProbability</span>
				<span class='then-state'>$messageThenState</span>
				<span class='probabilities'>
				"""

		PROBABILITIES.each {
			String probabilityLabel   = it.key
			Double probabilityPercent = it.value
			int parentConfigurationId = parentConfig?.id ?: 0
			String label              = "$probabilityLabel <span class='probability-explaination'>(About ${(int)(probabilityPercent)}/100 times)</span>"
			String name               = "parentConfigurationId=$parentConfigurationId,childId=$childState.variable.id,childStateId=$childState.id"
			String id                 = "$name,probability=$probabilityPercent"
			out << "<input type='radio' name='$name' id='$id' value='$probabilityPercent' /><label for='$id'>$label</label>"
		}

		out << """
				</span>
			</div>"""
	}


	// ===================================================================
	//                 Compatible parent configurations
	// ===================================================================

	/**
	 * @attr variable REQUIRED
	 */
	def expected = { attrs ->

		Variable variable = attrs.variable
		List<Variable> parents = bnService.getArcsByChild( variable )*.parent*.variable

		if ( parents?.size() > 1 ) {
			out << das2004.compatibleParentConfigurations( [ child : variable, parents : parents ] )
		} else {
			clientRedirect( 'likelihood' )
		}
	}

	private void clientRedirect( String dasAction ) {
		out << """
			<script type='text/javascript'>
				document.location = '${createLink( [ controller : 'das2004', action : dasAction ] )}';"
			</script>
			"""
	}

	/**
	 * @attr child REQUIRED
	 * @attr parents REQUIRED
	 */
	def compatibleParentConfigurations = { attrs ->

		Variable child         = attrs.child
		List<Variable> parents = attrs.parents

		parents.each { parent ->
			List<Variable> otherParents = parents.findAll { it != parent }
			parent.states.each { parentState ->
				out << das2004.compatibleConfigurations( [ parentState : parentState, otherParents : otherParents ] )
			}
		}
	}

	/**
	 * @attr parentState  REQUIRED
	 * @attr otherParents REQUIRED
	 */
	def compatibleConfigurations = { attrs ->

		State parentState           = attrs.parentState
		Variable parent             = parentState.variable
		List<Variable> otherParents = attrs.otherParents

		// TODO: In the future, may want to let people go back and edit responses, but for now - just ignore already answered questions...
		def compatibleConfig = das2004Service.getParentConfig( parentState )
		if ( compatibleConfig ) {
			return
		}

		String messageIfParentState = bn.htmlMessage( [ code : 'elicit.probabilities.expected.if-state', args : [ parentState.description ] ] )
		out << """
			<div class='question compatible-configurations hidden'>
				<span class='if-state'>$messageIfParentState</span>
				<ul class='siblings'>
				"""

		otherParents.eachWithIndex{ Variable otherParent, int i ->
			String messageThenOtherParentState = bn.htmlMessage( [ code : 'elicit.probabilities.expected.then-state', args : [ otherParent ] ] )

			if ( i > 0 ) {
				messageThenOtherParentState = "and $messageThenOtherParentState"
			}

			out << """
				<li>
					<span class='then-state'>$messageThenOtherParentState</span>
					<span class='sibling-states'>
					"""

			for ( State otherParentState in otherParent.states ) {
				String name = "parentId=$parent.id,parentStateId=$parentState.id,otherParentId=$otherParent.id"
				String id   = "$name,otherParentStateId=$otherParentState.id"
				out << "<input type='radio' name='$name' id='$id' value='$otherParentState.id' /><label for='$id'>$otherParentState.description</label>"
			}
			out << "</span></li>"
		}
		out << "</ul></div>"

	}

}