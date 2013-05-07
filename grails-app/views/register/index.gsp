<html>

	<head>
		<meta name='layout' content='main'/>
		<title><g:message code='spring.security.ui.register.title'/></title>
		<r:require module="auth" />
	</head>

	<body>

		<g:hasErrors bean="${command}">
			<div class="errors">
				<g:renderErrors bean="${command}" as="list" />
			</div>
		</g:hasErrors>

		<fieldset class="default">
			<legend>Username/password</legend>
			<g:form action='register' name='registerForm' method="post">

				<g:if test='${emailSent}'>
					<div class="message">
						<g:message code='spring.security.ui.register.sent'/>
					</div>
				</g:if>
				<g:else>

					<p class="form-element">
						<label for='email'>Email:</label>
						<input type='email' class='text_' name='email' id='email' value="${command.email ?: ''}"/>
					</p>

					<p class="form-element">
						<label for='password'>Password:</label>
						<input type='password' class='text_' name='password' id='password' />
					</p>

					<p class="form-element">
						<label for='password2'>Confirm password:</label>
						<input type='password' class='text_' name='password2' id='password2' />
					</p>

					<p id="login_button_holder" class="form-element">
						<input type='submit' id="submit" value='Register for survey'/>
					</p>

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
