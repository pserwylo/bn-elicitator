
<%@ page import="bn.elicitator.Variable" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Setup variable constraints</title>
		
		<g:javascript>
		
		var selectedLi = null;
		
		$( function() {
			
			$( '.variable-list' ).sortable( {
				handle: '.handle'
			});
			
		});
		
		/**
		 * Iterate over each 'li' in the list of variables, and parse it to
		 * find out if it is a group or not, or what the label is, etc.
		 * Then post the data in a JSON-ified string.
		 */
		function save() {
		
			var toSave = [];
			$( '.variable-list li' ).each( function( i, item ) {
			
				if ( $( item ).hasClass( 'separator' ) )
				{
					toSave.push({
						index: i,
						isSeparator: true,
						label: $.trim( $( item ).find( '.separator-label' ).text() ),
						withinGroup: $( item ).find( 'input[class=within-group]' ).val(),
						elicitParents: $( item ).find( 'input[class=elicit-parents]' ).val()
					});
				}
				else
				{
					var variableLabel = $( item ).attr( 'id' ).split( '-' ).pop();
					toSave.push({
						index: i,
						variable: variableLabel
					});
				}
			
			});
			
			$( "input[ name='constraintsToSave']" ).val( JSON.stringify( toSave ) );
			$( "form" ).submit();
			
			
		}
		
		function removeSeparator( item ) {
			$( item ).closest( 'li' ).remove();
		}
		
		function addSeparator() {
			$( '.variable-list' ).prepend( 
				"<li class='separator '>" +
				" <span class='config-box config-box-left handle'>" +
				"  <img src='${resource(dir: 'images/icons', file: 'arrow_up_down.png')}' />" + 
				" </span>" + 
				" <span onclick='editSeparator( this )' class='config-box config-box edit'>" +
				"  <img src='${resource(dir: 'images/icons', file: 'pencil.png')}' />" + 
				" </span>" + 
				" <span onclick='removeSeparator( this )' class='config-box config-box-right remove'>" +
				"  <img src='${resource(dir: 'images/icons', file: 'cross.png')}' />" + 
				" </span>" + 
				" <input type='hidden' class='elicit-parents' value='1' />" +
				" <input type='hidden' class='within-group' value='0' />" +
				" <span class='separator-label'>[Group]</span>" + 
				"</li>"
			);
		}
		
		function editSeparator( separator ) {
		
			// Remove highlight from any previously selected element and highlight the new one...
			if ( selectedLi != null )
			{
				selectedLi.removeClass( 'selected' );
			}
			selectedLi = $( separator ).closest( 'li' );
			selectedLi.addClass( 'selected' );
			
			// Setup the inputs in the dialog to reflect the separator we are editing...
			var labelSpan = selectedLi.find( '.separator-label' );
			$( '.edit-dialog-separator input[name=label]' ).val( $.trim( labelSpan.text() ) );
			$( '.edit-dialog-separator input[name=elicit-parents]').prop( 'checked', selectedLi.find( 'input[class=elicit-parents]' ).val() == '1' );
			$( '.edit-dialog-separator input[name=within-group]').prop( 'checked', selectedLi.find( 'input[class=within-group]' ).val() == '1' );
			
			// Need to move the dialog before showing, or it doesn't work...
			var offset = $( separator ).offset().top - $( '.edit-dialog-separator' ).parent().offset().top - 30;
			$( '.edit-dialog-separator' ).css( 'padding-top', offset + 'px' );
			
			$( '.edit-dialog-separator' ).show( 'fast' );
		}
		
		function doneEditingSeparator( save ) {
			// the selectedLi should represent the one currently being edited...
			selectedLi.removeClass( 'selected' );
			
			if ( save )
			{
				selectedLi.find( '.separator-label' ).html( $( '.edit-dialog-separator input[name=label]').val() );
				selectedLi.find( 'input[class=elicit-parents]' ).val( $( '.edit-dialog-separator input[name=elicit-parents]').prop( 'checked' ) ? '1' : '0' );
				selectedLi.find( 'input[class=within-group]' ).val( $( '.edit-dialog-separator input[name=within-group]').prop( 'checked' ) ? '1' : '0' );
			}
			
			$( '.edit-dialog-separator' ).hide( 'fast' );
		}
		
		</g:javascript>
		
	</head>
	
	<body>
	
		<h1>Setup constraints</h1>
	
		<g:form method="post" action="saveConstraints">
			
			<input type="hidden" name="constraintsToSave" value="" />
			
			
			<div class="column-header">
				<input type="button" value="Save and Continue" onclick="save()" class="" >
				<input type="button" value="Add Group" onclick="addSeparator()" class="" >
			</div>
			
			<div class="column-wrapper">
			
				<div class="column-left">
					
					<fieldset class="default ">
					
						<legend>Variables</legend>
						
						<ul class="variable-list sortable selectable ">
						
							<g:each var="g" in="${constraintGroups}">
							
								<g:if test="${!g.isDefaultGroup}">
									<li class='separator '>
										<span class='config-box config-box-left handle'>
											<img src='${resource(dir: 'images/icons', file: 'arrow_up_down.png')}' /> 
										</span>
										<span onclick='editSeparator( this )' class='config-box config-box edit'>
											<img src='${resource(dir: 'images/icons', file: 'pencil.png')}' /> 
										</span>
										<span onclick='removeSeparator( this )' class='config-box config-box-right remove'>
											<img src='${resource(dir: 'images/icons', file: 'cross.png')}' /> 
										</span> 
										<input type='hidden' class='elicit-parents' value='${g.elicitParents ? 1 : 0}' />
										<input type='hidden' class='within-group' value='${g.withinGroup ? 1 : 0}' />
										<span class='separator-label'>${g.label}</span>
									</li>
								</g:if>
					
								<g:each var="c" in="${g.constraintList}">			
									<li id="${c.var.label}" class="variable-item ">
										<span class='config-box config-box-left config-box-right handle'>
											<img src="${resource(dir: 'images/icons', file: 'arrow_up_down.png')}" />
										</span>
										<!-- <span class='config-box config-box-right edit'>
											<img src="${resource(dir: 'images/icons', file: 'pencil.png')}" />
										</span>  -->
										${c.var.label}
									</li>
								</g:each>
							</g:each>
						
						</ul>
					
					</fieldset>
						
				</div>
				
				<div class="column-right">
					
					<div class="edit-dialog-separator " style="display: none;">
					
						<fieldset class="default ">
						
							<legend class="">Edit Group</legend>
							
							<input type="text" name="label" />
							
							<br /><br />
							
							<input id="elicit-parents-checkbox" type="checkbox" name="elicit-parents" />
							<label for="elicit-parents-checkbox">
								Elicit parents for variables in this group
							</label>
							
							<br /><br />
							
							<input id="within-group-checkbox" type="checkbox" name="within-group" />
							<label for="within-group-checkbox">
								Allow variables in this group to be parents of other variables in this group
							</label>
							
							<br /><br />
							
							<input type="button" value="Done" onclick="doneEditingSeparator( true )" class="" />
							<input type="button" value="Cancel" onclick="doneEditingSeparator( false )" class="" />
							
						</fieldset>
						
					</div>
				
				</div>
				
				<div class="column-footer"></div>
				
			</div>
			
		</g:form>
		
	</body>
	
</html>
