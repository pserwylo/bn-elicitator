<html>

<head>
	<title><g:message code='spring.security.ui.resetPassword.title'/></title>
	<meta name='layout' content='main'/>
</head>

<body>

	<fieldset class="default">

		<legend><g:message code='spring.security.ui.resetPassword.description'/></legend>

		<g:form action='resetPassword' name='resetPasswordForm' autocomplete='off'>
			<g:hiddenField name='t' value='${token}'/>

			<table>
				<s2ui:passwordFieldRow name='password' labelCode='resetPasswordCommand.password.label' bean="${command}"
									 labelCodeDefault='Password' value="${command?.password}"/>

				<s2ui:passwordFieldRow name='password2' labelCode='resetPasswordCommand.password2.label' bean="${command}"
									 labelCodeDefault='Password (again)' value="${command?.password2}"/>

				<tr>
					<td></td>
					<td>
						<button id='reset'>
							<g:message code='spring.security.ui.resetPassword.submit' />
						</button>
					</td>
				</tr>
			</table>

		</g:form>

	</fieldset>

	<script>
	$(document).ready(function() {
		$('#password').focus();
	});
	</script>

</body>
</html>
