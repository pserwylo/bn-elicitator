<%@ page import="grails.converters.JSON" %>
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
<div class="user-details">
	<g:form name="user-details" action="save">
	
		<div class="header">Details</div>

		<input type="hidden" name="existingUsername" value="${isNew ? '' : user.username}" />
		<input type="hidden" name="isNew" value="${isNew ? 'true' : 'false'}" />

		<bn:saveButtons atTop="true" includeDelete="${user.username != 'admin'}" />

		<ul class="form-items">

			<li>

				<g:if test="${!isNew && user.username == 'admin'}">
					<input type="hidden" name="username" value="${user.username}" />
					<span class="label">Username:</span>
					${user.username}
				</g:if>
				<g:else>
					<label>
						Username:
						<input id="input-username" type="text" name="username" value="${isNew ? '' :  user.username}"/>
					</label>
				</g:else>

			</li>
			<li>

				<label>
					Password:
					<input id="input-password" type="password" name="password" placeholder="Enter new password" />
				</label>

			</li>
			<li>

				<label>
					Confirm:
					<input id="input-confirm-password" type="password" name="confirmPassword" placeholder="Confirm new password" />
				</label>

			</li>
			<li>

				<label>
					Real name:
					<input id="input-real-name" type="text" name="realName" placeholder="Enter real name" value="${isNew ? '' :  user.realName}" />
				</label>

			</li>
			<li>

				<label>
					Email:
					<input id="input-email" type="text" name="email" placeholder="Enter email" value="${isNew ? '' :  user.email}" />
				</label>

			</li>
			<li>

				<span class="label">Roles:</span>
				<ul class="roles ">
					<g:each var="role" in="${roles}">
						<li>
							<input
								type="checkbox"
								name="roles"
								value="${role.name}"
								id="role-${role.name}"
								${!isNew && user.roles.contains( role ) ? 'checked="checked"' : ''}/>
							<label for="role-${role.name}">${role.name}</label>

						</li>
					</g:each>
				</ul>

			</li>

		</ul>

		<bn:saveButtons atTop="false" includeDelete="${user.username != 'admin'}" />

		<g:if test="${!isNew}">

			<g:set var="filterTypes" value="${[
					[ label: 'Created Variable', fields: [ 'CreatedVariable' ] ],
					[ label: 'Finished Variable/Round', fields: [ 'FinishedVariable', 'FinishedRound' ] ],
					[ label: 'Logged in/out', fields: [ 'Login', 'Logout' ] ],
					[ label: 'Saved relationship', fields: [ 'SaveRelationship' ] ],
					[ label: 'Viewed relationship', fields: [ 'ViewRelationship' ] ],
					[ label: 'Fixed problems', fields: [ 'KeptRedundant', 'RemovedRedundant', 'RemovedCycle' ] ],
			]}" />

			<div class="header">History</div>
			<ul class="filters">
				<g:each var="filter" in="${filterTypes}">
					<li>
						<label>
							<input type="checkbox" name="filter" value="${filter.fields.join('-')}" checked="checked" />
							${filter.label}
						</label>
					</li>
				</g:each>
			</ul>
			<bnAdmin:eventList eventList="${history}" />


			<div class="header">Email log</div>
			<bnAdmin:emailLogList emailLogList="${emailLog}" />

		</g:if>

	</g:form>
</div>


<script type="text/javascript">
	(function() {

		$( 'button.close' ).click(
				function() {
					doneEditingUser( false );
					return false;
				}
		);

		$( 'button.delete' ).click(
				function() {
					deleteUser( '${user.username}' );
					return false;
				}
		);

		$( 'button.save' ).click(
				function() {
					$( 'form[name=user-details]' ).submit();
				}
		);

	})();
</script>
