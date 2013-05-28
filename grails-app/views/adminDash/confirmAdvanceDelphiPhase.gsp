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
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Admin Dashboard</title>

		<r:require module="admin" />

	</head>
	
	<body>

		<h1>Advance to the next phase?</h1>

		<g:if test="${yetToComplete.size() > 0}">
			<div class="errors">
				The following users have not completed the current round:
				<ul>
					<g:each in="${yetToComplete}" var="user">
						<li>${user.realName}</li>
					</g:each>
				</ul>
				You can still continue, and those who have at least answered some questions will have the opportunity to participate in the next phase.<br />
				However, they will nto get to pass on their contributions for this phase any more, and the ones who have not done anything will not be able to continue at all.
			</div>
		</g:if>

		<div class="message">
			<ul>
				<li>The current phase will be advanced from ${appProperties.delphiPhase} to ${appProperties.delphiPhase + 1}</li>
			</ul>
		</div>

		<div class="button-container">
			<input type="button" value="Oops, no thanks!" class="big" onclick="document.location = '${createLink( action: 'index' )}'"/>
			<input type="button" value="Yes please, I know what I'm doing" class="confirm-advance" onclick="document.location = '${createLink( action: 'yesAdvanceDelphiPhase' )}'" />
		</div>

	</body>
	
</html>
