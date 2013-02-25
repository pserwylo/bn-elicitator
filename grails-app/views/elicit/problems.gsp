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

			function deleteRedundant( parentLabel, childLabel, parent, child, comment ) {
				confirmDelete( '<g:createLink action="removeRedundant" />', parentLabel, childLabel, parent, child, comment );
			}

			function keepRedundant( parent, child ) {
				var link = '<g:createLink action="keepRedundant" />';
				document.location = link + "?" + generateParams( parent, child );
				return false;
			}

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
				return "parent=" + parent + "&child=" + child + "&scroll=" + $( window ).scrollTop()
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

			<g:if test="${cyclicalRelationships?.size() > 0}">

				<h1><g:message code="problems.cyclical.header" /></h1>

				<div class="info">
					<p>
						Each of the following relationships contain a cycle, which the system cannot handle.
					</p>
					<p>
						Please remove one of the relationships from the cycle by clicking a <bn:rArrow /> image.
					</p>
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
										onclick="deleteCycle( '${relationship.parent.readableLabel.encodeAsJavaScript()}', '${relationship.child.readableLabel.encodeAsJavaScript()}', '${relationship.parent.label.encodeAsJavaScript()}', '${relationship.child.label.encodeAsJavaScript()}', '${relationship.mostRecentComment?.comment?.encodeAsJavaScript() ?: ''}' )"/>
									<bn:variable var="${relationship.child}" />
								</g:each>
							</div>

						</li>

					</g:each>

				</ul>

			</g:if>

			<g:if test="${redundantRelationships?.size() > 0}">

				<div class="redundant content">

					<h1><g:message code="problems.redundant.header" /></h1>

					<div class="overview">
						<g:set var="exampleDirectRelationship" value="${redundantRelationships[0]?.relationship}" />
						<p>
							If any of the more detailed relationships explain the same thing as the direct
							relationship, then remove the direct relationship. However, if they explaining something
							else (e.g. there are multiple ways "${exampleDirectRelationship?.parent}" influences
							"${exampleDirectRelationship?.child}") then don't remove the direct relationship.
						</p>
					</div>

					<ul id="redundant-relationship-list" class="variable-list">

						<g:each in="${redundantRelationships}" var="${rel}">

							<g:set var="notRedundant" value="${rel.relationship.isRedundant != Relationship.IS_REDUNDANT_NO}" />

							<li class="redundant-relationship variable-item">

								<div class='header'>
									<bn:variable includeDescription="false" var="${rel.redundantParent}" />
									<bn:rArrow comment="${rel.relationship?.mostRecentComment?.comment}" />
									<bn:variable includeDescription="false" var="${rel.child}" />
								</div>

								<div class='mediating-chain'>
									<g:message code="problems.redundant.better-explained-by" />
									<ul class="indent item-count-${rel.chains.size()}">
										<g:each in="${rel.chains}" var="chain">
											<li>
												<g:each in="${chain}" var="child" status="i">
													<g:if test="${i > 0}">
														<g:set var="parent"  value="${chain.get( i - 1 )}" />
														<g:set var="comment" value="${bn.mostRecentComment( parent: parent, child: child )}" />
														<bn:rArrow comment="${comment}" />
													</g:if>
													<bn:variable var="${child}" />
												</g:each>
											</li>
										</g:each>
									</ul>
								</div>

								<g:if test="${notRedundant}">
									<div class="answers">
										<button
											class="keep"
											type="button"
											onclick="keepRedundant( '${rel.redundantParent.label}', '${rel.child.label}' )">
											<g:message code="problems.redundant.keep" />
										</button>
										<button
											class="remove"
											type="button"
											onclick="deleteRedundant( '${rel.redundantParent.readableLabel}', '${rel.child.readableLabel}', '${rel.redundantParent.label}', '${rel.child.label}', '${rel.relationship?.mostRecentComment?.comment?.encodeAsJavaScript() ?: ''}' )">
											<g:message code="problems.redundant.remove" />
										</button>
									</div>
								</g:if>

							</li>

						</g:each>

					</ul>
				</div>
			</g:if>
		</g:form>
	</body>
</html>
