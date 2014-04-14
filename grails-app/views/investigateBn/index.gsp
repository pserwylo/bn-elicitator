<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta name="layout" content="main">
	<title>Investigate BN</title>
</head>

<body>
	<h1>Investigate BN</h1>

	<fieldset class="default">
		<legend>BN Arcs</legend>
		<p>
			This section allows you to look at the various relationships in the BN, such as:
		</p>
		<ul>
			<li>Who asked for each relationship to be include</li>
			<li>Comments they gave for their reasoning</li>
		</ul>
		<input type="button" value="Investigate" onclick="document.location = '${createLink( action : 'arcs' )}'" />
	</fieldset>

	<fieldset class="default">
		<legend>Unused variables</legend>
		<p>
			This section shows the variables which did not end up in the final model.
		</p>
		<input type="button" value="Investigate" onclick="document.location = '${createLink( action : 'arcs' )}'" />
	</fieldset>
</body>
</html>