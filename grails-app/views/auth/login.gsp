<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title>Login</title>
</head>
<body>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:form action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}" />
    <table class="login-form">
      <tbody>
        <tr>
          <th><label for="username">Username:</label></th>
          <td><input id="username" type="text" name="username" value="${username}" /></td>
        </tr>
        <tr>
          <th><label for="password">Password:</label></th>
          <td>
			  <input id="password" type="password" name="password" value="" />
			  <div class="password-help"></div>
		  </td>
        </tr>
        <tr>
          <th>Remember me?:</th>
          <td><g:checkBox name="rememberMe" value="${rememberMe}" /></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="submit" value="Sign in" /></td>
        </tr>
      </tbody>
    </table>
  </g:form>
</body>
</html>
