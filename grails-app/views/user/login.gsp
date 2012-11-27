
<%@ page import="bn.elicitator.Variable" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Login</title>
	</head>
	
	<body>
	
		<h1>Login</h1>
		
		
		
		<auth:form authAction="login" success="[controller: '', action: '']" error="[action:'loginError']">
		
			<label for="inputLogin" class="login-form-label">User:</label>
			<input id="inputLogin" type="text" name="login" />
			<br />
			<br />
			<label for="inputPassword" class="login-form-label">Password:</label>
			<input id="inputPassword" type="password" name="password" />
			<br />
			<br />
			<!--  Here to align the button with the inputs... -->
			<label for="inputPassword" class="login-form-label"> </label>
			<input type="submit" value="Log in" />
		
		</auth:form>
		
	</body>
	
</html>
