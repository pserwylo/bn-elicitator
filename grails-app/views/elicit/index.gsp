
<%@ page import="bn.elicitator.Variable" %>
<!doctype html>

<g:set var="hasDetails" value="${delphiPhase > 1 || variables.size() > stillToVisit?.size()}" />

<html>

	<head>
		<meta name="layout" content="main">
		<title>Identify relationships between variables</title>

		<g:if test="${hasDetails}">
			<g:preferencesJs />
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
						btn.click( function() {

							if ( this.value.substring( 0, 4 ) == 'Hide' )
							{
								this.value = this.value.replace( 'Hide', 'Show' );
								itemDescriptions.hide( 'fast' );
								<g:setPreference key="show-description" value="false" />
							}
							else
							{
								this.value = this.value.replace( 'Show', 'Hide' );
								itemDescriptions.show( 'fast' );
								<g:setPreference key="show-description" value="true" />
							}

						});

						if ( '<g:preferenceValue key="show-description" />' == 'true' )
						{
							btn.trigger( 'click' );
						}
						else
						{
							itemDescriptions.hide();
						}
					}

				});

			</g:javascript>
		</g:if>

		<r:require module="elicit" />

	</head>
	
	<body>

		<g:if test="${hasDetails}">
			<input type="button" style="margin-top: 0.3em;" id="btnToggleDetails" value="Show details" />
		</g:if>

		<g:if test="${keptRedunantRelationships > 0}">
			<input
				type="button"
				style="margin-top: 0.3em;"
				id="btnShowProblems"
				value="Show ${keptRedunantRelationships} potential problems"
				onclick="document.location = '${createLink( action: 'problems', params: [ displayAll: true ] )}'" />
		</g:if>

		<br />
		<br />

		<g:if test="${!hasPreviousPhase}">

			<g:listSummaryFirstPhase variables="${variables}" stillToVisit="${stillToVisit}"/>

		</g:if>
		<g:else>

			%{--
			<h2>Do you agree with the other participants?</h2>
			<button id="btnToggleDetails" onclick="toggleDetails()">Show <span style="color: green;">Agreements</span> and <span style="color: red;">Disagreements</span></button>
			<br /><br />
			--}%

			<g:listSummary variables="${variables}" showAgree="${false}" stillToVisit="${stillToVisit}"/>

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
