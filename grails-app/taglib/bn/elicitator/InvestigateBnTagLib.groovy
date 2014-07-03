/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2014 Peter Serwylo (peter.serwylo@monash.edu)
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

import bn.elicitator.auth.User

import java.text.DecimalFormat

class InvestigateBnTagLib {

	static namespace = "bnInvestigate"

	UserService userService

	/**
	 * @attr parent REQUIRED
	 * @attr child  REQUIRED
	 * @attr user
	 */
	def arcSummary = { attrs ->

		Variable parent = attrs.parent
		Variable child  = attrs.child
		User user       = attrs.containsKey( 'user' ) ? attrs.remove( 'user' ) : null

		int delphiPhase = 1

		while (delphiPhase <= 3) {
			def relationships = Relationship.findAllByParentAndChildAndDelphiPhase( parent, child, delphiPhase )
			out << bnInvestigate.relationshipsForDelphiPhase( [ delphiPhase : delphiPhase, relationships : relationships ] )
			delphiPhase ++
		}
	}

	/**
	 * @attr delphiPhase REQUIRED
	 * @attr relationships REQUIRED
	 */
	def relationshipsForDelphiPhase = { attrs ->
		int delphiPhase = attrs.delphiPhase
		List<Relationship> relationships = attrs.relationships.sort { it1, it2 -> it1.createdBy.id <=> it2.createdBy.id }

		out << "<h2>During Delphi phase ${delphiPhase}</h2>"
		out << "<ul class='relationshipFromUser'>"

		relationships.each { Relationship relationship ->

			String comment
			String existsClass = relationship.exists ? "exists" : "doesntExist"
			String commentClass = ""
			String completedClass = relationship.isExistsInitialized ? "answered" : "didntAnswer"

			if ( !relationship.isExistsInitialized ) {
				comment = "Didn't answer question"
			}
			else if ( relationship.comment?.comment?.size() > 0 ) {
				comment = relationship.comment.comment
			} else {
				comment = "No comment provided"
				commentClass = "noComment"
			}

			out << """
				<li class='user $existsClass $completedClass user-$relationship.createdBy.id'>
					<input type='hidden' name='id' value='$relationship.createdBy.id' />
					${workerQualityBar( [ user : relationship.createdBy ] ) }
					<span class='name'>Participant $relationship.createdBy.id</span>
					<span class='comment $commentClass'>$comment</span>
				</il>
			"""
		}

		out << "</ul>"
	}

	/**
	 * @attr user REQUIRED
	 */
	def workerQualityBar = { attrs ->

		User user = attrs.user

		float min   = userService.minEstimatedQuality
		float max   = userService.maxEstimatedQuality
		float value = user.estimatedQuality
		float range = max - min
		float percent = ( value - min ) / range * 100

		String valueFormatted = new DecimalFormat( "0.000" ).format( value )

		out << """
			<span class='quality-bar'>
				<span class='value'>$valueFormatted</span>
				<span class='outer-bar'>
					<span class='inner-bar' style='width: $percent%'></span>
				</span>
			</span>
"""

	}

	/**
	 * @attr id   REQUIRED
	 * @attr name REQUIRED
	 * @attr variables
	 * @attr selectedId
	 * @attr assignedToUser
	 */
	def childVariableList = { attrs ->
		if ( attrs.containsKey( 'assignedToUser' ) ) {
			attrs.variables = Relationship.findAllByCreatedByAndIsExistsInitializedAndDelphiPhaseAndChildInList( attrs.assignedToUser, true, 1, attrs.variables )*.child.unique { it.id }
		}
		out << variableList( attrs )
	}

	/**
	 * @attr id   REQUIRED
	 * @attr name REQUIRED
	 * @attr variables
	 * @attr selectedId
	 * @attr assignedToUser
	 */
	def parentVariableList = { attrs ->
		if ( attrs.containsKey( 'assignedToUser' ) ) {
			attrs.variables = Relationship.findAllByCreatedByAndIsExistsInitializedAndDelphiPhase( attrs.assignedToUser, true, 1 )*.parent.unique { it.id }
		}
		out << variableList( attrs )
	}

	/**
	 * @attr id   REQUIRED
	 * @attr name REQUIRED
	 * @attr variables
	 * @attr selectedId
	 */
	def variableList = { attrs ->

		List<Variable> variables = attrs.containsKey( 'variables'      ) ? attrs.variables      : Variable.list().sort { it1, it2 -> it1.readableLabel <=> it2.readableLabel }
		def selectedId           = attrs.containsKey( 'selectedId'     ) ? attrs.selectedId     : null

		out << g.select(
			id          : attrs.id,
			name        : attrs.name,
			optionKey   : 'id',
			optionValue : { it -> "$it.readableLabel ($it.label)" },
			from        : variables,
			value       : selectedId,
			noSelection : selectedId ? null : [ 0 : '-- Select variable --' ]
		)

	}

}
