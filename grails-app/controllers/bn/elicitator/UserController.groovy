/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bn.elicitator

import bn.elicitator.auth.Role
import bn.elicitator.auth.User
import bn.elicitator.auth.UserRole
import bn.elicitator.events.LoggedEvent
import org.apache.commons.collections.CollectionUtils

class UserController {

    static defaultAction = "list"
	
	VariableService variableService
	UserService userService

	def list = {

		User toShow = null

		if ( params.containsKey( "showUser" ) ) {
			toShow = User.findByUsername( params.showUser )
		}

		int totalNumberOfVars = variableService.getAllChildVars().size() /* Used for progress counter of users. */

		[
			userList : User.list(),
			roleList : Role.list(),
			showUser: toShow,
			totalNumberOfVars: totalNumberOfVars
		]
	
	}

	/**
	 * Called from an ajax context, so need not render anything when done.
	 */
	def remove = {
		String username = (String)params['username'];
		this.userService.deleteUser( User.findByUsername( username ) )
	}
	
	def details = {
		
		if ( params['isNew'] == "true" )
		{
			[ user: new User(), roles: Role.list(), history: [], isNew: true ]
		}
		else
		{
			User user = User.findByUsername( (String)params['username'] )
			if ( user == null )
			{
				throw new Exception( "Not found: " + params['username'] )
			}
			else
			{
				[
					user: user,
					roles: Role.list(),
					history: LoggedEvent.findAllByUser( user, [ sort: "date", order: "desc" ] ),
					emailLog: EmailLog.findAllByRecipient( user )
				]
			}
		}
	}

	/**
	 * TODO: Update this for new roles stuff (i.e. s/Shiro/SpringSecurity/g).
	 */
	def save = { SaveUserCommand cmd ->

		cmd.roles = Role.findAllByNameInList( params.list( 'roles' ) )
		flash.errors = []
		
		boolean retry = false
		boolean requiresSave = false
		boolean requiresChangeToRoles = false

		User userToSave = cmd.existingUser ?: new User( username: cmd.username, password: cmd.password )

		if ( !cmd.existingUser || !CollectionUtils.isEqualCollection( cmd.roles, userToSave.roles ) ) {
			requiresChangeToRoles = true
			requiresSave          = true
		}

		// Again, for some reason, the command never populates 'confirmPassword' :(
		if ( params['password'] != params['confirmPassword'] ) {
			String error = "Passwords do not match ('" + params['password'] + "' != '" + params['confirmPassword'] + "'"
			flash.errors.add( error )
			retry = true
		}
		else if ( cmd.username.length() == 0 ) {
			String error = "No username specified"
			flash.errors.( error )
			retry = true
		}
		// If we are saving the admin user, only allow the password to be changed...
		else if ( cmd.existingUser?.username == 'admin' ) {
			if ( !cmd.roles.contains( Role.admin ) ) {
				String error = "Admin user must have admin role"
				flash.errors.( error )
				retry = true
			} else {
				if ( cmd.password.length() > 0 ) {
					userToSave.password = cmd.password
					requiresSave = true
				}
			}

		} else {
			if ( !cmd.username?.length() ) {
				String error = "Please specify a username."
				flash.errors.add( error )
				retry = true
			}
			else if ( cmd.existingUser == null || cmd.existingUser.username != cmd.username ) {
				// New user, or username has changed... does it clash with another user?
				User clash = User.findByUsername( cmd.username )
				if ( clash != null ) {
					flash.errors.add( "Username '${cmd.username}' already in use" )
					retry = true
				} else {
					userToSave.username = cmd.username
					requiresSave = true
				}
			}
			
			if ( cmd.password.length() > 0 ) {
				userToSave.password = cmd.password
				requiresSave = true
			}
			else if ( cmd.existingUser == null ) {
				String error = "New user must have password specified"
				flash.errors.add( error )
				retry = true
			}

			if ( cmd.roles.size() == 0 ) {
				String error = "User with no roles will not be able to do anything"
				flash.errors.add( error )
				retry = true
			}

			if ( params['realName']?.size() > 0 ) {
				userToSave.realName = params['realName']
				requiresSave = true
			}

			if ( params['email']?.size() > 0 ) {
				userToSave.email = params['email']
				requiresSave = true
			}
		}
		
		if ( requiresSave && !retry) {

			if ( userToSave.id && requiresChangeToRoles ) {
				List<Role> rolesToRemoveFrom = userToSave.roles.findAll { !cmd.roles.contains( it ) }.toList()
				UserRole.findAllByUserAndRoleInList( userToSave, rolesToRemoveFrom )*.delete( flush: true, failOnError: true )
			}

			if ( !userToSave.save( flush: true ) ) {
				userToSave.errors.allErrors.each { flash.errors.add( it.toString() ) }
				retry = true
			} else {
				if ( requiresChangeToRoles ) {
					cmd.roles.findAll { !userToSave.roles.contains( it ) }.each { role ->
						role.addUser( userToSave )
					}
				}
				redirect( action: 'list' )
			}
		}
		
		if ( retry ) {
			redirect( action: 'list', params: [ showUser: params['existingUsername'] ] )
		}
		
	}
	
}

class SaveUserCommand {

	User existingUser = null

	String username

	String password

	String confirmPassword

	String realName

	String email

	List<Role> roles = null

	User getExistingUser()
	{
		if ( this.existingUser == null )
		{
			this.existingUser = User.findByUsername( params['existingUsername'] )
		}
		return this.existingUser
	}

	List<Role> getRoles()
	{
		if ( this.roles == null ) 
		{
			this.roles = Role.findAllByNameInList( params.list( 'roles' ) )
		}
		return this.roles
	}
	
}
