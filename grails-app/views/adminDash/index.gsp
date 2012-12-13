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
<html xmlns="http://www.w3.org/1999/html">

	<head>
		<meta name="layout" content="main">
		<title>Admin Dashboard</title>
		<style type="text/css">
			.column-left {
				width: 350px;
			}

			.variable-item {
				padding-left: 0.5em;
			}

			.overview .info {
				margin-top: 0.5em;
			}

			.overview .info .label {
				font-weight: bold;
			}
		</style>
	</head>
	
	<body>
	
		<h1>Admin Dashboard</h1>

		<g:if test="${flash.messages?.size() > 0}">
			<div class="message">
				<ul>
					<g:each in="${flash.messages}" var="message">
						<li>${message}</li>
					</g:each>
				</ul>
			</div>
		</g:if>

		<div class="column-wrapper">

			<div class="column-header">

				<fieldset class="default ">

					<legend>Study overview</legend>

					<input type="button" value="Advance to next delphi phase" onclick="document.location = '${createLink( action: 'confirmAdvanceDelphiPhase' )}'" />
					<input type="button" value="Edit details" onclick="document.location = '${createLink( action: 'editStudyDetails' )}'" />

					<div class="overview">
						<div class="info">
							<span class="label">Name:</span> ${appProperties.title}
						</div>

						<div class="info">
							<span class="label">Delphi Phase:</span> ${appProperties.delphiPhase}
						</div>
					</div>

				</fieldset>

			</div>


			<div class="column-left">

				<fieldset class="default ">

					<legend>Participants</legend>

					<input type="button" class="" value="Manage Users" onclick="document.location = '${createLink( controller: 'user', action: 'list' )}'" />

					<div class="overview">
						<div class="info">
							<span class="label">Total users:</span> ${userList.size()}
						</div>

						<div class="info">
							<span class="label">Completed this round:</span> ${completedCurrentRound.size()}
						</div>
					</div>

					<ul class="variable-list ">
						<g:each in="${userList}" var="${user}">
							<li class=" variable-item">
								${user.username}
								<g:if test="${completedCurrentRound*.completedBy.contains( user )}">
									<span class="stats">Completed phase ${appProperties.delphiPhase}</span>
								</g:if>
							</li>
						</g:each>
					</ul>

				</fieldset>

			</div>


			<div class="column-right">

			</div>
		</div>
	
	</body>
	
</html>
