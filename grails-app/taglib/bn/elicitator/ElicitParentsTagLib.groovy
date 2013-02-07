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

class ElicitParentsTagLib {

	static namespace = "bnElicit"

	VariableService variableService
	DelphiService delphiService

	/**
	 * @attr var REQUIRED
	 * @attr includeLabel
	 */
	def variableSynonyms = { attrs ->

		Variable var = attrs.var
		boolean includeLabel = attrs.containsKey( 'includeLabel' ) ? attrs.includeLabel : false

		Set<String> synonyms = var.synonyms ?: []

		String output = ""

		if ( !includeLabel )
		{
			// The synonym list includes the readable label, which is not interesting to us here, because we have
			// already rendered it to the screen.
			if ( synonyms.contains( var.readableLabel ) )
			{
				synonyms.remove( var.readableLabel )
			}
		}

		if ( synonyms.size() > 0 )
		{
			output += "<ul class'synonyms'><li>${synonyms.join( '</li><li>' )}</li></ul>"
		}

		out << output

	}

	/**
	 * @attr relationships REQUIRED
	 */
	def reasonsList = { attrs ->

		List<Relationship> relationships = attrs.relationships


		out << """
			<div class='reasons'>
				<span class='header'>
					${message( code: "elicit.parents.reason.header" )} ${img( [ dir: "images/icons", file: "comments.png" ] )}
				</span>
				"""

		boolean hasReasons = false

		out <<  """
			<div  class='list-wrapper'>
				<ul class='reasons-list'>
				"""

		relationships.each {

			if ( it != null ) {

				Comment comment = it.comment
				if ( comment?.comment?.trim()?.size() > 0 )
				{
					hasReasons = true;

					boolean isMine = comment.createdBy == ShiroUser.current
					String author = isMine ? "Myself" : "Other participant"
					String className = "phase-" + it.delphiPhase
					className += isMine ? " me" : " other"
					className += it.exists ? " exists" : " doesnt-exist"

					out << """
						<li class='${className}'>
							"${comment.comment}"
							<div class='author'> - ${author}</div>
						</li>
						"""
				}
			}
		}

		out << """
				</ul>
			</div>"""

		if ( !hasReasons )
		{
			out << "<div class='no-reasons'>No reasons given.</div>\n"
		}

		out << "</div>"
	}

	/**
	 * Iterates over each potentialParents and invokes the potentialParent taglib.
	 * If we are in subsequent phases, we don't show variables which received no love from anybody in the previous phase.
	 * @attr child REQUIRED
	 * @attr potentialParents REQUIRED
	 */
	def potentialParentsList = { attrs ->

		List<Variable> potentialParents = attrs.potentialParents
		Variable child = attrs.child

		out << "<ul id='list-$key' class='potential-parents-list variable-list'>"

		for ( Variable parent in potentialParents )
		{
			if ( parent != child )
			{
				out << bnElicit.potentialParent( child: child, parent: parent )
			}
		}

		if ( delphiService.phase == 1 )
		{
			out << """
				<li id="add-variable-item" class=" variable-item new-var">
					${bnElicit.newVariableForm( var: child )}
				</li>
				"""
		}

		out << "</ul>"
	}

	private void dumpPotentialParentLabel( Variable parent, Boolean selected )
	{

		out << """
			<label for='input-${parent.label}'>
				<input
					id='input-${parent.label}'
					type='checkbox'
					name='parents'
					value='${parent.label}'
					class='potential-parent'
					${selected ? "checked='checked'" : ''} />

				${bn.variable( [ var: parent, includeDescription: false ] )}
				${bn.variableDescription( [ var: parent ] )}
				${bnElicit.variableSynonyms( [ var: parent ] )}

			</label>

			<a href='javascript:void' class='unsaved-icon'>
				<img src='${resource( dir: 'images/skin/', file: 'exclamation.png' )}' />
			</a>
			"""

	}

	private void dumpPotentialParentSummary( Variable parent )
	{
		// TODO: Change this to something like "Review" for subsequent rounds...
		out << """
			<div id='${parent.label}-summary' class='var-summary'>
				<span class="toggle-details">
					<button type="button" class="show-var-details " onclick="showVarDetails( '${parent.label}' )">
						${message( code: "general.show" )}
						${message( code: "elicit.parents.info" )}
					</button>
				</span>
			</div>
			"""
	}

	/**
	 * @attr child        REQUIRED
	 * @attr parent       REQUIRED
	 * @attr relationship REQUIRED
	 * @attr isSelected   REQUIRED
	 */
	def potentialParentDialog = { attrs ->

		Variable child            = attrs.child
		Variable parent           = attrs.parent
		Relationship relationship = attrs.relationship
		Boolean isSelected        = attrs.isSelected

		out << """
			<div id='${parent.label}-details' class='var-details floating-dialog'>
				<div class='header-wrapper'>
					${bn.saveButtons( [ atTop: true ] )}
				</div>
				<table width="100%" class="form">
					<tr>
						<th>
						</th>
						<td>
							<label>
								<input
									id='input-${parent.label}-form'
									type='checkbox'
									${isSelected ? "checked='checked'" : ''}
									name='parents'
									value='${parent.label}'
									/>

								I think it does
							</label>
						</td>
					</tr>
					<tr>
						<th>
							Why do you think this?
						</th>
						<td>
							<div class='my-comment'>
								<textarea name='comment'>${relationship?.delphiPhase == delphiService.phase && relationship?.comment?.comment?.length() > 0 ? relationship.comment.comment : ''}</textarea>
							</div>
						</td>
					</tr>
				</table>
				"""

		List<Relationship> relationshipsToShowCommentsFor = this.delphiService.hasPreviousPhase ? this.delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child ) : [ relationship ]

		out << """
			${bnElicit.reasonsList( [ relationships: relationshipsToShowCommentsFor ] )}
			${bn.saveButtons( [ atTop: false ] )}
		</div>
		"""
	}

	/**
	 * Displays a list element which portrays a variable which is primed to be selected as a parent of child.
	 * If the user has already viewed and saved a relationship for this pair of variables, we will retrieve that.
	 * @attrs child REQUIRED
	 * @attrs parent REQUIRED
	 */
	def potentialParent = { attrs ->

		Variable child   = attrs.child
		Variable parent  = attrs.parent

		Relationship relationship = this.delphiService.getMyCurrentRelationship( parent, child )
		Boolean isSelected = relationship?.exists
		Boolean isVisible = true // TODO: Make invisible when all people said it doesn't exist...

		out << "<li id='${parent.label}-variable-item' class='variable-item ${isVisible ? '' : 'hide'}'>"
		{
			dumpPotentialParentSummary( parent )
			dumpPotentialParentLabel( parent, isSelected )
		}
		out << "</li>"

		out << bnElicit.potentialParentDialog([
			child: child,
			parent: parent,
			relationship: relationship,
			isSelected: isSelected
		])
		
	}

	/**
	 * @attr var REQUIRED
	 */
	def newVariableForm = { attrs ->

		Variable variable = attrs.var

		out << """
			<a href="javascript:toggleAddVariable( true )">
				<img src='${resource(dir: 'images/icons', file: 'pencil.png')}' />
				Add another variable which is not listed
			</a>
			<div id="new-var-form" class="dialog" style="display: none;">
			"""


		// I guess if we're getting picky, we really shouldn't be here (because we
		// can't elicit parents for variables with no potential parents
		if ( variable.variableClass.potentialParents.size() == 0 )
		{
			out << """
				<p>
					Sorry, but because ${variable.readableLabel} is a ${variable.variableClass.name}
					variable, we wont be modelling any other variables which influence it.
				</p>
				<input type="button" value="Okay" onclick="toggleAddVariable( false )" class="" />
				"""
		}
		else
		{
			out << """
				<form action="${createLink( [ action: "addVariable" ] )}">
					<intput type="hidden" name="returnToVar" value="${variable.label}" />
					<label>
						Name:
						<input type="text" id="inputNewVariableLabel" name="label"></input>
					</label>
					"""


			// We don't need to ask if there is only one possibility --}%
			if ( variable.variableClass.potentialParents.size() == 1 )
			{
				out << "<input type='hidden' name='variableClassName' value='${variable.variableClass.potentialParents[ 0 ].name}' />\n"
			}
			else
			{
				out << """
					<label for="newVarClass">Type</label>
					<select id="newVarClass" name="variableClassName">
					"""

				variable.variableClass.potentialParents.each {
					out << "<option value='${it.name}'>${it.niceName} variable</option>"
				}

				out << "</select>"

				out << bn.tooltip( [] ) {
"""This helps us decide which other variables your new one will be allowed to influence. We will describe them using examples from a model of diagnosing lung cancer:

 - Problem Variables: The variables of interest (e.g. does the patient have cancer?).

 - Background variables: Information available before the problem variables occur (e.g. does the patient smoke?).

 - Symptom variables: Observable consequences of problem variables (e.g. shortness of breath).

 - Mediating variables: unobservable variables which may also cause the same symptoms as the problem variables (e.g. are they asthmatic?). This helps to correctly model the relationship between problem and symptom variables</bn:tooltip>
"""
					}
			}

			out << """
				<label>
					Description:
					<textarea id="newVarDescription" name="description"></textarea>
				</label>

				<input type="button" value="Cancel" onclick="toggleAddVariable( false )" class="" />
				<input type="submit" value="Save" class="" />
			</form>
			"""

		}

		out << "</div>"

	}

}