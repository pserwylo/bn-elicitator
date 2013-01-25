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
				
				$( '.class-list-label' ).each( function( i, item ) {
					
					toggleIndividual( $( item ).text(), false );
				
				});
			}
			
			function filter( filterText )
			{
				$( '.variable-list > li' ).each( function( i, item ) {
				
					var isChecked = $( item ).children( 'input' ).prop( 'checked' );
					var label = $( item ).children( '.class-list-label' ).text() 
					var matchesFilter = label.toLowerCase().indexOf( filterText.toLowerCase() ) >= 0;
					
					if ( matchesFilter || isChecked )
					{
						$( item ).show();
					}
					else
					{
						$( item ).hide();
					}
				});
			}
			
			function toggleIndividual( label, animate )
			{
				if ( $( 'input[value=' + label + ']').prop( 'checked' ) )
				{
					selectLabel( label, animate );
				}
				else
				{
					deselectLabel( label, animate );
				}
			}
			
			/**
			 * Adds the selected variable to the list on the right (if not already
			 * there for some other reason).
			 */
			function selectLabel( label, animate )
			{
				var found = $( '.selected-variable-list > #li-selected-' + label );
				if ( found.length == 0 )
				{
					$( '.selected-variable-list' ).append( 
						'<li id="li-selected-' + label + '" class="">' +
						' ' + label +
						'</li>'
					);
					
					if ( animate )
					{
						$( '#li-selected-' + label ).hide().show( 'fast' );
					}
				}
			}
			
			/**
			 * Removes the selected variable from the list on the right (if it has 
			 * not already been removed for some other reason).
			 */
			function deselectLabel( label, animate )
			{
				var found = $( '.selected-variable-list > #li-selected-' + label );
				if ( found.length > 0 )
				{
					if ( animate )
					{
						found.hide( 'fast', function() { found.remove() } );
					}
					else
					{
						found.remove();
					}
				}
			}
			
			$( document ).ready( function() {
				
				$( '.variable-list > li' ).mouseover( function() {
				
					$( this ).removeClass( '' ).addClass( 'ui-state-hover' );
				
				})
				.mouseout( function() {
					
					$( this ).removeClass( 'ui-state-hover' ).addClass( '' );
				
				})
				.click( function( event ) {
				
					var input = $( this ).find( 'input' );
					
					// Toggle the associated checkbox only if we didn't click a checkbox
					if ( event.target != input[0] )
					{
						input.prop( 'checked', !input.prop( 'checked' ) )
						toggleIndividual( $( this ).find( '.class-list-label' ).text(), true );
					}	
				
				});
				
			});
		
		</g:javascript>
		
	</head>
	
	<body>
	
		<h1>Select Variables</h1>
	
		<g:form method="post">
			
			<g:actionSubmit action="saveSelectedVariables" value="Save and Continue" class="" />
			
			<br />
			
			<div class="column-wrapper">
			
				<div class="column-left">
			
					<fieldset class="default ">
				
						<legend>Available Variables</legend>
				
						<input type="text" id="input-filter" placeholder="Filter variables..." onkeyup="filter( $(this).val() )" />
						
						<br />
						<br />
						
						<input type="checkbox" id="checkbox-toggle-all" onclick="toggleAll()" /> Select all/none
				
						<br />
						<br />
				
						<ul class="variable-list variable-list-filter ">
							<g:each var="variable" in="${allVariables}">
								<%boolean saved = ( savedVariables.find { v -> v.label == variable.label } != null )%>
								<li class=" ${saved ? "variable-saved" : "" }">
									<input 
										${saved ? "checked='checked'" : ""} 
										type="checkbox" 
										class="checkbox-class" 
										name="variables" 
										value="${variable?.label}"
										onchange="toggleIndividual( '${variable?.label}', true )" /> 
									<span class="class-list-label">${variable?.label}</span>
								</li>
							</g:each>
						</ul>
						
					</fieldset>
						
				</div>
				
				<div class="column-right">
					
					<fieldset class="default ">
						<legend>Selected</legend>
						<ul class="selected-variable-list ">
							<g:each var="variable" in="${savedVariables}">
								<li id="li-selected-${variable.label}" class="">
									${variable.label}
								</li>
							</g:each>
						</ul>
					</fieldset>
					
				</div>
				
			</div>
			
			<g:actionSubmit action="saveSelectedVariables" value="Save and Continue" class="" />
			
		</g:form>
		
	</body>
	
</html>
