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
<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
	<head>
		<meta name="layout" content="main">
		<title>Explanatory Statement</title>
		<r:require module="explain" />
	</head>

	<body>

		<div id="explanatory-statement">

			<bn:top />
			<h1>Explanatory Statement</h1>

			<g:if test="${flash.mustCheckRead}">
				<div class="errors">
					Must check the 'I have read and understood this explanatory statement' checkbox before continuing.
				</div>
			</g:if>

			<p class="message">
				Please read this Explanatory Statement in full before making a decision about participating.
				<br />
				Once read, click the "Continue" button at the bottom of the page.
			</p>

			<g:javascript>
				(function() {
					$( 'input[name=readStatement]').change( function() {
						$( "#continue").prop( 'disabled', !$( "input[name=readStatement]" ).prop( 'checked' ) );
					});
				})();
			</g:javascript>

			<ul id="toc" class="">
				<g:javascript>
					(function(){
						$( 'h2').each( function( i, item ) {
							var safeString = $( item ).text().replace( /[^a-zA-Z0-9]+/g, '' );
							$( item ).before( '<a name="' + safeString + '"></a>' );
							$( item ).after( "<span class='back-to-top'><a href='#top'>(back to top)</a></span>" );
							$( '#toc' ).append( '<li><a href="#' + safeString + '">' + $( item).text() + '</a></li>' );
						});
					})();
				</g:javascript>
			</ul>

			${explanatoryStatement}

			<g:form action="consent">
				<input id="continue" disabled="disabled" type="submit" class="big" value="Continue" onclick="document.location = '${createLink( controller: 'explain', action: 'consent' )}'" />
				<label>
					<input type="checkbox" name="readStatement" value="1"/>
					I have read and understood this explanatory statement.
				</label>
			</g:form>

		</div>

	</body>
</html>