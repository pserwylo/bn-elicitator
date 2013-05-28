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
<%@ page import="bn.elicitator.VariableService" %>
<g:set var="variableService" value="${grailsApplication.classLoader.loadClass( 'bn.elicitator.VariableService' ).newInstance()}" />

<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<r:require module="adminUsers" />
		<title>Users</title>
		
		<g:javascript>
		
			var selectedLi = null;

			var eventFilter = null;

			function confirmDeleteUser( username ) {

				if ( username == 'admin' ) {
					throw new Error( "Attempted to delete admin user." );
				}

				var li = $( '#user-' + username );

				if ( selectedLi == null || li.attr( "id" ) != selectedLi.attr( "id" ) ) {
					throw new Error( "Trying to delete '" + username + "', but they are not selected." )
				}

				doneEditingUser( false );

				li.remove();

				if ( username != '' ) {
					$.post( "${createLink( action: "remove" )}", { username: username } );
				}
			}

			function deleteUser( username ) {

				var confirmationMessage = "Are you sure you want to delete\n\n    User: " + username + "?";

				// Empty username means the 'New User' which doesn't exist in the database, only in this browser...
				var allowDelete = username == "" ? true : confirm( confirmationMessage );

				if ( allowDelete ) {
					confirmDeleteUser( username );
				}
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
					throw new Error( "Error finding li node for user '" + username + "'" );
				}
				
				selectedLi.addClass( 'selected' );

				var formContents = $( '#form-contents' );
				var editDialog = $( '#edit-dialog-user' );

				formContents.html( "<img src='${resource(dir: 'images', file: 'spinner.gif')}' />" );
				editDialog.hide( 'fast' );
				var offset = selectedLi.offset().top - editDialog.parent().offset().top - 30;
				
				var isNew = selectedLi.find( 'input[name=isNew]' ).length > 0;
				
				formContents.load(
					'${createLink( action: 'details' )}', 
					{ 
						username: username,
						isNew: isNew 
					},
					function( response ) {
						// Need to move the dialog before showing, or it doesn't work...
						editDialog.css( 'padding-top', offset + 'px' );
						editDialog.show( 'fast' );
						eventFilter = new EventFilter();
					}
				);
				
			}
			
			function addUser() {
				var newLi = 
					'<li id="user-NewUser" class=" variable-item">' +
					'	<span class="username">' + 
					'		New User' + 
					'	</span>' + 
					'	<input type="hidden" name="isNew" value="true" />' +
					'	<button class="show-details" onclick="editUser( \'NewUser\' );">Show details</button>' +
					'</li>';
				$( '.variable-list' ).append( newLi );
				$( '#add-user' ).prop( 'disabled', true );
				editUser( 'NewUser' );
				
			}
			
			function doneEditingUser( save ) {
				// the selectedLi should represent the one currently being edited...
				selectedLi.removeClass( 'selected' );
				$( '#edit-dialog-user' ).hide( 'fast' );
			}
			
			<g:if test="${showUser}">
				(function() {
					editUser( '${showUser.username}' );
				})();
			</g:if>

		</g:javascript>

		<g:javascript>

			var EventFilter = function() {

				var filterCheckboxes = $( 'input:checkbox[name=filter]' );
				var historyItems     = $( 'ul#eventLog li' );

				var getEventsToShow = function( checkbox ) {
					var classNames = [];
					var names = $( checkbox ).val().split( "-" );
					for ( var i = 0; i < names.length; i ++ ) {
						classNames.push( "bn.elicitator.events." + names[ i ] + "Event" );
					}
					return classNames;
				};

				var shouldShow = function( item, eventsToShow ) {
					var show = false;
					for ( var i = 0; i < eventsToShow.length; i ++ ) {
						if ( $( item ).hasClass( eventsToShow[ i ] ) ) {
							show = true;
							break;
						}
					}
					return show;
				};

				var toggleItem = function( historyItem, eventsToShow ) {
					var item = $( historyItem );
					var show = shouldShow( historyItem, eventsToShow );
					var isVisible = item.is( ":visible" );
					if ( isVisible && !show ) {
						item.hide( 'fast' );
					} else if ( !isVisible && show ) {
						item.show( 'fast' );
					}
				};

				var performFilter = function() {
					console.log( "Filtering" );
					var allEventsToShow = [];
					for ( var i = 0; i < filterCheckboxes.length; i ++ ) {
						var checkbox = $( filterCheckboxes[ i ] );
						if ( checkbox.prop( 'checked' ) ) {
							allEventsToShow = allEventsToShow.concat( getEventsToShow( checkbox ) );
						}
					}

					console.log( allEventsToShow );

					for ( var j = 0; j < historyItems.length; j ++ ) {
						var item = $( historyItems[ j ] );
						toggleItem( item, allEventsToShow );
					}
				};

				filterCheckboxes.click( performFilter );

			};

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

									<span class='buttons'>
										<button class="show-details" onclick="editUser( '${user.username}' );">Show details</button>
										<sec:ifAllGranted roles='ROLE_ADMIN'>
											<form action='${request.contextPath}/j_spring_security_switch_user' method='POST'>
												<input type="hidden" name="j_username" value="${user.username}" />
												<button type="submit">Switch to user</button>
											</form>
										</sec:ifAllGranted>
									</span>

									<span class="username">
										${user.username} <g:if test="${user.realName != user.username}">${user.realName}</g:if>
									</span>

									<div class="stats">
										<g:if test="${user.email != user.username}">${user.email}<br /></g:if>
										<bnUser:completedInfo user="${user}" />
									</div>

								</li>
							</g:each>
						</ul>
					</fieldset>

				</div>

				<div class="column-right">

					<div id="edit-dialog-user" class="floating-dialog" style="display: none;">

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
