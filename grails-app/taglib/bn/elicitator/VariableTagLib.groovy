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
	static returnObjectForTags = ['mostRecentComment']

	VariableService variableService

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
		def includeDescription = true
		if ( attrs.containsKey( "includeDescription" ) )
		{
			includeDescription = attrs.remove( "includeDescription" )
		}
		out << """<span class='variable'>${var.readableLabel}${includeDescription ? ' ' + bn.variableDescription( [ var: var ] ) : ''}</span>"""
	}

	public static String generateTooltip( String tooltip, String id = null, String classes = null, String content = null, Boolean includeAlert = true )
	{
		String jsTooltip = tooltip.replaceAll( "'", "\\\\'" ).replaceAll( "\n", "\\\\n" )

		id = id ? "id='${id}'" : ""

		if ( content == null ) {
			content = "(?)"
		}

		String a    = includeAlert ? "<a href='javascript:alert( \"${jsTooltip}\" );'>" : ""
		String aEnd = includeAlert ? "</a>" : ""

		// Had to fudge the formatting of the HTML so that there was not a space after the tooltip.
		return """<span ${id} class="tooltip ${classes ?: ''}">${a}${content}${aEnd}<span class="tip">${tooltip.encodeAsHTML().replaceAll( "\n", " <br /> " )}</span></span>""".trim().replaceAll( "\n", "" )
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
	 * @attr relationship
	 * @attr child
	 * @attr parent
	 */
	def mostRecentComment = { attrs ->

		assert attrs.containsKey( "relationship" ) || ( attrs.containsKey( "child" ) && attrs.containsKey( "parent" ) )

		Relationship relationship = null;
		if ( attrs.containsKey( "relationship" ) ) {
			relationship = attrs.remove( "relationship" )
		}

		if ( relationship == null ) {
			Variable child = attrs.child
			Variable parent = attrs.parent
			relationship = Relationship.findByCreatedByAndDelphiPhaseAndExistsAndParentAndChild( ShiroUser.current, AppProperties.properties.delphiPhase, true, parent, child )
		}

		relationship?.mostRecentComment?.comment
	}

	/**
	 * @attr chain REQUIRED
	 * @attr includeTooltip
	 */
	def variableChain = { attrs ->

		List<Variable> chain = attrs.chain
		def includeTooltip = true
		if ( attrs.containsKey( "includeTooltip" ) )
		{
			includeTooltip = attrs.remove( "includeTooltip" )
		}

		String output = "";
		for ( int i = 0; i < chain.size(); i ++ )
		{
			output += bn.variable( [ var: chain[ i ], includeDescription: includeTooltip ] )

			if ( i < chain.size() - 1 )
			{
				Relationship rel = Relationship.findByCreatedByAndDelphiPhaseAndExistsAndParentAndChild( ShiroUser.current, AppProperties.properties.delphiPhase, true, chain[ i ], chain[ i + 1 ] )
				String comment = rel?.mostRecentComment?.comment
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
	 */
	def listSummary = { attrs ->

		List<Variable> variables = attrs.variables
		List<Variable> stillToVisit = attrs.stillToVisit
		out << "<ul id='all-children-list' class='variable-list '>\n"

		variables.eachWithIndex { child, i ->
			Boolean hasVisited = !stillToVisit.contains( child )
			String classNeedsReview = hasVisited ? 'doesnt-need-review' : 'needs-review'
			out << """
				<li class='variable-item  ${classNeedsReview}'>
					<a href='${createLink( controller: 'elicit', action: 'parents', params: [ for: child.label ] )}'>${bn.variable( [ var: child, includeDescription: false ] )}</a>
				</li>
				"""
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

			out << """
				<li class='variable-item  ${classes}'>
					<a href='${createLink( controller: 'elicit', action: 'parents', params: [ for: child.label ] )}'>${bn.variable( [ var: child, includeDescription: false ] )}</a>
				</li>
				"""
		}

		out << "</ul>\n"
	}

}
