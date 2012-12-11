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
