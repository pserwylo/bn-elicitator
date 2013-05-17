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
							<legend>Register/login via Facebook</legend>
							<oauth:connect provider="facebook" id="facebook-connect-link">
								<div class="facebook-connect">
									<span style="display: none">Connect with facebook</span>
								</div>
							</oauth:connect>
							<p class="description">
								To see why we ask Facebook for
								"your public profile, friend list and email address", read our brief
								<bnContent:link page="${bn.elicitator.ContentPage.PRIVACY_POLICY}">
									privacy policy
								</bnContent:link>.
							</p>
						</fieldset>
					</div>
					<div class="column-right">
						<fieldset class="default">
							<legend>Register/login with password</legend>

							<form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
								<p class="form-element">
									<label for='username'>Email:</label>
									<input type='text' class='text_' name='j_username' id='username'/>
								</p>

								<p class="form-element">
									<label for='password'>Password:</label>
									<input type='password' class='text_' name='j_password' id='password'/>
								</p>

								<p id="remember_me_holder" class="form-element">
									<input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
									<label for='remember_me'>Remember me</label>
								</p>

								<p id="login_button_holder" class="form-element">
									<input type='submit' value='Login'/>
									<input id="btnRegister" type='button' value='Register'/>
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

			$(document).ready( function() {
				$('#btnRegister').click( function() {
					document.location = '${createLink( controller: "register" )}?email=' + $( '#username' ).val();
				});
			});
			// -->
		</script>
	</body>
</html>
