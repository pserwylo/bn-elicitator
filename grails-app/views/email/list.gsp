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
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Email templates</title>

		<style type="text/css">
			.column-left {
				width: 40%;
			}
			.column-right {
				margin-left: 42%;
			}
		</style>

		<g:javascript>
		
			var currentTemplate = null;

			function deselect() {

				$( 'li.selected' ).removeClass( 'selected' );
				currentTemplate = null;

			}

			function select( emailName ) {

				deselect();

				currentTemplate = emailName;

				var selectedLi = $( '#email-' + emailName );
				var success = true;
				if ( selectedLi.length == 0 )
				{
					console.log( "Error finding li node for user '" + emailName + "'" );
					success = false;
				}
				else
				{
					selectedLi.addClass( 'selected' );
				}

				return success;
			}

			function save() {

				if ( currentTemplate == null )
				{
					throw new Error( "Cannot save, not editing a template." );
				}

				$.post(

					'${createLink( action: 'save' )}',

					{
						emailName: currentTemplate,
						subject: $( '#inputSubject' ).val(),
						body: $( '#inputBody' ).val()
					},

					function( response ) {

						doneEditing();

					}

				)

			}

			function doneEditing() {

				deselect();
				$( '.edit-dialog' ).hide( 'fast' );

			}

			function editEmail( emailName ) {

				var found = select( emailName );

				if ( found )
				{

					var editDialog = $( '.edit-dialog' );
					editDialog.prop( 'disabled', true );

					var offset = $( "#email-" + currentTemplate ).offset().top - editDialog.parent().offset().top - 30;

					$.get(

						'${createLink( action: 'template' )}',

						{ emailName: emailName },

						function( data ) {

							$( '#inputSubject' ).val( data.subject );
							$( '#inputBody' ).val( data.body );
							$( '#outputDescription' ).html( data.description );

							// Need to move the dialog before showing, or it doesn't work...
							editDialog.css( 'padding-top', offset + 'px' );
							editDialog.show( 'fast' );
							editDialog.prop( 'disabled', false );

						}

					);
				}
			}


			$( document ).ready( function() {

				$( '#btnSave' ).click( save );
				$( '#btnCancel' ).click( doneEditing );

				<g:if test="${showEmail}">
					editEmail( '${showEmail.name}' );
				</g:if>


			})

		</g:javascript>
		
	</head>
	
	<body>
	
		<h1>Manager email templates</h1>

		<br />

		<div class="list-emails">

			<div class="column-wrapper">

				<div class="column-header">
				</div>

				<div class="column-left">

					<fieldset class="default ">

						<legend>Email templates</legend>

						<ul class="variable-list ">

							<g:each var="email" in="${emailTemplateList}">

								<li id="email-${email.name}" class=" variable-item">

									<span
										class="config-box config-box-left config-box-right"
										onclick="editEmail( '${email.name}' );">
										<img src='${resource(dir: 'images/icons', file: 'pencil.png')}' />
									</span>

									<div class="name">${email.name}</div>
									<div class="stats subject">${email.description}</div>

								</li>

							</g:each>

						</ul>

					</fieldset>

				</div>

				<div class="column-right">

					<div class="edit-dialog dialog " style="display: none;">

						<fieldset class="default ">

							<legend id="edit-dialog-legend" class="">Edit template</legend>

							<g:form id="editForm">

								<label>Description</label>
								<span class="description" id="outputDescription"></span>

								<label for="inputSubject">Subject</label>
								<g:textField id="inputSubject" name="subject"/>

								<label for="inputBody">Body</label>
								<g:textArea id="inputBody" name="body"/>

								<input id="btnCancel" type="button" value="Cancel" />
								<input id="btnSave" type="button" value="Save" />

							</g:form>

						</fieldset>

					</div>

				</div>

			</div>

		</div>

	</body>
	
</html>
