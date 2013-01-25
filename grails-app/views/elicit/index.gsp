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

		<g:if test="${completed}">
			<br />
			<div class="message"><g:message code="elicit.list.info.round-complete" /></div>
		</g:if>

		<g:if test="${hasDetails}">
			<button type="button" style="margin-top: 0.3em;" id="btnToggleDetails">
			 	<g:message code="general.show" />
				<g:message code="elicit.list.details" />
			</button>
			<h:help title="${g.message( code: "help.elicit.list.show-details.title" )}" forId="btnToggleDetails" index="10000" location="right">
				<g:message code="help.elicit.list.show-details" />
			</h:help>
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
			<h:help title="${g.message( code: "help.elicit.list.show-redundant.title" )}" forId="btnShowProblems" index="10001" location="right">
				<g:message code="help.elicit.list.show-redundant" />
			</h:help>
		</g:if>

		<br />
		<br />

		<g:if test="${!hasPreviousPhase}">

			<bn:listSummaryFirstPhase variables="${variables}" stillToVisit="${stillToVisit}"/>

			<h:help title="${g.message( code: "help.elicit.list.welcome.title" )}" index="1">
				<g:message code="help.elicit.list.welcome" />
			</h:help>

		</g:if>
		<g:else>

			%{--
			<h2>Do you agree with the other participants?</h2>
			<button id="btnToggleDetails" onclick="toggleDetails()">Show <span style="color: green;">Agreements</span> and <span style="color: red;">Disagreements</span></button>
			<br /><br />
			--}%

			<bn:listSummary variables="${variables}" stillToVisit="${stillToVisit}"/>

			<h:help title="${g.message( code: "help.elicit.list.round2.title" )}" index="100">
				<g:message code="help.elicit.list.round2" />
			</h:help>

			<h:help title="${g.message( code: "help.elicit.list.disagreement.title" )}" forId="disagree-label-0" location="right" index="101">
				<g:message code="help.elicit.list.disagreement" />
			</h:help>

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
			<h:help title="${g.message( code: "help.elicit.list.complete.title" )}" forId="btnCompleteRound" location="right">
				<g:message code="help.elicit.list.complete" />
			</h:help>
		</g:else>

	</body>
	
</html>
