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
	UserService userService

	static Integer idCounter = 1

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
	 * @attr agreement REQUIRED
	 */
	def agreementCharts = { attrs ->

		Agreement agreement = attrs.agreement

		if ( agreement.current )
		{
			agreement = this.delphiService.calcAgreement( agreement.parent, agreement.child, this.delphiService.getMyPreviousRelationship( agreement.parent, agreement.child ) )
		}

		boolean byMe = ( ( agreement.specifiedBy & Agreement.BY_ME ) == Agreement.BY_ME );
		boolean byOthers = ( ( agreement.specifiedBy & Agreement.BY_OTHERS ) == Agreement.BY_OTHERS );

		out << "<div class='me'>"

		if ( byMe )
		{
			out << """
				<div class='previous-description yes'>
					I thought it does and I was ${ConfidenceValue.create( agreement.myConfidence ).toString()}
					${bnElicit.indicatorBar( [ widthPercent: agreement.myConfidence ] )}
				</div>
				"""
		}
		else
		{
			out << """
				<div class='previous-description no'>
					I thought it doesn't
				</div>
				${bnElicit.indicatorBar( [ widthPercent: 0 ] )}
				"""
		}

		out << "</div><div class='others'>"

		if ( byOthers )
		{
			boolean isMajority = agreement.othersCount > userService.expertCount / 2;
			boolean isGoodConfidence = agreement.othersConfidence > 50;

			String description = isMajority ? "" : "Only";

			Integer othersPercent = (Double)agreement.othersCount / userService.expertCount * 100;

			String andOrBut = "and"
			if ( isMajority && !isGoodConfidence || !isMajority && isGoodConfidence )
			{
				andOrBut = "but"
			}
			String only = isGoodConfidence ? "" : "only"

			out << """
				<div class='previous-description yes'>
					${description} ${agreement.othersCount} of ${userService.expertCount} others thought it does
				</div>
				${bnElicit.indicatorBar( widthPercent: othersPercent )}
				${bnElicit.indicatorBar(
					widthPercent: agreement.othersConfidence,
					label: "${andOrBut} they were (on average) ${only} ${ConfidenceValue.create( agreement.othersConfidence )}"
				)}
				"""
		}
		else
		{
			out << """
				<div class='previous-description no'>
					All others thought it doesn't
				</div>
				${bnElicit.indicatorBar( [ widthPercent: 0 ] )}
				${bnElicit.indicatorBar( [ widthPercent: 0 ] )}
				"""
		}

		out << "</div>"
	}

	/**
	 * @attr widthPercent REQUIRED
	 * @attr label
	 */
	def indicatorBar = { attrs ->

		Integer widthPercent = attrs.widthPercent
		String label = attrs.containsKey( 'label' ) ? attrs.label : ""

		String id = ++idCounter
		String output = ""

		output += "<div class='bar-label-wrapper'>\n"
		output += "	<div id='bar-${id}' class='bar-chart'></div>\n"
		if ( label )
		{
			output += "	<div class='var-confidence-label'>${label}</div>\n"
		}
		output += "</div>\n"

		output += "<script type='text/javascript'>\n"
		output += "	\$( document ).ready( function(){ \n"
		output += "		\$( '#bar-${id}' ).slider({ range: 'min', min: 0, max: 100, value: ${widthPercent} } ).slider( 'disable' );\n"
		output += "	});\n"
		output += "</script>\n"

		out << output;
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

		Map<String,List<String>> variableLists = [:]

		String firstRound = "firstRound"
		String agree      = g.message( code: 'elicit.parents.agree-with-others' )
		String disagree   = g.message( code: 'elicit.parents.disagree-with-others' )
		String hidden     = g.message( code: 'elicit.parents.nobody-wants-these', args: [ child.readableLabel ] )

		( delphiService.phase == 1
			? [ firstRound ]
			: [ agree, disagree, hidden ]
		).each { variableLists.put( it, [] ) }

		for ( Variable parent in potentialParents )
		{
			if ( parent != child )
			{
				Agreement agreement = null
				if ( delphiService.hasPreviousPhase )
				{
					agreement = delphiService.calcAgreement( parent, child )
				}

				String item = bnElicit.potentialParent( child: child, parent: parent, agreement: agreement )
				String listToPut

				if ( delphiService.phase == 1 )
				{
					listToPut = firstRound
				}
				else if ( !showParent( agreement ) )
				{
					listToPut = hidden
				}
				else if ( agreement?.agree )
				{
					listToPut = agree
				}
				else
				{
					listToPut = disagree
				}

				variableLists[ listToPut ].add( item )
			}
		}

		if ( delphiService.phase == 1 )
		{
			variableLists[ firstRound ].add(
				"""
				<li id="add-variable-item" class=" variable-item new-var">
					${bnElicit.newVariableForm( var: child )}
				</li>
				"""
			)
		}

		variableLists.each { entry ->

			if ( entry.key != firstRound )
			{
				out << "<h2>$entry.key</h2>"
			}

			out << """
				<ul class="potential-parents-list variable-list">
					${entry.value.join( '\n' )}
				</ul>
				"""
		}

	}

	private static Boolean showParent( Agreement agreement )
	{
		return !( agreement && agreement.specifiedBy == Agreement.BY_NEITHER )
	}

	private void dumpPotentialParentLabel( Variable parent, Boolean selected )
	{

		out << """
			<input type='hidden' name='confidence[${parent.label}]' value='' />
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
	 * @attr agreement    REQUIRED
	 * @attr isSelected   REQUIRED
	 */
	def potentialParentDialog = { attrs ->

		Variable child            = attrs.child
		Variable parent           = attrs.parent
		Relationship relationship = attrs.relationship
		Agreement agreement       = attrs.agreement
		Boolean isSelected        = attrs.isSelected

		out << """
			<div id='${parent.label}-details' class='var-details floating-dialog'>
				<div class='header-wrapper'>
					${delphiService.hasPreviousPhase && agreement != null ? "<span class='header'>This time</span>" : ''}
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
					<tr class='my-confidence ${relationship?.exists ? '' : 'hidden'}'>
						<th>
							<span class='confidence-label'>How confident are you?</span>
						</th>
						<td>
							<div
								id='${parent.label}-confidence-slider'
								class='slider var-confidence-slider'>
							</div>
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

		if ( agreement != null )
		{
			String agreeClass = ( agreement.specifiedBy == Agreement.BY_ME || agreement.specifiedBy == Agreement.BY_OTHERS ) ? "disagree" : "agree"
			out << """
				<div class='agreement'>
					<div class='header'>Last time</div>
					<span class='${agreeClass}'>
						${bnElicit.agreementCharts( [ agreement: agreement ] )}
					</span>
				</div>
				"""
		}

		List<Relationship> relationshipsToShowCommentsFor = this.delphiService.hasPreviousPhase ? this.delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child ) : [ relationship ]

		out << """
			${bnElicit.reasonsList( [ relationships: relationshipsToShowCommentsFor ] )}
			${bn.saveButtons( [ atTop: false ] )}
		</div>
		"""

		if ( relationship != null )
		{
			out << """
				<script type='text/javascript'>
					\$( document ).ready( function(){
						sliderValues.${parent.label} = ${relationship.confidence == null ? 0 : relationship.confidence};
					});
				</script>
				"""
		}	
	}

	/**
	 * Displays a list element which portrays a variable which is primed to be selected as a parent of child.
	 * If the user has already viewed and saved a relationship for this pair of variables, we will retrieve that.
	 * If an agreement is specified, we will just not look it up ourself.
	 * @attrs child REQUIRED
	 * @attrs parent REQUIRED
	 * @attrs agreement
	 */
	def potentialParent = { attrs ->

		Variable varChild   = attrs.child
		Variable varParent  = attrs.parent
		Agreement agreement = attrs.containsKey( 'agreement' ) ? attrs.agreement : null

		Relationship relationship = this.delphiService.getMyCurrentRelationship( varParent, varChild )

		if ( delphiService.hasPreviousPhase && relationship == null && !agreement?.myRelationship?.isCurrent() )
		{
			// Display their answer from last round if they haven't already provided one here.
			relationship = agreement.myRelationship
		}

		Boolean isVisible = showParent( agreement )
		Boolean isSelected = relationship?.exists

		String agreementClass = "";
		if ( delphiService.phase > 1 )
		{
			agreementClass = agreement?.agree ? 'agree' : 'disagree'
		}

		out << "<li id='${varParent.label}-variable-item' class='variable-item ${isVisible ? '' : 'hide'} ${agreementClass}'>"

			dumpPotentialParentSummary( varParent )
			dumpPotentialParentLabel( varParent, isSelected )

		out << "</li>"

		out << bnElicit.potentialParentDialog([
			child: varChild,
			parent: varParent,
			relationship: relationship,
			agreement: agreement,
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