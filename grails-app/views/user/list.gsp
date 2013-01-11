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
<%@ page import="bn.elicitator.LoggedEvent; bn.elicitator.ShiroUser" %>
<%@ page import="bn.elicitator.LoggedEvent" %>
<%@ page import="bn.elicitator.VariableService" %>
<%
	VariableService variableService = grailsApplication.classLoader.loadClass( 'bn.elicitator.VariableService' ).newInstance()
 %>

<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Users</title>
		
		<g:javascript>
		
			var selectedLi = null;
		
			function deleteUser( username ) {
				var result = confirm( "Are you sure you want to delete\n\n    User: " + username + "?" );
			}
		
			function editUser( username ) {
			
				// Remove highlight from any previously selected element and highlight the new one...
				if ( selectedLi != null )
				{
					selectedLi.removeClass( 'selected' );
				}
				
				selectedLi = $( '#user-' + username );
				
				if ( selectedLi.length == 0 )
				{
					console.log( "Error finding li node for user '" + username + "'" );
					return;
				}
				
				selectedLi.addClass( 'selected' );
				
				$( '#form-contents' ).html( "<img src='${resource(dir: 'images', file: 'spinner.gif')}' />" );
				$( '.edit-dialog-user' ).hide( 'fast' );
				var offset = selectedLi.offset().top - $( '.edit-dialog-user' ).parent().offset().top - 30;
				
				var isNew = selectedLi.find( 'input[name=isNew]' ).length > 0;
				
				$( '#form-contents' ).load( 
					'${createLink( action: 'details' )}', 
					{ 
						username: username,
						isNew: isNew 
					},
					function( response ) {
						// Need to move the dialog before showing, or it doesn't work...
						$( '.edit-dialog-user' ).css( 'padding-top', offset + 'px' ); 
						$( '.edit-dialog-user' ).show( 'fast' ) 
					}
				);
				
			}
			
			function addUser() {
				var newLi = 
					'<li id="user-NewUser" class=" variable-item">' +
					'	<span id="new-user" class="config-box config-box-left" onclick="editUser( \'NewUser\' );">' + 
					'		<img src="${resource(dir: 'images/icons', file: 'pencil.png')}" />' + 
					'	</span>' + 
					'	<span class="config-box config-box-right" onclick="deleteUser( this );">' + 
					'		<img src="${resource(dir: 'images/icons', file: 'cross.png')}" />' + 
					'	</span>' + 
					'	<span class="username">' + 
					'		New User' + 
					'	</span>' + 
					'	<input type="hidden" name="isNew" value="true" />' + 
					'</li>';
				$( '.variable-list' ).append( newLi );
				$( '#add-user' ).prop( 'disabled', true );
				editUser( 'NewUser' );
				
			}
			
			function doneEditingUser( save ) {
				// the selectedLi should represent the one currently being edited...
				selectedLi.removeClass( 'selected' );
				$( '.edit-dialog-user' ).hide( 'fast' );
			}
			
			<g:if test="${showUser}">
			$( function() {
				editUser( '${showUser.username}' );
			});
			</g:if>
			
		</g:javascript>
		
	</head>
	
	<body>
	
		<h1>Manager users</h1>
	
		<g:if test="${flash.errors?.size() > 0}">
			<ul class="errors">
				<g:each var="err" in="${flash.errors}">
					<li>${err}</li>
				</g:each>
			</ul>
		</g:if>
	
		<br />

		<div class="list-users">

			<div class="column-wrapper">

				<div class="column-header">

					<input type="button" value="Add user" class="" id="add-user" onclick="addUser();" />

				</div>

				<div class="column-left">

					<fieldset class="default ">
						<legend>Users</legend>
						<ul class="variable-list ">
							<g:each var="user" in="${userList}">
								<li id="user-${user.username}" class=" variable-item">

									<span
										class="config-box config-box-left ${user.username == 'admin' ? 'config-box-right' : ''}"
										onclick="editUser( $( this ).closest( 'li' ).find( '.username' ).text().trim() );">
										<img src='${resource(dir: 'images/icons', file: 'pencil.png')}' />
									</span>


									<g:if test="${user.username != 'admin'}">
										<span
											class="config-box config-box-right"
											onclick="deleteUser( this );">
											<img src='${resource(dir: 'images/icons', file: 'cross.png')}' />
										</span>
									</g:if>

									<span class="username">
										${user.username}
									</span>

									<div class="stats">
										<g:set var="loginEvent" value="${LoggedEvent.findByTypeAndUser( LoggedEvent.Type.LOGIN, user )}" />
										Last login: ${loginEvent ? loginEvent.date.format( 'dd/MM/yyyy hh:mm' ) : "Never"}
										<br />
										<g:set var="visitedCount" value="${variableService.getVisitedCount( user )}" />
										Completed: ${(int)( ( (double)visitedCount / totalNumberOfVars ) * 100 )}%
									</div>
								</li>
							</g:each>
						</ul>
					</fieldset>

				</div>

				<div class="column-right">

					<div class="edit-dialog-user " style="display: none;">

						<fieldset class="default ">

							<legend id="edit-dialog-user-legend" class="">Edit user</legend>

							<div id="form-contents">
							</div>

						</fieldset>

					</div>

				</div>

			</div>

		</div>

	</body>
	
</html>
