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
