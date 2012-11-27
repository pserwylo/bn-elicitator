
<%@ page import="bn.elicitator.Variable" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Consent form</title>
	</head>
	
	<body>
	
		<h1>Consent form</h1>
		<div class="warning">
			Note: A record of this consent will be kept by the researcher for their records.
		</div>
	
		
	
		<br />
		<input type="button" value="I agree" class="" onclick="document.location = '${createLink( action: 'consentAgree' )}'"/>
		
	</body>
	
</html>
