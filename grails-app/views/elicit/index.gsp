%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
  -
  - This program is free software: you can redistribute it and/or modify
  - it under the terms of the GNU General Public License as published by
  - the Free Software Foundation, either version 3 of the License, or
  - (at your option) any later version.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU General Public License for more details.
  -
  - You should have received a copy of the GNU General Public License
  - along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --}%
<%@ page import="bn.elicitator.ShiroUser; bn.elicitator.Variable" %>
<!doctype html>

<g:set var="hasReviewedSome" value="${variables.size() > stillToVisit?.size()}" />
<g:set var="hasDetails" value="${delphiPhase > 1 || variables.size() > stillToVisit?.size()}" />

<html>

	<head>
		<meta name="layout" content="main">
		<title>Identify relationships between variables</title>

		<g:javascript>

			$( document).ready( function() {

				var itemsToToggle = $( '.list-of-parents, .list-of-children, .icon-key-details' );
				var variableCells = $( '.variable-cell' );
				var showToggleDetails = itemsToToggle.length > 0;
				var btn = $( '#btnToggleDetails');
				if ( !showToggleDetails )
				{
					btn.hide();
				}
				else
				{
					var msgShow = '<g:message code="general.show" />';
					var msgHide = '<g:message code="general.hide" />';

					btn.click( function() {

						var label = $.trim( this.innerHTML );
						if ( label.substring( 0, 4 ) == msgHide )
						{
							label = label.replace( msgHide, msgShow );
							itemsToToggle.hide( 'fast' );
							variableCells.removeClass( 'restricted-width' );
						}
						else
						{
							label = label.replace( msgShow, msgHide );
							itemsToToggle.show( 'fast' );
							variableCells.addClass( 'restricted-width' );
						}
						this.innerHTML = label;
					});

					itemsToToggle.hide();
				}

			});

		</g:javascript>

		<r:require module="elicitList" />

	</head>
	
	<body>

		<g:if test="${completed}">
			<br />
			<div class="message"><g:message code="elicit.list.info.round-complete" /></div>
		</g:if>

		<g:if test="${hasDetails}">
			<button type="button" style="margin-top: 0.3em;" id="btnToggleDetails">
			 	<g:message code="general.show" />
				<g:message code="elicit.list.details" />
			</button>
		</g:if>

		<bnIcons:key>

			<bnIcons:icon
				label="${message( code: "icon-key.needs-review.label")}"
				iconPath="${resource([ dir: "images/icons/", file: "lightbulb.png" ])}"><g:message code="icon-key.needs-review" /></bnIcons:icon>

			<bnIcons:icon
				label="${message( code: "icon-key.doesnt-need-review.label")}"
				iconPath="${resource([ dir: "images/icons/", file: "accept.png" ])}"
				display="${hasReviewedSome}"><g:message code="icon-key.doesnt-need-review" /></bnIcons:icon>

			<bnIcons:icon
				label="${message( code: "icon-key.relationship.label")}"
				iconPath="${resource([ dir: "images/icons/", file: "arrow_right.png" ])}"
				classes="icon-key-details"
				display="hasDetails"><g:message code="icon-key.relationship" /></bnIcons:icon>

			<bnIcons:icon
				label="${message( code: "icon-key.relationship-with-comment.label")}"
				iconPath="${resource([ dir: "images/icons-custom/", file: "arrow_right_comment.png" ])}"
				classes="icon-key-details"
				display="hasDetails"><g:message code="icon-key.relationship-with-comment" /></bnIcons:icon>

		</bnIcons:key>

		<g:if test="${!hasPreviousPhase}">

			<bn:listSummaryFirstPhase variables="${variables}" stillToVisit="${stillToVisit}"/>

		</g:if>
		<g:else>

			<bn:listSummary variables="${variables}" stillToVisit="${stillToVisit}"/>

		</g:else>

		<g:if test="${completed}">
			<div class="message">
				<ul>
					<li>
						<g:message code="elicit.list.info.round-complete" />
					</li>
				</ul>
			</div>
		</g:if>
		<g:elseif test="${stillToVisit.size() > 0}">
			<div class="message">
				<ul>
					<li>
						<g:message code="elicit.list.info.round-incomplete" />
					</li>
				</ul>
			</div>
		</g:elseif>
		<g:else>
			<button
				id="btnCompleteRound"
				type="button"
				onclick="document.location = '${createLink( action: 'completed' )}'"
				class='big '
				${stillToVisit.size() > 0 ? 'disabled="disabled"' : ''}>
				<g:message code="elicit.list.finish-round" />
			</button>
		</g:else>

		<g:if test="${keptRedunantRelationships > 0}">
			<button
				type="button"
				style="margin-top: 0.3em;"
				id="btnShowProblems"
				onclick="document.location = '${createLink( action: 'problems', params: [ displayAll: true ] )}'">

				<g:message code="general.show" />
				<g:message code="elicit.list.potential-problems" args="${[ keptRedunantRelationships ]}" />

			</button>
		</g:if>

	</body>
	
</html>
