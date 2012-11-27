<%@ page import="bn.elicitator.AppProperties; grails.util.Environment" %>
<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

		<title><g:layoutTitle default="Survey"/></title>

		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">

		<g:javascript library="global"/>
        <r:layoutResources />
		<g:layoutHead/>

	</head>

	<body>

		<g:if test="${Environment.getCurrent().equals( Environment.DEVELOPMENT )}">
			<div id="app-info">
				<ul class="">
					<li>App Info - </li>
					<li>Delphi phase: ${AppProperties.properties.delphiPhase}</li>
					<li>Elicitation phase: ${AppProperties.properties.elicitationPhase}</li>
				</ul>
			</div>
		</g:if>

		<div id="heading">${AppProperties.properties.title}</div>

		<shiro:authenticated>
			<div id="user-info">Welcome <shiro:principal />! (<g:link controller="auth" action="logout">logout</g:link>)</div>
		</shiro:authenticated>

		<g:layoutBody/>

		<div class="footer" role="contentinfo"></div>

		<r:layoutResources />
	</body>
</html>