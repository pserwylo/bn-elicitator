<html>

<head>
<title><g:message code='spring.security.ui.forgotPassword.title'/></title>
<meta name='layout' content='main'/>
</head>

<body>

	<g:if test="${flash.error}">
		<div class="errors">
			${flash.error}
		</div>
	</g:if>

	<fieldset class="default">
		<legend>Forgot Password</legend>

		<g:form action='forgotPassword' name="forgotPasswordForm" autocomplete='off'>

			<g:if test='${emailSent}'>
				<g:message code='spring.security.ui.forgotPassword.sent'/>
			</g:if>

			<g:else>
				<p><g:message code='spring.security.ui.forgotPassword.description'/></p>
				<table>
					<tr>
						<td>
							<label for="username">
								<g:message code='spring.security.ui.forgotPassword.username'/>
							</label>
						</td>
						<td>
							<g:textField name="username" size="25" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<button type="submit"><g:message code="spring.security.ui.forgotPassword.submit"/></button>
						</td>
					</tr>
				</table>



			</g:else>

		</g:form>

	</fieldset>

<script>
$(document).ready(function() {
	$('#username').focus();
});
</script>

</body>
</html>
