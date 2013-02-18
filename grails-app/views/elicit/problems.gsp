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

				<g:if test="${displayAll}">
					$( '#btnBack' ).click( function() {
						document.location = '<g:createLink action="index" />'
					});
				</g:if>

				// TODO: Refactor toggling code out to JS library and perhaps a matching taglib...

				var keepers = $( '.keeper' );
				var btnKeepers = $( '#btnToggleKeepers');

				var msgShow = "<g:message code='general.show' />";
				var msgHide = "<g:message code='general.hide' />";

				keepers.detach().appendTo( $( '#keepers' ) );

				var showKeepers = ${displayAll?.toString()};
				if ( !showKeepers ) {

					keepers.hide();
					btnKeepers.click( function() {

						if ( this.value.substring( 0, 4 ) == msgHide ) {

							this.value = this.value.replace( msgHide, msgShow );
							keepers.hide( 'fast' );

						} else {

							this.value = this.value.replace( msgShow, msgHide );
							keepers.show( 'fast' );

						}
					});

				} else {

					btnKeepers.hide();

				}

				$( '#redundant-relationship-list' ).find( 'button.keep' ).click( function() {
					var link = '<g:createLink action="keepRedundant" />';
					var parts = $( this ).val().split( '-' );
					var parentLabel = parts[ 0 ];
					var childLabel = parts[ 1 ];
					document.location = link + "?parent=" + parentLabel + "&child=" + childLabel<g:if test="${displayAll}"> + '&displayAll=true'</g:if> + "&scroll=" + $( window ).scrollTop();
					return false;
				});

				$( '#cyclical-relationship-list' ).find( 'button.remove' ).click( function() {
					var link = '<g:createLink action="removeCycle" />';
					var parts = $( this ).val().split( '-' );
					var parentLabel = parts[ 0 ];
					var childLabel = parts[ 1 ];
					document.location = link + "?parent=" + parentLabel + "&child=" + childLabel<g:if test="${displayAll}"> + '&displayAll=true'</g:if> + "&scroll=" + $( window ).scrollTop();
					return false;
				});


			});

			function deleteRedundant( parentLabel, childLabel, parent, child, comment ) {
				confirmDelete( '<g:createLink action="removeRedundant" />', parentLabel, childLabel, parent, child, comment );
			}

			function deleteRegular( parentLabel, childLabel, parent, child, comment ) {
				confirmDelete( '<g:createLink action="removeRegular" />', parentLabel, childLabel, parent, child, comment );
			}

			function deleteCycle( parentLabel, childLabel, parent, child, comment ) {
				confirmDelete( '<g:createLink action="removeCycle" />', parentLabel, childLabel, parent, child, comment );
			}

			function confirmDelete( link, parentLabel, childLabel, parent, child, comment ) {
				var message = "Are you sure you want to remove the following relationship?\n\n";
				message    += "  " + parentLabel + " influences " + childLabel;
				if ( typeof comment !== "undefined" && comment != null && comment.length > 0 && comment != 'null' ) {
					message += "\n\n  Reason: \"" + comment + '"';
				}
				var confirmed = confirm( message );
				if ( confirmed ) {
					document.location = link + "?parent=" + parent + "&child=" + child<g:if test="${displayAll}"> + '&displayAll=true'</g:if> + "&scroll=" + $( window ).scrollTop();
				}
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
					The following lists of variables cause cycles, which we are unfortunately unable to allow.
					To proceed, remove one of the relationships which cause the cycle by clicking a
					<bn:rArrow /> or <bn:rArrow forceCommentIcon="true" /> image.
				</div>

				<ul id="cyclical-relationship-list" class="variable-list">

					<g:each in="${cyclicalRelationships}" var="${cyclicRelationship}">

						<li class="cyclical-relationship variable-item">

							<div class='mediating-chain'>
								<g:each in="${cyclicRelationship.relationships}" var="relationship" status="i">
									<g:if test="${i == 0}">
										<bn:variable var="${relationship.child}" />
									</g:if>
									<bn:rArrow
										comment="${relationship.mostRecentComment?.comment}"
										onclick="deleteCycle( '${relationship.parent.readableLabel.encodeAsJavaScript()}', '${relationship.child.readableLabel.encodeAsJavaScript()}', '${relationship.parent.label.encodeAsJavaScript()}', '${relationship.child.label.encodeAsJavaScript()}', '${relationship.mostRecentComment?.comment?.encodeAsJavaScript()}' )"/>
									<bn:variable var="${relationship.parent}" />
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
						If any of the more detailed relationships explains the same thing as the direct relationship, then remove the direct relationship by clicking on the
						<bn:rArrow /> or <bn:rArrow forceCommentIcon="true" /> image.
					</div>

					<ul id="redundant-relationship-list" class="variable-list">

						<g:each in="${redundantRelationships}" var="${rel}">

							<g:set var="notRedundant" value="${rel.relationship.isRedundant != Relationship.IS_REDUNDANT_NO}" />

							<li class="redundant-relationship variable-item ${ !notRedundant ? 'keeper' : ''}">

								<div class='header'>
									<bn:variable includeDescription="false" var="${rel.redundantParent}" />
									<bn:rArrow comment="${rel.relationship?.mostRecentComment?.comment}"  onclick="deleteRedundant( '${rel.redundantParent.readableLabel}', '${rel.child.readableLabel}', '${rel.redundantParent.label}', '${rel.child.label}', '${comment}' )"/>
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
														<bn:rArrow comment="${comment}" onclick="deleteRegular( '${parent.readableLabel}', '${child.readableLabel}', '${parent.label}', '${child.label}', '${comment}' )"/>
													</g:if>
													<bn:variable var="${child}" />
												</g:each>
											</li>
										</g:each>
									</ul>
								</div>

								<g:if test="${notRedundant}">
									<div class="answers">
										<button class="keep" value="${rel.relationship.parent.label}-${rel.relationship.child.label}"><g:message code="problems.redundant.keep" /></button>
									</div>
								</g:if>

							</li>

						</g:each>

					</ul>

					<g:if test="${numKeepers > 0}">
						<input
							type="button"
							style="margin-top: 0.3em;"
							id="btnToggleKeepers"
							value="Show ${numKeepers} more direct relationships" />
							<g:if test="${!displayAll}">
								<span class="info">that you previously chose not to remove</span>
							</g:if>
					</g:if>


					<ul id="keepers" class="variable-list">

						%{-- Will be populated via jQuery once page is loaded. --}%

					</ul>

				</div>

			</g:if>

		</g:form>

		<g:set var="canGoBack" value="${cyclicalRelationships.size() == 0 && numKeepers == redundantRelationships.size()}" />
			<button id="btnBack" class="big" ${!canGoBack ? 'disabled="disabled"' : ''}><g:message code="main.back-to-list" /></button>
			<g:if test="${!canGoBack}">
				<div class='info' style="display: inline-block; max-width: 50%">
					<g:if test="${cyclicalRelationships.size() > 0}">
						<g:message code="problems.cant-return.cyclical" args="${[ cyclicalRelationships.size() ]}" />
					</g:if>
					<g:else>
						<g:message code="problems.cant-return.redundant" args="${[ redundantRelationships.size() - numKeepers ]}" />
					</g:else>
				</div>
			</g:if>

	</body>
	
</html>
