
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
