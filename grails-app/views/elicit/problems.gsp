%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 11/12/12 1:12 PM.$year Peter Serwylo (peter.serwylo@monash.edu)
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
<%@ page import="bn.elicitator.Relationship" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>These relationships may not be needed</title>
		<r:require module="elicitProblems" />

		<g:javascript>

			$( document).ready( function() {

				<g:if test="${scroll != null}">
					$( window ).scrollTop( ${scroll} );
				</g:if>

			});

			function deleteCycle( parentLabel, childLabel, parent, child, comment ) {
				confirmDelete( '<g:createLink action="removeCycle" />', parentLabel, childLabel, parent, child, comment );
			}

			function confirmDelete( link, parentLabel, childLabel, parent, child, comment ) {
				var message = "Are you sure you want to remove the following relationship?\n\n";
				message    += ' "' + parentLabel + '" directly influences "' + childLabel + '"';
				if ( ( typeof comment !== "undefined" ) && ( comment != null ) && ( comment.length > 0 ) && ( comment != 'null' ) ) {
					message += "\n\n  Reason: \"" + comment + '"';
				}
				var confirmed = confirm( message );
				if ( confirmed ) {
					document.location = link + "?" + generateParams( parent, child );
				}
			}

			function generateParams( parent, child ) {
				return "parent=" + parent + "&child=" + child + "&scroll=" + $( window ).scrollTop() + "&redirectTo=${redirectTo.encodeAsURL()}";
			}

		</g:javascript>

	</head>
	
	<body>

		<bnIcons:key>

			<bnIcons:icon
					label="${message( code: "icon-key.relationship.label")}"
					iconPath="${resource([ dir: "images/icons/", file: "arrow_right.png" ])}"
					classes="icon-key-details"><g:message code="icon-key.relationship" /></bnIcons:icon>

			<bnIcons:icon
					label="${message( code: "icon-key.relationship-with-comment.label")}"
					iconPath="${resource([ dir: "images/icons-custom/", file: "arrow_right_comment.png" ])}"
					classes="icon-key-details"><g:message code="icon-key.relationship-with-comment" /></bnIcons:icon>

		</bnIcons:key>

		<a name="top"></a>

		<g:form action="fixProblems">
				<h1><g:message code="problems.cyclical.header" /></h1>
				<div class="info">
					<p>Each of the following relationships contain a cycle, which the system cannot handle.</p>
					<p>Please remove one of the relationships from the cycle by clicking a <bn:rArrow /> image.</p>
				</div>

				<ul id="cyclical-relationship-list" class="variable-list">
					<g:each in="${cyclicalRelationships}" var="${cyclicRelationship}">
						<li class="cyclical-relationship variable-item">
							<div class='mediating-chain'>
								<g:each in="${cyclicRelationship.relationships}" var="relationship" status="i">
									<g:if test="${i == 0}">
										<bn:variable var="${relationship.parent}" />
									</g:if>
									<bn:rArrow
										comment="${relationship.mostRecentComment?.comment}"
										onclick="deleteCycle( '${relationship.parent.readableLabel.encodeAsJavaScript().encodeAsHTML()}', '${relationship.child.readableLabel.encodeAsJavaScript().encodeAsHTML()}', '${relationship.parent.label}', '${relationship.child.label}', '${relationship.mostRecentComment?.comment?.encodeAsJavaScript()?.encodeAsHTML() ?: ''}' )"/>
									<bn:variable var="${relationship.child}" />
								</g:each>
							</div>
						</li>
					</g:each>
				</ul>
		</g:form>
	</body>
</html>
