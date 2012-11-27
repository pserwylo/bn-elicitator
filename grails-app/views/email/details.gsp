<!doctype html>
<div class="user-details">
	<g:form name="user-details" action="save">
	
		<div class="form-heading">Details</div>

		<input type="hidden" name="existingUsername" value="${isNew ? '' : user.username}" />
		<input type="hidden" name="isNew" value="${isNew ? 'true' : 'false'}" />
		
		<span class="label">Username:</span> 
		<g:if test="${!isNew && user.username == 'admin'}">
			${user.username}
		</g:if>
		<g:else>
			<input id="input-username" type="text" name="username" value="${isNew ? '' :  user.username}"/>
		</g:else>
		
		<br /><br />
		
		<span class="label">Password:</span>
		<input id="input-password" type="password" name="password" placeholder="Enter new password" />
		
		<br />
		
		<span class="label">Confirm:</span>
		<input id="input-confirm-password" type="password" name="confirmPassword" placeholder="Confirm new password" />
		
		<br /><br />
		
		<span class="label roles-label">Roles:</span>
		<ul class="roles ">
			<g:each var="role" in="${roles}">
			
				<li>
				
					<input 
						type="checkbox" 
						name="roles"
						value="${role.name}" 
						id="role-${role.name}" 
						${!isNew && user.roles.contains( role ) ? 'checked="checked"' : ''}
						${!isNew && user.username == 'admin' ? 'disabled="disabled"' : '' })/>
					<label for="role-${role.name}">${role.name}</label>
				
				</li>
			
			</g:each>
		</ul>
		
		<br /><br />

		<input type="button" value="Save" onclick="$( 'form#user-details' ).submit()" class="" />
		<input type="button" value="Cancel" onclick="doneEditingUser( false )" class="" />
		
		<g:if test="${!isNew}">
			<br />
			
			<div class="form-heading">History</div>
			
			<g:if test="${history.size() > 0}">
				<g:each var="event" in="${history}">
					<div class="event">${event}</div>				
				</g:each>
			</g:if>
			<g:else>
				None found...
			</g:else>
		</g:if>
				
	</g:form>
</div>
