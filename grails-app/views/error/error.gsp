<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>An error occured</title>
	<meta name="layout" content="main">
</head>
<body>

	<h1>${error.title}</h1>

	<div class="errors">${error.message}</div>

	<g:if test="${error.exception}">
		<div class="exception" style="display: none;">
			<g:renderException exception="${error.exception}" />
		</div>
	</g:if>

</body>
</html>