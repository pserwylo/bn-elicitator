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

class ElicitProbabilitiesTagLib {

	static namespace = "bnElicit"

	def bnService
	def das2004Service

	/**
	 * @attr variable REQUIRED
	 */
	def scenarios = { attrs ->

		Variable child = attrs.variable
		List<Variable> parents = bnService.getArcsByChild( child )*.parent*.variable

		if ( parents?.size() > 0 ) {
			List<Set<State>> states = parents.collect { it.states }
			states.combinations().each { stateCombinations ->
				out << bnElicit.conditionalScenario( [ variable : child, parentStates : stateCombinations ] )
			}
		} else {
			out << bnElicit.marginalScenario( [ variable : child ] )
		}
	}

	/**
	 * @attr variable REQUIRED
	 * @attr parentStates REQUIRED
	 */
	def conditionalScenario = { attrs ->
		Variable child           = attrs.variable
		List<State> parentStates = attrs.parentStates
		child.states.each { childState ->
			out << """
				<div class='scenario'>
					<p class='consider'>
						Consider that
						<ul>
							${parentStates.collect { "<li>$it.variable is $it.readableLabel</li>" }.join( "\n" )}
						</ul>
					</p>
					${question( [ child : child, state : childState ] )}
				</div>
				"""
		}
	}

	/**
	 * @attr child REQUIRED
	 * @attr state REQUIRED
	 */
	def question = { attrs ->
		Variable child = attrs.child
		State state    = attrs.state
		out << """
			<p class='question'>
				How likely is it that $child is $state.readableLabel?
			</p>
		"""
	}

	/**
	 * @attr variable REQUIRED
	 */
	def marginalScenario = { attrs ->
		Variable variable = attrs.variable
		variable.states.each { state ->
			out << """
				<div class='scenario'>
					${question( [ child : variable, state : state ] )}
				</div>
			"""
		}

	}


	// =======================================================================================
	//          Das 2004 - Compatible parent configurations + Importance weighting
	// =======================================================================================

	/**
	 * @attr variable REQUIRED
	 */
	def elicitDas2004 = { attrs ->

		Variable variable = attrs.variable
		List<Variable> parents = bnService.getArcsByChild( variable )*.parent*.variable

		if ( parents?.size() > 0 ) {
			out << bnElicit.compatibleParentConfigurations( [ child : variable, parents : parents ] )
		} else {
			out << bnElicit.marginalScenario( [ variable : variable ] )
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
				out << bnElicit.compatibleConfigurations( [ parentState : parentState, otherParents : otherParents ] )
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

		String messageIfParentState = message( [ code : 'elicit.probabilities.compatible-configurations.if-state', args : [ parent, parentState ] ] )
		out << """
			<div class='question compatible-configurations hidden'>
				<span class='if-state'>$messageIfParentState</span>
				<ul class='siblings'>
				"""

		// for ( Variable sibling in siblings ) {
		otherParents.eachWithIndex{ Variable otherParent, int i ->
			String messageThenOtherParentState = message( [ code : 'elicit.probabilities.compatible-configurations.then-state', args : [ otherParent ] ] )

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
				String selected = ""
				out << "<input type='radio' name='$name' id='$id' value='$otherParentState.id' /><label for='$id'>$otherParentState</label>"
			}
			out << "</span></li>"
		}
		out << "</ul></div>"

	}

}