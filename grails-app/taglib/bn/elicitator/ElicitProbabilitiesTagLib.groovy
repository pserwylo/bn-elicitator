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

	def delphiService

	/**
	 * @attr variable REQUIRED
	 */
	def scenarios = { attrs ->
		Variable variable      = attrs.variable
		List<Variable> parents = Relationship.findAllByDelphiPhaseAndExistsAndIsExistsInitializedAndChild( delphiService.phase, true, true, variable )*.parent.unique()

		if ( parents?.size() > 0 ) {
			List<Set<State>> states = parents.collect { it.states }
			states.combinations().each { stateCombinations ->
				out << bnElicit.conditionalScenario( [ variable : variable, parentStates : stateCombinations ] )
			}
		} else {
			out << bnElicit.marginalScenario( [ variable : variable ] )
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
					<p class='question'>
						How likely is it that $child is $childState.readableLabel?
					</p>
				</div>
				"""
		}
	}

	/**
	 * @attr variable REQUIRED
	 */
	def marginalScenario = { attrs ->
		out << """
			<div class='scenario'>
				<span class='question'>How likely is it that $child is Y?</span>
			</div>
"""
	}
}