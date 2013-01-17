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
		out << """<span class='variable'>${var.readableLabel}${includeDescription ? ' ' + bn.variableDescription( [ var: var ] ) : ''}</span>"""
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

	/**
	 * @attr var REQUIRED
	 */
	def variableDescription = { attrs ->
		Variable var = attrs.var
		String description = "";
		if ( var?.description?.size() > 0 )
		{
			description = generateTooltip( var.description )
		}
		out << description
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
					output += generateTooltip( comment, null, null, (String)r.img( [ dir: "images/icons-custom", file: "arrow_right_comment.png" ] ).toString() )
				}
			}
		}
		out << output

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

			out << """
				<li class='variable-item  ${classes}'>
					${bn.variableInListWithRelationships( [ variable: child ] )}
					${needsReview}
					<span class='agreement-summary item-description'>
						<span class='short'>
							Disagree with ${agreements.count{ it -> !it.agree }} of ${potentialParents.size()} relationships
						</span>
					</span>
				</li>
				"""
		}
		out << "</ul>"

	}

	/**
	 * @attr variable REQUIRED
	 */
	def variableInListWithRelationships = { attrs ->
		Variable variable = attrs.variable
		out << """
			<table>
				<tr>
					<td>
						${this.generateListOfParents( variable )}
					</td>
					<td class='variable-cell'>
						<a href='${createLink( controller: 'elicit', action: 'parents', params: [ for: variable.label ] )}'>${bn.variable( [ var: variable, includeDescription: false ] )}</a>
					</td>
					<td>
						${this.generateListOfChildren( variable )}
					</td>
				</tr>
			</table>
		"""
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

			out << """
				<li class='variable-item  ${classes}'>
					${bn.variableInListWithRelationships( [ variable: child ] )}
					${needsReview}
				</li>
				"""
		}

		out << "</ul>\n"
	}

	private String generateListOfChildren( Variable var )
	{
		List<Relationship> childRelationships  = this.variableService.getSpecifiedRelationshipsByParent( var )

		String output = "<div class='list-of-children item-description'>"

		if ( childRelationships.size() > 0 )
		{
			output += "<ul>"
			childRelationships.each {
				output += "<li>" + bn.rArrow( [ comment: it.comment?.comment ] ) + " " + it.child + "</li>\n"
			}
			output += "</ul>"
		}
		else
		{
			output += bn.rArrow() + " " + g.message( [ code: "elicit.list.no-variables" ] )
		}

		output += "</div>"

		return output
	}

	private String generateListOfParents( Variable var )
	{
		List<Relationship> parentRelationships = this.variableService.getSpecifiedRelationshipsByChild( var )

		String output = "<div class='list-of-parents item-description'>"

		if ( parentRelationships.size() > 0 )
		{
			output += "<ul>"
			parentRelationships.each {
				output += "<li>$it.parent.readableLabel ${bn.rArrow( [ comment: it.comment?.comment ] )}</li>\n"
			}
			output += "</ul>"
		}
		else
		{
			output += g.message( [ code: "elicit.list.no-variables" ] ) + " " + bn.rArrow()
		}

		output += "</div>"

		return output
	}

}
