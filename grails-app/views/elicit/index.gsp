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
<%@ page import="bn.elicitator.Variable" %>
<!doctype html>

<g:set var="hasDetails" value="${delphiPhase > 1 || variables.size() > stillToVisit?.size()}" />

<html>

	<head>
		<meta name="layout" content="main">
		<title>Identify relationships between variables</title>

		<g:if test="${hasDetails}">
			<bn:preferencesJs />
			<g:javascript>

				$( document).ready( function() {

					var itemDescriptions = $( '.list-of-parents, .list-of-children' );
					var variableCells = $( '.variable-cell' );
					var showToggleDetails = itemDescriptions.length > 0;
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

							var label = this.innerHTML.trim();
							if ( label.substring( 0, 4 ) == msgHide )
							{
								label = label.replace( msgHide, msgShow );
								itemDescriptions.hide( 'fast' );
								variableCells.removeClass( 'restricted-width' );
								<bn:setPreference key="show-description" value="false" />
							}
							else
							{
								label = label.replace( msgShow, msgHide );
								itemDescriptions.show( 'fast' );
								variableCells.addClass( 'restricted-width' );
								<bn:setPreference key="show-description" value="true" />
							}
							this.innerHTML = label;
						});

						if ( '<bn:preferenceValue key="show-description" />' == 'true' ) {
							btn.trigger( 'click' );
						} else {
							itemDescriptions.hide();
						}
					}

				});

			</g:javascript>
		</g:if>

		<r:require module="elicitList" />

	</head>
	
	<body>

		<h:help title="Welcome!" index="1">
			Thank you for participating in the survey. We'll quickly run you through some concepts to get you started.
		</h:help>

		<h:help for="btnToggleDetails" title="Help title" location="right" index="2">
			Here is some help text!
		</h:help>

		<h:help for="all-children-list" title="List of variables" location="right" index="3" width="200px">
			You need to go through these variables, one at a time, and do cool stuff in response to their presence.
		</h:help>

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

		<br />
		<br />

		<g:if test="${!hasPreviousPhase}">

			<bn:listSummaryFirstPhase variables="${variables}" stillToVisit="${stillToVisit}"/>

		</g:if>
		<g:else>

			%{--
			<h2>Do you agree with the other participants?</h2>
			<button id="btnToggleDetails" onclick="toggleDetails()">Show <span style="color: green;">Agreements</span> and <span style="color: red;">Disagreements</span></button>
			<br /><br />
			--}%

			<bn:listSummary variables="${variables}" showAgree="${false}" stillToVisit="${stillToVisit}"/>

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
			<div class="message">
				<ul>
					<li>
						<g:message code="elicit.list.info.round-ready-to-complete" />
					</li>
				</ul>
			</div>
			<button
				type="button"
				onclick="document.location = '${createLink( action: 'completed' )}'"
				class='big '
				${stillToVisit.size() > 0 ? 'disabled="disabled"' : ''}>
				<g:message code="elicit.list.finish-round" />
			</button>
		</g:else>

	</body>
	
</html>
