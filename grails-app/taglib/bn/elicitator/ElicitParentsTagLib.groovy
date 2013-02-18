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
	DelphiService   delphiService
	UserService     userService

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
							<div class='author'> - ${author}${message( code: 'elicit.parents.comment-phase', args: [ it.delphiPhase ])}</div>
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
	 * Produces three lists:
	 *  - I said yes
	 *  - I said no
	 *  - We *all* said no (which is hidden)
	 * @attr child REQUIRED
	 * @attr potentialParents REQUIRED
	 */
	def potentialParentsListLaterRounds = { attrs ->

		List<Variable> potentialParents = attrs.potentialParents
		Variable child = attrs.child

		List<Variable> listYes   = []
		List<Variable> listNo    = []
		List<Variable> listNoAll = []
		Map<Variable, List<Relationship>> allRelationships = [:]
		Map<Variable, Integer>            allOthersCount   = [:]

		potentialParents.each { parent ->

			List<Relationship> relationships = delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child )

			Relationship       myCurrent      = relationships.find    { it.createdBy == ShiroUser.current && it.delphiPhase == delphiService.phase }
			Relationship       myPrevious     = relationships.find    { it.createdBy == ShiroUser.current && it.delphiPhase == delphiService.previousPhase }
			List<Relationship> othersPrevious = relationships.findAll { it.createdBy != ShiroUser.current }
			Relationship       myMostRecent   = myCurrent ?: myPrevious
			Integer            othersCount    = othersPrevious.count { it?.exists }

			allRelationships.put( parent, relationships )
			allOthersCount.put( parent, othersCount )

			if ( myMostRecent?.exists ) {
				listYes.add( parent )
			} else if ( othersCount == 0 ) {
				listNoAll.add( parent )
			} else {
				listNo.add( parent )
			}
		}

		Integer totalUsers = userService.expertCount

		def sortYes  = { low, high ->              allOthersCount.get( low ) <=>              allOthersCount.get( high ) }
		def sortNo   = { low, high -> totalUsers - allOthersCount.get( low ) <=> totalUsers - allOthersCount.get( high ) }
		def listItem = { parent, count ->

			List<String> countClasses = [ "low", "medium", "high" ]
			float   countPercent    = count / totalUsers
			int     countClassIndex = ( countClasses.size() - 1 ) - (int)( countPercent * countClasses.size() )

			out << """
			<li id='${parent.label}-variable-item' class='variable-item'>
				<span class='var-summary'>
					<span class='count ${countClasses[ countClassIndex ]}'>
						${message( code: 'elicit.parents.agreement-count', args : [ count, totalUsers, (int)( countPercent * 100 ) ] )}
					</span>
					<button class='review' value='${parent.label}'>Review</button>
				</span>
				${bn.variable( [ var: parent ] )}
			</li>
			"""
		}

		out << """
				<h2 class='review-yes' style='font-size: 1.0em; float: right;'>Others who also said "<strong>Yes</strong>"</h2>
				<h2 class='review-yes'>I said "Yes"</h2>
				<ul id='list-yes' class='review-yes potential-parents-list variable-list'>
				"""
			listYes.sort( sortYes ).each { parent ->
				listItem( parent, allOthersCount.get( parent ) )
			}
			out << """
				</ul>
			"""

		out << """
				<h2 class='review-no' style='font-size: 1.0em; float: right;'>Others who also said "<strong>No</strong>"</h2>
				<h2 class='review-no'>I said "No"</h2>
				<ul id='list-no' class='review-no potential-parents-list variable-list'>
				"""
		listNo.sort( sortNo ).each { parent ->
			listItem( parent, totalUsers - allOthersCount.get( parent ) )
		}
		out << """
				</ul>
			"""
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

		if ( delphiService.hasPreviousPhase ) {
			out << bnElicit.potentialParentsListLaterRounds( [ potentialParents: potentialParents, child: child ] )
			return
		}

		out << "<ul class='potential-parents-list variable-list'>"

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
		String message = delphiService.hasPreviousPhase ? message( code: "elicit.parents.review" ) : message( code:  "general.show" ) + " " + message( code: "elicit.parents.info" )
		out << """
			<div id='${parent.label}-summary' class='var-summary'>
				<span class="toggle-details">
					<button type="button" class="show-var-details " onclick="showVarDetails( '${parent.label}' )">
						$message
					</button>
				</span>
			</div>
			"""
	}

	/**
	 * If no attributes are specified, then we will render an empty form, prime to be populated via JSON objects.
	 * @attr child
	 * @attr parent
	 * @attr relationship
	 * @attr isSelected
	 */
	def potentialParentDialog = { attrs ->

		Variable child            = null
		Variable parent           = null
		Relationship relationship = null
		Boolean isSelected        = null

		if ( attrs.containsKey( 'child' ) && attrs.containsKey( 'parent' ) && attrs.containsKey( 'relationship' ) && attrs.containsKey( 'isSelected' ) ) {
			child        = attrs.remove( 'child' )
			parent       = attrs.remove( 'parent' )
			relationship = attrs.remove( 'relationship' )
			isSelected   = attrs.remove( 'isSelected' )
		}

		String dialogId     = parent ? parent.label + "-details"    : "details-form"
		String inputIdAttr  = parent ? "id='input-${parent.label}-form'" : ""
		String inputName    = parent ? "parents" : "exists"
		String comment      = relationship?.delphiPhase == delphiService.phase && relationship?.comment?.comment?.length() > 0 ? relationship.comment.comment : ''
		String commentLabel = parent ? "Why do you think this?" : "Do you have any further comments?"

		out << """
			<div id='$dialogId' class='var-details floating-dialog'>
				<table width="100%" class="form">
					<tr>
						<th></th>
						<td>
							<label>
								<input
									$inputIdAttr
									type='radio'
									${isSelected ? "checked='checked'" : ''}
									name='$inputName'
									value='yes'
									/>
								Yes it does
							</label>
							<br />
							<label>
								<input
									$inputIdAttr
									type='radio'
									${isSelected ? "checked='checked'" : ''}
									name='$inputName'
									value='no'
									/>
								No it doesn't
							</label>
						</td>
					</tr>
					<tr>
						<th>
							$commentLabel
						</th>
						<td>
							<div class='my-comment'>
								<textarea name='comment'>$comment</textarea>
							</div>
						</td>
					</tr>
				</table>
				<div class='header-wrapper'>
					${bn.saveButtons( [ atTop: true ] )}
				</div>
				"""

		List<Relationship> relationshipsToShowCommentsFor = parent ?
			( this.delphiService.hasPreviousPhase ?
				this.delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child, false ) :
				[ relationship ] ) :
			[]

		out << """
			${bnElicit.reasonsList( [ relationships: relationshipsToShowCommentsFor ] )}
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

		out << "<li id='${parent.label}-variable-item' class='variable-item'>"

			dumpPotentialParentSummary( parent )
			dumpPotentialParentLabel( parent, isSelected )

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