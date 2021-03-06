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
<%@ page import="bn.elicitator.ContentPage; bn.elicitator.AppProperties; grails.util.Environment" %>
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

		<script type="text/javascript">
			if ( typeof bn === "undefined" ) {
				bn = {};
			}

			bn.config = {
				webroot : '<g:resource absolute="true" dir="/" />'
			};

			bn.log = function( message ) {
				if ( typeof console !== "undefined" ) {
					console.log( message );
				}
			};

			window.onerror = function( msg, url, line ) {

				var description = msg + " - " + url + " @ line " + line;

				if ( typeof console !== "undefined" ) {
					console.log( "Uncaught exception: " + description + "\nLogging on the server, then redirecting back here." );
				}

				alert( "Uh oh... an unexpected error occurred.\n\nWe'll refresh this page, and if it still persists, please contact peter.serwylo@monash.edu." );

				$.post(
					"${createLink( controller: 'error', action: 'jsError' )}",
					{
						message : msg,
						url     : url,
						line    : line
					}
				);
			};
		</script>

		<g:javascript library="global"/>
        <r:layoutResources />
		<g:layoutHead/>

	</head>

	<body>

		%{--<g:if test="${Environment.getCurrent().equals( Environment.DEVELOPMENT )}">
			<div id="app-info">
				<ul class="">
					<li>App Info - </li>
					<li>Delphi phase: ${AppProperties.properties.delphiPhase}</li>
					<li>Elicitation phase: ${AppProperties.properties.elicitationPhase}</li>
				</ul>
			</div>
		</g:if>--}%

		<g:if test="${controllerName == 'elicit' || controllerName == 'das2004'}">
			<help:help index="-10" uniqueId="global-help" targetId="action-help" title="Need help?">
				<p>At any point, you can check out the <bnContent:link page="help">help page</bnContent:link> for detailed help.
				If you still have questions, feel free to contact <a href="mailto:peter.serwylo@monash.edu">Peter Serwylo</a>.</p>
			</help:help>
		</g:if>

		<div id="headingWrapper">
			<sec:ifLoggedIn>
				<div id="user-info">
					<div id="welcome-user">
					<g:message code="main.welcome" args="${[ bnUser.realName() ]}" />
					</div>
					<bnContent:link
						id="action-help"
						page="${ContentPage.HELP}"
						class="help">
						Help
					</bnContent:link>
					<bnContent:link
							page="${ContentPage.PRIVACY_POLICY}"
							class="privacy">
						Privacy
					</bnContent:link>
					<sec:ifSwitched>
						<a class="switch" href='${request.contextPath}/j_spring_security_exit_user'>
							Switch back
						</a>
					</sec:ifSwitched>
					<g:link controller="logout" class="logout">
						Logout
					</g:link>
				</div>
			</sec:ifLoggedIn>

			<div id="heading">
				<g:link controller="home">
					${AppProperties.properties.title}
				</g:link>
			</div>

		</div>

		<g:layoutBody/>

		<div class="footer" role="contentinfo">
		</div>

		<r:layoutResources />
	</body>
</html>