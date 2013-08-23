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

import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.das2004.CompatibleParentConfiguration

class Das2004TagLib {

	static namespace = "das2004"

	def bnService
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
	//                           Parent Weights
	// ===================================================================

	/**
	 * @attr variable REQUIRED
	 */
	def importance = { attrs ->

		Variable variable = attrs.variable
		List<Variable> parents = bnService.getArcsByChild( variable )*.parent*.variable

		if ( parents?.size() > 0 ) {
			out << das2004.weightElicitation( [ child : variable, parents : parents ] )
		} else {
			throw new Exception( "Marginal probability elicitation not supported yet." )
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
						${message( [ code : 'elicit.probabilities.importance.which', args : [ child.readableLabel ] ])}
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
					${message( [ code : 'elicit.probabilities.importance.how-much' ] )}
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

		if ( parents?.size() > 0 ) {
			out << das2004.distributionElicitation( [ child : variable, parents : parents ] )
		} else {
			throw new Exception( "Marginal probability elicitation not supported yet." )
		}
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
			parentConfigurations.each { CompatibleParentConfiguration parentConfiguration ->
				out <<  das2004.distributionOverParents( [ childState : childState, parentConfiguration : parentConfiguration ] )
			}
		}
	}

	/**
	 * @attr childState REQUIRED
	 * @attr parentConfiguration REQUIRED
	 */
	def distributionOverParents = { attrs ->

		if ( !attrs.containsKey( 'childState' ) ) {
			throwTagError( "Tag [distributionOverParents] missing required [childState] attribute." )
		}

		if ( !attrs.containsKey( 'parentConfiguration' ) ) {
			throwTagError( "Tag [distributionOverParents] missing required [parentConfiguration] attribute." )
		}

		State childState                           = attrs.childState
		CompatibleParentConfiguration parentConfig = attrs.parentConfiguration

		// TODO: In the future, may want to let people go back and edit responses, but for now - just ignore already answered questions...
		def existingEstimation = das2004Service.getProbabilityEstimation( childState, parentConfig )
		if ( existingEstimation ) {
			return
		}

		out << "<div class='question likelihood hidden'>"

		parentConfig.allParentStates().eachWithIndex { State parentState, int i ->
			String ifAnd = i > 0 ? " and " : "If "
			String messageIfParentState = message( [ code : 'elicit.probabilities.likelihood.if-state', args : [ parentState.variable.readableLabel, parentState.readableLabel ] ] )
			out << "<span class='if-state $ifAnd'>$ifAnd $messageIfParentState</span>"
		}

		String messageThenProbability = message( [ code : 'elicit.probabilities.likelihood.then-probability' ] )
		out << """
			<span class='then-probability'>$messageThenProbability</span>
				<span class='probabilities'>
				"""

		PROBABILITIES.each {
			String probabilityLabel   = it.key
			Double probabilityPercent = it.value
			String label              = "$probabilityLabel <span class='probability-explaination'>(About ${(int)(probabilityPercent)}/100 times)</span>"
			String name               = "parentConfigurationId=$parentConfig.id,childId=$childState.variable.id,childStateId=$childState.id"
			String id                 = "$name,probability=$probabilityPercent"
			out << "<input type='radio' name='$name' id='$id' value='$probabilityPercent' /><label for='$id'>$label</label>"
		}

		String messageThenState = message( [ code : 'elicit.probabilities.likelihood.then-state', args : [ childState.variable.readableLabel, childState.readableLabel ] ] )
		out << """
				</span>
				<span class='then-state'>$messageThenState</span>
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

		if ( parents?.size() > 0 ) {
			out << das2004.compatibleParentConfigurations( [ child : variable, parents : parents ] )
		} else {
			throw new Exception( "Marginal probability elicitation not supported yet." )
		}
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
		def compatibleConfig        = das2004Service.getParentConfig( parentState )
		if ( compatibleConfig ) {
			return
		}

		String messageIfParentState = message( [ code : 'elicit.probabilities.expected.if-state', args : [ parent, parentState ] ] )
		out << """
			<div class='question compatible-configurations hidden'>
				<span class='if-state'>$messageIfParentState</span>
				<ul class='siblings'>
				"""

		otherParents.eachWithIndex{ Variable otherParent, int i ->
			String messageThenOtherParentState = message( [ code : 'elicit.probabilities.expected.then-state', args : [ otherParent ] ] )

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
				out << "<input type='radio' name='$name' id='$id' value='$otherParentState.id' /><label for='$id'>$otherParentState</label>"
			}
			out << "</span></li>"
		}
		out << "</ul></div>"

	}

}