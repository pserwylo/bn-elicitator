<!doctype html>
<html>
<head>
  <title>An error occured</title>
	<meta name="layout" content="main">
</head>
<body>
	<h1>${error.title}</h1>

	<ul>
		<li class="errors">
			<p>${error.message}</p>
			<p><g:link controller="home">Click here</g:link> to return to the main page.</p>
		</li>
	</ul>

	<g:if test="${error.exception}">
		<div class="exception" style="display: none;">
			<g:renderException exception="${error.exception}" />
		</div>
	</g:if>

</body>
</html>