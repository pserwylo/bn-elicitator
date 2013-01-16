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

class VariableTagLib {

	static namespace = "bn"

	VariableService variableService
	DelphiService delphiService
	UserService userService

	static Integer idCounter = 1

	/**
	 * @attr id
	 * @attr classes
	 */
	def tooltip = { attrs, body ->
		String id = attrs.id;
		String classes = attrs.classes;
		out << generateTooltip( (String)(body()), id, classes )
	}

	/**
	 * @attr var REQUIRED
	 * @attr includeDescription Defaults to true
	 */
	def variable = { attrs ->
		Variable var = attrs.var
		boolean includeDescription = attrs.containsKey( "includeDescription" ) ? attrs.includeDescription : true
		out << """<span class='variable'>${var.readableLabel}${includeDescription ? ' ' + generateDescription( var ) : ''}</span>"""
	}

	public static String generateTooltip( String tooltip, String id = null, String classes = null, String content = null )
	{
		String jsTooltip = tooltip.replaceAll( "'", "\\\\'" ).replaceAll( "\n", "\\\\n" )

		id = id ? "id='${id}'" : ""

		if ( content == null ) {
			content = "(?)"
		}

		// Had to fudge the formatting of the HTML so that there was not a space after the tooltip.
		return """<span ${id} class="tooltip ${classes ?: ''}"><a href="javascript:alert( '${jsTooltip}' );">${content}</a><span class="tip">${tooltip.encodeAsHTML().replaceAll( "\n", " <br /> " )}</span></span>""".trim().replaceAll( "\n", "" )
	}

	public static String generateDescription( Variable var )
	{
		String description = "";
		if ( var?.description?.size() > 0 )
		{
			description = generateTooltip( var.description )
		}
		return description
	}

	/**
	 * @attr chain REQUIRED
	 * @attr includeTooltip
	 */
	def relationshipChain = { attrs ->

		List<Relationship> chain = attrs.chain
		Boolean includeTooltip = attrs.hasProperty( "includeTooltip" ) ? attrs.includeTooltip : true

		for ( int i = 0; i < chain.size(); i ++ )
		{
			Relationship rel = chain.get( i )

			if ( i == 0 )
			{
				out << bn.variable( [ var: rel.parent, includeDescription: includeTooltip ] )
			}

			out << bn.rArrow( [ comment: rel.comment?.comment ] )
			out << bn.variable( [ var: rel.child, includeDescription: includeTooltip ] )

		}
	}

	/**
	 * @attr chain REQUIRED
	 * @attr includeTooltip
	 */
	def variableChain = { attrs ->

		List<Variable> chain = attrs.chain
		Boolean includeTooltip = attrs.hasProperty( "includeTooltip" ) ? attrs.includeTooltip : true

		String output = "";
		for ( int i = 0; i < chain.size(); i ++ )
		{
			output += bn.variable( [ var: chain[ i ], includeDescription: includeTooltip ] )

			if ( i < chain.size() - 1 )
			{
				Relationship rel = Relationship.findByCreatedByAndDelphiPhaseAndExistsAndParentAndChild( ShiroUser.current, AppProperties.properties.delphiPhase, true, chain[ i ], chain[ i + 1 ] )
				String comment = rel?.comment?.comment
				if ( comment == null ) {
					output += bn.rArrow()
				} else {
					output += generateTooltip( comment, null, null, r.img( [ dir: "images/icons-custom", file: "arrow_right_comment.png" ] ).toString() )
				}
			}
		}
		out << output

	}

	/**
	 * @attr var REQUIRED
	 */
	def variableDescription = { attrs ->

		out << generateDescription( attrs.var )

	}

	private static String generateSynonyms( Variable var, boolean includeLabel = false )
	{
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
			output += '<ul class="synonyms">'
			for ( Iterator<String> it = synonyms.iterator(); it.hasNext(); )
			{
				String synonym = it.next()
			}
			output += "</ul>"
		}

		return output

	}

	/**
	 * @attr var REQUIRED
	 * @attr includeLabel
	 */
	def variableSynonyms = { attrs ->

		Variable var = attrs.var
		boolean includeLabel = attrs.includeLabel
		out << generateSynonyms( var, includeLabel )
	}

	private String generateAgreementMatrix( List<Agreement> agreementList, Boolean showDisagreements = false )
	{

		List<String> bothYes = []
		List<String> bothNo = []
		List<String> meNotOthers = []
		List<String> othersNotMe = []
		for ( Agreement agreement in agreementList )
		{
			if ( agreement.specifiedBy == Agreement.BY_BOTH )
			{
				bothYes.add( agreement.parent.readableLabel )
			}
			else if ( agreement.specifiedBy == Agreement.BY_NEITHER )
			{
				bothNo.add( agreement.parent.readableLabel )
			}
			else if ( agreement.specifiedBy == Agreement.BY_ME )
			{
				meNotOthers.add( agreement.parent.readableLabel )
			}
			else if ( agreement.specifiedBy == Agreement.BY_OTHERS )
			{
				othersNotMe.add( agreement.parent.readableLabel )
			}
		}

		String hidden = showDisagreements ? '' : "<span class='none'>None</span>"

		String bothYesOutput = bothYes.size() > 0 ? "<ul><li>" + bothYes.join( "</li><li>" ) + "</li></ul>" : hidden
		String bothNoOutput = bothNo.size() > 0 ? "<ul><li>" + bothNo.join( "</li><li>" ) + "</li></ul>" : hidden
		String meNotOthersOutput = meNotOthers.size() > 0 ? "<ul><li>" + meNotOthers.join( "</li><li>" ) + "</li></ul>" : hidden
		String othersNotMeOutput = othersNotMe.size() > 0 ? "<ul><li>" + othersNotMe.join( "</li><li>" ) + "</li></ul>" : hidden

		String matrix = "<table>\n"
		matrix += "	<tr>\n"
		matrix += "		<th class='empty'></th>\n"
		matrix += "		<th class='empty'></th>\n"
		matrix += "		<th colspan='2'>Others</th>\n"
		matrix += "	</tr>\n"
		matrix += "	<tr>\n"
		matrix += "		<th class='empty'></th>\n"
		matrix += "		<th class='empty'></th>\n"
		matrix += "		<th class='yes'>Influenced by</th>\n"
		matrix += "		<th class='no'>Not influence by</th>\n"
		matrix += "	</tr>\n"
		matrix += "	<tr>\n"
		matrix += "		<th rowspan='2'>Me</th>\n"
		matrix += "		<th class='yes'>Influenced by</th>\n"
		matrix += "		<td><div class='agree'>" + bothYesOutput + "</div></td>\n"
		matrix += "		<td><div class='disagree'>" + meNotOthersOutput + "</div></td>\n"
		matrix += "	</tr>\n"
		matrix += "	<tr>\n"
		matrix += "		<th class='no'>Not influenced by</th>\n"
		matrix += "		<td><div class='disagree'>" + othersNotMeOutput + "</div></td>\n"
		matrix += "		<td><div class='agree'>" + bothNoOutput + "</div></td>\n"
		matrix += "	</tr>\n"
		matrix += "</table>\n"
		return matrix
	}

	private String generateAgreementString( Agreement agreement) {
		String string = "";
		boolean byMe = ( ( agreement.specifiedBy & Agreement.BY_ME ) == Agreement.BY_ME );
		boolean byOthers = ( ( agreement.specifiedBy & Agreement.BY_OTHERS ) == Agreement.BY_OTHERS );
		if ( byMe )
		{
			string += "<span class='me yes'>I thought it does, with " + agreement.myConfidence + "% confidence</span>";
		}
		else
		{
			string += "<span class='me no'>I thought it doesn't</span>"
		}

		string += "<br />";

		if ( byOthers )
		{
			string += "<span class='others yes'>" + agreement.othersCount + " others thought it does, with  " + agreement.othersConfidence + "% confidence</span>"
		}
		else
		{
			string += "<span class='others no'>All others thought it doesn't</span>"
		}
		return string
	}

	/**
	 * @param agreement If this is null, we will still output the ul and its wrapper, it will just be empty.
	 * @return
	 */
	private String generateReasonsList( List<Relationship> relationships )
	{
		String output = ""
		output += "<div class='reasons'>\n"
		output += "	<span class='header'>Reasons ${img( [ dir: "images/icons", file: "comments.png" ] )}</span>\n"

		boolean hasReasons = false

		output += "<div  class='list-wrapper'>\n"
		output += "	<ul class='reasons-list'>\n"
		for ( Relationship relationship in relationships )
		{
			if ( relationship != null )
			{
				Comment comment = relationship.comment
				if ( comment?.comment?.trim()?.size() > 0 )
				{
					hasReasons = true;

					boolean isMine = comment.createdBy == ShiroUser.current
					String author = isMine ? "Myself" : "Other participant"
					String className = "phase-" + relationship.delphiPhase
					className += isMine ? " me" : " other"
					className += relationship.exists ? " exists" : " doesnt-exist"
					output += "		<li class='${className}'>\n"
					output += "			\"${comment.comment}\"\n"
					output += "			<div class='author'> - ${author}</div>\n"
					output += "		</li>\n"
				}
			}
		}

		output += "	</ul>\n"
		output += "</div>\n"

		if ( !hasReasons )
		{
			output += "<div class='no-reasons'>No reasons given.</div>\n"
		}

		output += "</div>\n"
		return output
	}

	private String generateAgreementCharts( Agreement agreement ) {

		if ( agreement.current )
		{
			agreement = this.delphiService.calcAgreement( agreement.parent, agreement.child, this.delphiService.getMyPreviousRelationship( agreement.parent, agreement.child ) )
		}

		String string = "";
		boolean byMe = ( ( agreement.specifiedBy & Agreement.BY_ME ) == Agreement.BY_ME );
		boolean byOthers = ( ( agreement.specifiedBy & Agreement.BY_OTHERS ) == Agreement.BY_OTHERS );

		string += "<div class='me'>\n"
		if ( byMe )
		{
			string += "<div class='previous-description yes'>I thought it does and I was ${ConfidenceValue.create( agreement.myConfidence ).toString()}</div>"
			string += "	" + generateBar( agreement.myConfidence,  );
		}
		else
		{
			string += "<div class='previous-description no'>I thought it doesn't</div>\n"
			string += generateBar( 0 );
		}
		string += "</div>\n"

		string += "<div class='others'>\n"
		if ( byOthers )
		{
			boolean isMajority = agreement.othersCount > userService.expertCount / 2;
			boolean isGoodConfidence = agreement.othersConfidence > 50;

			String description = isMajority ? "" : "Only";
			string += "<div class='previous-description yes'>${description} ${agreement.othersCount} of ${userService.expertCount} others thought it does</div>\n"

			Integer othersPercent = (Double)agreement.othersCount / userService.expertCount * 100;
			string += generateBar( othersPercent );

			String andOrBut = "and"
			if ( isMajority && !isGoodConfidence || !isMajority && isGoodConfidence )
			{
				andOrBut = "but"
			}
			String only = isGoodConfidence ? "" : "only"
			string += generateBar( agreement.othersConfidence, "${andOrBut} they were (on average) ${only} ${ConfidenceValue.create( agreement.othersConfidence )}" );
		}
		else
		{
			string += "<div class='previous-description no'>All others thought it doesn't</div>"
			string += generateBar( 0 );
			string += generateBar( 0 );
		}
		string += "</div>"

		return string
	}

	private String generateBar( Integer widthPercent, String label = "" ) {

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


		return output;

		/*String bar = "<div class='bar-chart'>\n"
		bar += "	<div class='bar ${className}'>.</div>"
		bar += "</div>\n"
		return bar*/
	}

	/**
	 * Produce a summary list of variables.
	 * This will include:
	 *  - links to elicit parents for a variable
	 *  - if we are past the first delphi round, show variables which you agree/disagree about
	 * @attr variables REQUIRED
	 * @attr stillToVisit REQUIRED Variables which are yet to be seen, so we should so some sort of info telling them this...
	 * @attr hideAgree
	 * @attr hideDisagree
	 */
	def listSummary = { attrs ->

		Boolean showDisagreements = attrs["showDisagreements"] != null ? (Boolean)attrs["showDisagreements"] : true
		List<Variable> variables = attrs.variables
		List<Variable> stillToVisit = attrs.stillToVisit
		out << "<ul id='all-children-list' class='variable-list '>\n"

		for( Variable child in variables )
		{
			List<Agreement> agreements = []
			List<Variable> potentialParents = this.variableService.getPotentialParents( child )

			if ( delphiService.hasPreviousPhase )
			{
				for ( Variable parent in potentialParents )
				{
					Agreement agreement = this.delphiService.calcAgreement( parent, child )
					agreements.add( agreement )
				}
			}

			Boolean hasVisited = !stillToVisit.contains( child )
			String classes = hasVisited ? 'doesnt-need-review' : 'needs-review'
			String needsReview = hasVisited ? '' : '<span class="stats">(needs review)</span>'

			out << "<li class='variable-item  ${classes}'>\n"
			out << "	<a href='" + createLink( controller: "elicit", action: "parents", params: [ for: child.label ] ) + "'>${child.readableLabel}</a> ${needsReview}\n"
			out << "	<span class='agreement-summary item-description'>\n"
			out << "		<span class='short'>\n"
			out << "			Disagree with " + agreements.count{ it -> !it.agree } + " of " + potentialParents.size() + " relationships\n"
			out << "		</span>\n"
			/*
			out << "		<span class='long' style='display: none'>\n"
			out << 				generateAgreementMatrix( agreements, showDisagreements )
			out << "		</span>\n"
			*/
			out << "	</span>\n"
			out << "	" + this.generateListsOfRelations( child ) + "\n";
			out << "</li>\n"
		}
		out << "</ul>"

	}

	/**
	 * @attrs variables REQUIRED
	 * @attr stillToVisit REQUIRED Variables which are yet to be seen, so we should so some sort of info telling them this...
	 */
	def listSummaryFirstPhase = { attrs ->

		List<Variable> variables = attrs.variables
		List<Variable> stillToVisit = attrs.stillToVisit

		out << "<ul id='all-children-list' class='variable-list'>\n"

		for( Variable child in variables )
		{
			Boolean hasVisited = !stillToVisit.contains( child )
			String classes = hasVisited ? 'doesnt-need-review' : 'needs-review'
			String needsReview = hasVisited ? '' : '<span class="info">(needs review)</span>'

			out << "<li class='variable-item  ${classes}'>\n"
			out << "	<a href='${createLink( controller: 'elicit', action: 'parents', params: [ for: child.label ] )}'>${bn.variable( [ var: child, includeDescription: false ] )}</a>\n"
			out << "	" + needsReview + "\n"
			out << "	" + this.generateListsOfRelations( child ) + "\n";
			out << "</li>\n"
		}

		out << "</ul>\n"
	}

	/**
	 * Iterates over each potentialParents and invokes the potentialParent taglib.
	 * If we are in subsequent phases, we don't show variables which received no love from anybody in the previous phase.
	 * @attr child REQUIRED
	 * @attr potentialParents REQUIRED
	 */
	def potentialParentsList = { attrs ->
		List<Variable> potentialParents = attrs['potentialParents']
		Variable child = attrs['child']

		for ( Variable parent in potentialParents )
		{
			if ( parent != child )
			{
				Agreement agreement = null
				if ( delphiService.hasPreviousPhase )
				{
					agreement = delphiService.calcAgreement( parent, child )
				}
				this.dumpPotentialParent( child, parent, agreement )
			}
		}

	}

	private Boolean showParent( Variable child, Variable parent, Agreement agreement )
	{
		return !( agreement && agreement.specifiedBy == Agreement.BY_NEITHER )
	}

	private void dumpPotentialParentLabel( Variable parent, Boolean selected, visible )
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
				${generateDescription( parent )}
				${generateSynonyms( parent )}

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
					<input type="button" value="Show details" class="show-var-details " onclick="showVarDetails( '${parent.label}' )" />
				</span>
			</div>
			"""
	}
	
	private void dumpPotentialParentDialog( Variable child, Variable parent, Relationship relationship, Agreement agreement, Boolean isSelected )
	{

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
						${generateAgreementCharts( agreement )}
					</span>
				</div>
				"""
		}

		List<Relationship> relationshipsToShowCommentsFor = this.delphiService.hasPreviousPhase ? this.delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child ) : [ relationship ]

		out << """
			${generateReasonsList( relationshipsToShowCommentsFor )}
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
	 */
	private void dumpPotentialParent( Variable varChild, Variable varParent, Agreement agreement = null )
	{

		Relationship relationship = this.delphiService.getMyCurrentRelationship( varParent, varChild )

		if ( delphiService.hasPreviousPhase && relationship == null && !agreement?.myRelationship?.isCurrent() )
		{
			// Display their answer from last round if they haven't already provided one here.
			relationship = agreement.myRelationship
		}

		Boolean isVisible = showParent( varChild, varParent, agreement )
		Boolean isSelected = relationship?.exists

		String agreementClass = "";
		if ( delphiService.phase > 1 )
		{
			agreementClass = agreement?.agree ? 'agree' : 'disagree'
		}

		out << "<li id='${varParent.label}-variable-item' class='variable-item ${isVisible ? '' : 'hidden'} ${agreementClass}'>"

			dumpPotentialParentSummary( varParent )
			dumpPotentialParentLabel( varParent, isSelected, isVisible )

		out << "</li>"

		dumpPotentialParentDialog( varChild, varParent, relationship, agreement, isSelected )
		
	}

	/**
	 * @attr atTop REQUIRED Specifies whether to style the buttons as if they are at the top of the form or the bottom.
	 * @attr includeDelete
	 */
	def saveButtons = { attrs ->
		Boolean atTop = attrs.atTop
		Boolean includeDelete = attrs.containsKey( 'includeDelete' ) ? attrs.includeDelete : false
		out << """
			<span class='save-wrapper ${atTop ? "top" : "bottom"}'>
				<button class='close' type='button'>Close</button>
				"""

		if ( includeDelete ) {
			out << "<button class='delete' type='button'>Delete</button>"
		}

		out << """
				<button class='save' type='submit'>Save</button>
			</span>
			"""
	}

	/**
	 * Builds a comma separated string of the parents this user has specified for child (in this round or the previous
	 * round). It is wrapped in a &lt;span&gt; with class 'item-description'. Then builds another list of the children.
	 * @param child
	 * @return
	 */
	private String generateListsOfRelations( Variable child )
	{
		List<Relationship> parentRelationships = this.variableService.getSpecifiedRelationshipsByChild( child )
		List<Relationship> childRelationships  = this.variableService.getSpecifiedRelationshipsByParent( child )

		String output = ""

		if ( parentRelationships.size() == 0 || childRelationships.size() == 0 )
		{
			output = "<div class='item-description'>No relationships</div>"
		}
		else
		{
			output = """
				<table class='list-of-relations item-description'>
					<tr>
						<td class='parents'>
						"""

			if ( parentRelationships.size() > 0 )
			{
				output += "<ul>"
				parentRelationships.each {
					output += "<li>$it.parent.readableLabel ${bn.rArrow( [ comment: it.comment?.comment ] )}</li>\n"
				}
				output += "</ul>"
			}

			output += """
				</td>
				<td class='variable'>$child.readableLabel</td>
				<td class='children'>
				"""

			if ( childRelationships.size() > 0 )
			{
				output += "<ul>"
				childRelationships.each {
					output += "<li>" + bn.rArrow( [ comment: it.comment?.comment ] ) + " " + it.parent + "</li>\n"
				}
				output += "</ul>"
			}

			output += """</td></tr></table>"""

		}

		return output
	}

}
