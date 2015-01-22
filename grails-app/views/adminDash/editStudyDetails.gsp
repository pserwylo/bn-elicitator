%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 2015 Peter Serwylo (peter.serwylo@monash.edu)
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
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Admin Dashboard - Survey Details</title>

		<r:require module="adminSurveyDetails" />

	</head>
	
	<body>

		<fieldset class="default">
			<legend>Survey details</legend>
			
			<g:form action="saveStudyDetails">
					
				<div class="field">
					<label>
						Survey title:
						<g:textField name="title" value="${appProperties.title}" />
					</label>
				</div>
	
				<div class="field">
					<label>
						Admin Email:
						<g:textField name="adminEmail" value="${appProperties.adminEmail}" />
					</label>
				</div>
	
				<div class="field">
					<span class="label">Prizes:</span>
					<g:if test="${appProperties.arePrizesEnabled()}">
						Enabled (delete content from <bnContent:editLink label="${bn.elicitator.ContentPage.PRIZE}">here</bnContent:editLink> to disable prizes).
					</g:if>
					<g:else>
						Disabled (add description <bnContent:editLink label="${bn.elicitator.ContentPage.PRIZE}">here</bnContent:editLink> to enable prizes).
					</g:else>
				</div>
				
				<div class="button-container">
					<g:submitButton name="Save" />
					<input type="button" value="Back" onclick="document.location = '${createLink( controller: 'adminDash' )}'" />
				</div>
	
			</g:form>
			
		</fieldset>

	</body>
	
</html>
