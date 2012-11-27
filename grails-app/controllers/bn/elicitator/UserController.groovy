package bn.elicitator

import org.apache.shiro.crypto.hash.*;

class UserController {

    static defaultAction = "list"
	
	VariableService variableService
	
	def list = {
		
		ShiroUser toShow = params['showUser'] ? ShiroUser.findByUsername( params['showUser'] ) : null
		int totalNumberOfVars = variableService.getAllChildVars().size() /* Used for progress counter of users. */
		[ userList : ShiroUser.list(), roleList : ShiroRole.list(), showUser: toShow, totalNumberOfVars: totalNumberOfVars ]
	
	}
	
	def details = {
		
		if ( params['isNew'] == "true" )
		{
			[ user: new ShiroUser(), roles: ShiroRole.list(), history: [], isNew: true ]
		}
		else
		{
			ShiroUser user = ShiroUser.findByUsername( params['username'] )
			if ( user == null )
			{
				response.status = 404
				render "Not Found"
			}
			else
			{
				[ user: user, roles: ShiroRole.list(), history: Event.findAllByUser( user ), emailLog: EmailLog.findAllByRecipient( user ) ]
			}
		}
	}
	
	def save = { SaveUserCommand cmd ->
		
		cmd.roles = ShiroRole.findAllByNameInList( params.list( 'roles' ) )
		flash.errors = []
		
		boolean retry = false
		boolean requiresSave = false
		
		ShiroUser userToSave = cmd.existingUser ?: new ShiroUser(
			username: cmd.username,
			passwordHash: new Sha256Hash( cmd.password ).toHex(),
			roles: cmd.roles )
		
		// Again, for some reason, the command never polulates 'confirmPassword' :(
		if ( params['password'] != params['confirmPassword'] )
		{
			String error = "Passwords do not match ('" + params['password'] + "' != '" + params['confirmPassword'] + "'"
			render "Form data has errors: " + error + "<br />"
			flash.errors.add( error )
			retry = true
		}
		else if ( cmd.username.length() == 0 )
		{
			String error = "No username specified"
			render "Form data has errors: " + error + "<br />"
			flash.errors.( error )
			retry = true
		}
		// If we are saving the admin user, only allow the password to be changed...
		else if ( cmd.existingUser?.username == 'admin' )
		{
			render "Saving admin user<br />"
			
			if ( cmd.password.length() > 0 )
			{
				render "Updating password to: '" + cmd.password + "'<br />"
				userToSave.passwordHash = new Sha256Hash( cmd.password ).toHex()
				requiresSave = true
			}
		}
		else
		{
			if ( cmd.username.length() < 5 )
			{
				String error = "Username too short. Must be at least 5 characters long."
				render "Form has errors: " + error + "<br />"
				flash.errors.add( error )
				retry = true
			}
			else if ( cmd.existingUser == null || cmd.existingUser.username != cmd.username )
			{
				// New user, or username has changed... does it clash with another user?
				render "Checking for clash of usernames: '" + cmd.username + "'<br />"
				ShiroUser clash = ShiroUser.findByUsername( cmd.username )
				if ( clash != null )
				{
					render "User already exists<br />"
					flash.errors.add( "Username '${cmd.username}' already in use" )
					retry = true
				}
				else
				{
					render "Updating username: '" + cmd.username + "'<br />"
					userToSave.username = cmd.username
					requiresSave = true
				}
			}
			
			if ( cmd.password.length() > 0 )
			{
				render "Updating password: '" + cmd.password + "'<br />"
				userToSave.passwordHash = new Sha256Hash( cmd.password ).toHex()
				requiresSave = true
			}
			else if ( cmd.existingUser == null )
			{
				String error = "New user must have password specified"
				render "Form has errors: " + error + "<br />"
				flash.errors.add( error )
				retry = true
			}
				
			if ( cmd.roles.size() == 0 )
			{
				String error = "User with no roles will not be able to do anything"
				render "Form has errors: " + error + "<br />"
				flash.errors.add( error )
				retry = true
			}
			else
			{
				render "Updating roles: '" + cmd.roles + "'<br />"
				userToSave.roles = cmd.roles
				requiresSave = true
			}

			if ( params['realName']?.size() > 0 )
			{
				userToSave.realName = params['realName']
				requiresSave = true
			}

			if ( params['email']?.size() > 0 )
			{
				userToSave.email = params['email']
				requiresSave = true
			}
		}
		
		if ( requiresSave && !retry)
		{
			render "Saving user<br />"
			if ( !userToSave.save() )
			{
				render "Error saving user<br />"
				userToSave.errors.allErrors.each { flash.errors.add( it.toString() ) }
				retry = true
			}
			else
			{
				redirect( action: 'list' )
			}
		}
		
		if ( retry )
		{
			render "Redirecting with errors: " + flash.errors + "<br />"
			redirect( action: 'list', params: [ showUser: params['existingUsername'] ] )
		}
		
	}
	
}

class SaveUserCommand {

	ShiroUser existingUser = null

	String username

	String password

	String confirmPassword

	String realName

	String email

	List<ShiroRole> roles = null
	
	ShiroUser getExistingUser()
	{
		if ( this.existingUser == null )
		{
			this.existingUser = ShiroUser.findByUsername( params['existingUsername'] )
		}
		return this.existingUser
	}
	
	List<ShiroRole> getRoles() 
	{
		if ( this.roles == null ) 
		{
			this.roles = ShiroRole.findAllByNameInList( params.list( 'roles' ) )
		}
		return this.roles
	}
	
}
