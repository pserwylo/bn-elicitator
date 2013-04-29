<html>
	<head>
		<meta name='layout' content='main'/>
		<title><g:message code="springSecurity.login.title"/></title>
		<r:require module="auth" />
	</head>

	<body>
		<div id='login'>
			<div class='inner'>

				<g:if test='${flash.message}'>
					<div class='message'>${flash.message}</div>
				</g:if>

				<div class="column-wrapper">
					<div class="column-header">
						<h2><g:message code="springSecurity.login.header"/></h2>
					</div>
					<div class="column-left">
						<fieldset class="default">
							<legend>Social networks</legend>
							<oauth:connect provider="facebook" id="facebook-connect-link">Facebook</oauth:connect>
							<oauth:connect provider="twitter" id="twitter-connect-link">Twitter</oauth:connect>
							<oauth:connect provider="google" id="google-connect-link">Google</oauth:connect>
						</fieldset>
					</div>
					<div class="column-right">
						<fieldset class="default">
							<legend>Username/password</legend>

							<form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
								<p class="form-element">
									<label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
									<input type='text' class='text_' name='j_username' id='username'/>
								</p>

								<p class="form-element">
									<label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
									<input type='password' class='text_' name='j_password' id='password'/>
								</p>

								<p id="remember_me_holder" class="form-element">
									<input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
									<label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
								</p>

								<p id="login_button_holder" class="form-element">
									<input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
								</p>
							</form>
						</fieldset>
					</div>
				</div>
			</div>
		</div>
		<script type='text/javascript'>
			<!--
			(function() {
				document.forms['loginForm'].elements['j_username'].focus();
			})();
			// -->
		</script>
	</body>
</html>
