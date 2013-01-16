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

					var itemDescriptions = $( '.item-description:not( .agreement-summary )' );
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

							if ( this.value.substring( 0, 4 ) == msgHide )
							{
								this.value = this.value.replace( msgHide, msgShow );
								itemDescriptions.hide( 'fast' );
								<bn:setPreference key="show-description" value="false" />
							}
							else
							{
								this.value = this.value.replace( msgShow, msgHide );
								itemDescriptions.show( 'fast' );
								<bn:setPreference key="show-description" value="true" />
							}

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
			<div class="message">Thank you for completing this round. You are free to modify your answers until the round finishes, at which point you will be notified via email.</div>
		</g:if>
		<g:elseif test="${stillToVisit.size() > 0}">
			<div class="message"><ul><li>Please review all variables before completing the round.</li></ul></div>
		</g:elseif>
		<g:else>
			<div class="message">Once you've finished the survey, you will be free to modify your answers until the round finishes, at which point you will be notified via email.</div>
			<input type="button" onclick="document.location = '${createLink( action: 'completed' )}'" value="Finish survey" class='big ' ${stillToVisit.size() > 0 ? 'disabled="disabled"' : ''} />
		</g:else>

	</body>
	
</html>
