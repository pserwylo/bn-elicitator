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
		<title>Step 1</title>
		
		<g:javascript>
		
			function toggleAll()
			{
				var inputs = $( '.checkbox-class' );
				var checkedInputs = $( '.checkbox-class:checked' );
				var checkAll = ( checkedInputs.length != inputs.length );
				inputs.prop( 'checked', checkAll );
				
				$( '#checkbox-toggle-all' ).prop( 'checked', checkAll );
			}
		
		</g:javascript>
		
	</head>
	
	<body>
	
		<h1>Select Variables</h1>
	
		<g:form method="post" action="saveSelectedVariables">
			
			<input type="checkbox" id="checkbox-toggle-all" onclick="toggleAll()" /> Select all/none
			
			<ul class="variable-list">
				<g:each var="ontoClass" in="${classes}">
					<li>
						<input type="checkbox" class="checkbox-class" name="${ontoClass?.label}" /> <span class="class-list-label">${ontoClass?.label}</span>
					</li>
				</g:each>
			</ul>
			
			<g:actionSubmit controller="config" action="saveSelectedVariables" value="Save and Continue" />
			
		</g:form>
		
	</body>
	
</html>
