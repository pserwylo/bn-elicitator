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
				You can still continue, and they will have the opportunity to participate in the next phase.<br />
				However, they will nto get to pass on their contributions for this phase any more.
			</div>
		</g:if>

		<div class="message">
			<ul>
				<li>All participants will be emailed</li>
				<li>The current phase will be advanced from ${appProperties.delphiPhase} to ${appProperties.delphiPhase + 1}</li>
			</ul>
		</div>

		<div class="button-container">
			<input type="button" value="Oops, no thanks!" class="big" onclick="document.location = '${createLink( action: 'index' )}'"/>
			<input type="button" value="Yes please, I know what I'm doing" class="confirm-advance" onclick="document.location = '${createLink( action: 'yesAdvanceDelphiPhase' )}'" />
		</div>

	</body>
	
</html>
