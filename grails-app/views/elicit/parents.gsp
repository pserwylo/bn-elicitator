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
<%@ page import="bn.elicitator.AppProperties; bn.elicitator.Variable; bn.elicitator.Comment" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Elicit Parents of Variable</title>

		<g:javascript>

        /**
        * Keep track of all of the dialogs here, indexed by their parent label. We store them here because we'll be
        * detatching them from the DOM (rather than removing them completely), and don't want to lose them when we do so.
        * @type {Object}
        */
		var detailsDivs = {};

		/**
		 * The currently showing variable dialog...
		 * @type {string}
		 */
		var currentVariable = null;

		/**
		 * If they do any sort of modification to a variable (e.g. check the relationship checkbox, or modify their
		 * reason), we store it here until they hit save. This allows us to both add visual feedback
		 * to the screen and also prevent them clicking the 'finished' button down the bottom.
		 * @type {Array}
		 */
		var unsavedVariables = [];

		function onFinish() {
			if ( unsavedVariables.length > 0 )
			{
				var plural = unsavedVariables.length == 1 ? '' : 's';
				alert( "Cannot finish yet.\n\nYou still have " + unsavedVariables.length + " unsaved variable" + plural + ".\n\nClick 'Show Details' next to the variables with a red exclamation mark, then save the details." );
			}
			else
			{
				document.location = '${createLink( action: 'completedVariable', params: [ variable: "${variable.label}" ] )}';
			}
		}

		$( document ).ready( function() {

			var potentialParentsList = $( '.potential-parents-list' );
			potentialParentsList.find( 'input[class=potential-parent]' ).change( onToggleListCheckbox );

			$( '#inputNewVariableLabel' ).autocomplete( {
				source: "<g:createLink action='getVariablesFromOntology' controller='data'/>",
				minLength: 2,
				select: function( event, ui ) {

				}
			});

			// There are several 'save' buttons (two per form, and two 'save and hide') per form...
			var btnHolders = $( '.save-wrapper' );
			btnHolders.find( 'button.save' ).click( onSave );
			btnHolders.find( 'button.close' ).click( hideVarDetails );

			$( '.var-details' ).detach().each( function( i, item ) {
				var label = item.id.substring( 0, item.id.length - '-details'.length );
				$( item ).find( '#input-' + label + '-form' ).change( onToggleFormCheckbox );
				$( item ).find( 'textarea[name=comment]' ).change( function(){ markUnsaved( label ); } );
				detailsDivs[ label ] = item;
			});

			$( '.unsaved-icon' ).click( function() {
				alert( 'This variable has unsaved changes.\n\nClick "Show Details" and save them before continuing.' )
			})

		});

		/**
		 * Adds a new comment to the top of the list of comments for the dialog belonging to 'variable'.
		 * The comment will be styled differently if the relationship is stated as existing or not.
		 * @param variable
		 * @param exists
		 * @param comment
		 */
		function addComment( variable, exists, comment ) {

			var dialog = detailsDivs[ variable ];
			var list = $( dialog ).find( '.reasons-list' );
			var phaseClass = 'phase-${AppProperties.properties.delphiPhase}';

			var li = list.find( '.me.' + phaseClass );
			if ( comment != null && comment.length == 0 )
			{
				if ( li.length > 0 )
				{
					li.remove();
				}

				if ( list.children().length > 0 )
				{
					list.children().remove();
				}
			}
			else
			{
				$( dialog ).find( '.no-reasons' ).remove();

				var liContents = '"' + comment + '"<div class="author"> - Myself</div>';
				if ( li.length > 0 )
				{
					li.removeClass( 'exists doesnt-exist' );
					li.addClass( exists ? 'exists' : 'doesnt-exist' );
					li.html( liContents );
				}
				else
				{
					var classes = [ 'me', phaseClass ];
					classes.push( exists ? 'exists' : 'doesnt-exist' );
					list.prepend( '<li class="' + classes.join( ' ' ) + '">' + liContents + '</li>' );
				}
			}

		}

		/**
		 * Collects data from the form and posts it via AJAX to the server.
		 * @param event
		 * @return {Boolean}
		 */
		function onSave( event ) {

			var button = $( event.target );
			var dialog = $( '#var-details-dialog' );
			var parent = dialog.find( 'input:checkbox[name=parents]' ).val();
			var comment = dialog.find( 'textarea[name=comment]' ).val();
			var exists = dialog.find( 'input:checkbox[name=parents]' ).prop( 'checked' );

			var allButtonsInDialog = dialog.find( 'button.save' );
			allButtonsInDialog.html( 'Saving...' ).prop( 'disabled', true );

			var undisable = function() {
				allButtonsInDialog
					.prop( 'disabled', false )
					.html( "Save" );
			};

			$.ajax({

				type: 'post',

				url: '${createLink( action: 'save' )}',

				data: {
					child: '${variable.label}',
					parent: parent,
					comment: comment,
					exists: exists
				},

				dataType: 'text json',

				error: function( data ) {
					undisable();
					alert( "Error while saving. The administrator has been notified." );
				},

				success: function( data ) {
					markSaved( parent );
					undisable();
					addComment( parent, exists, comment );
				}
			});

			return false;

		}

		/**
		 * Show the details form for the variable which the li belongs to.
		 * @param event
		 */
        function onToggleListCheckbox( event ) {
            var checked = $( event.target ).prop( 'checked' );
            var label = $( event.target ).attr( 'value' );
            showVarDetails( label );
			toggleRelationship( label, checked );
        }

        /**
         * Apart from toggling the appropriate stuff in the form , we will also make sure the list checkbox is kept
         * up to date.
		 * @param event
		 */
        function onToggleFormCheckbox( event ) {
            var checked = $( event.target ).prop( 'checked' );
            var label = $( event.target ).attr( 'value' );
        	toggleRelationship( label, checked );
        }

		/**
		 * Takes a variable and adds it to the unsavedVariables array (if it isn't already in there).
		 * @param variableLabel
		 */
		function markUnsaved( variableLabel ) {
			if ( $.inArray( variableLabel, unsavedVariables ) == -1 ) 	{
				unsavedVariables.push( variableLabel );
				$( '#' + variableLabel + '-variable-item' ).addClass( 'unsaved' );
			}

		}

		function markSaved( variableLabel ) {
			var index = $.inArray( variableLabel, unsavedVariables );
			if ( index >= 0 ) {
				unsavedVariables.splice( index, 1 );
				$( '#' + variableLabel + '-variable-item' ).removeClass( 'unsaved' );
			}
		}

		function isUnsaved( variableLabel ) {
			return ( $.inArray( variableLabel, unsavedVariables ) != -1 );
		}

        /**
		 * @param variableLabel
		 * @param hasRelationship
		 */
        function toggleRelationship( variableLabel, hasRelationship ) {
			markUnsaved( variableLabel );
            $( '#input-' + variableLabel ).prop( 'checked', hasRelationship );
            $( '#input-' + variableLabel + '-form' ).prop( 'checked', hasRelationship );
        }

        /**
        * Find the dialog with the form in it, detach anything which was previously in it, and then append the form
        * which belongs to parentLabel. We then set the location of the form appropriately (aligned with the list item
        * that belongs to the parent), and then shows it with an animation.
        * @param parentLabel
        */
		function showVarDetails( parentLabel ) {

			currentVariable = parentLabel;

			// Need to move the dialog before showing, or it doesn't work...
			var dialog = $( '#var-details-dialog' );
			var offset = $( '#' + parentLabel + '-variable-item' ).offset().top - dialog.parent().offset().top;
			var contents = dialog.find( '.contents' );
			contents.children().detach();
			contents.append( detailsDivs[ parentLabel ] );
			dialog.find( 'legend' ).html( 'Does ' + parentLabel + '<br />influence ${variable.readableLabel}?' );
			dialog.css( 'padding-top', offset + 'px' );
			dialog.show( 'fast' );

		}

		function hideVarDetails() {

			var safe = true;

			if ( isUnsaved( currentVariable ) ) {
				safe = confirm( 'You have unsaved changes about this variable. Close anyway?' );
			}

			if ( safe ) {
				$( '#var-details-dialog' ).hide( 'fast' );
				currentVariable = null;
			}

		}

		function toggleAddVariable( show ) {
			var form = $( '#new-var-form' );
			show ? form.show( 'fast' ) : form.hide( 'fast' );
		}

		function finish() {
			document.location = '${createLink( action: 'finished' )}';
		}

		</g:javascript>

		<r:require module="elicitParents" />

	</head>
	
	<body>

		<div class="elicit-parents">

			<div class="column-wrapper">

				<div class="column-left">

					<fieldset class="default">

						<legend>
							${variable.readableLabel} <bn:variableDescription var="${variable}" />
						</legend>

						<p>
							<g:if test="${variable.usageDescription?.length() > 0}">
								${variable.usageDescription.replace( '\n', '<br />' )}
							</g:if>
							<g:else>
								<g:message code="elicit.parents.desc" args="${[variable.readableLabel]}" />
							</g:else>
						</p>
						<br />

						<bnElicit:potentialParentsList potentialParents="${potentialParents}" child="${variable}" />

					</fieldset>

					<input type="button" style="margin-top: 5px;" value="Finished with ${variable.readableLabel}" class="big " onclick="onFinish()"/>

				</div>

				<div class="column-right">

					<div id="var-details-dialog" class="" style="display: none;">

						<fieldset class="default ">

							<legend class="">Details</legend>

							<div class='contents'>

							</div>

						</fieldset>

					</div>

				</div>

				<div class="column-footer">

				</div>

			</div>

		</div>

	</body>
	
</html>
