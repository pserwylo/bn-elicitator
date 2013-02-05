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

<g:set var="hasDetails" value="${delphiPhase > 1 || variables.size() > stillToVisit?.size()}" />

<html>

	<head>

		<meta name="layout" content="main">

		<title>These relationships may not be needed</title>

		<r:require module="elicitProblems" />

		<bn:preferencesJs />

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

				var showKeepers = ${displayAll?.toString()};
				if ( !showKeepers )
				{
					keepers.hide();

					btnKeepers.click( function() {

						if ( this.value.substring( 0, 4 ) == msgHide )
						{
							this.value = this.value.replace( msgHide, msgShow );
							keepers.hide( 'fast' );
						}
						else
						{
							this.value = this.value.replace( msgShow, msgHide );
							keepers.show( 'fast' );
						}

					});
				}
				else
				{
					btnKeepers.hide();
				}

				$( '#redundant-relationship-list' ).find( 'button' ).click( function() {
					var link = $( this ).hasClass( 'keep' ) ? '<g:createLink action="keepRedundant" />' : '<g:createLink action="removeRedundant" />';
					var parts = this.value.split( '-' );
					var parentLabel = parts[ 0 ];
					var childLabel = parts[ 1 ];
					document.location = link + "?parent=" + parentLabel + "&child=" + childLabel<g:if test="${displayAll}"> + '&displayAll=true'</g:if> + "&scroll=" + $( window ).scrollTop();
					return false;
				});

				$( '#cyclical-relationship-list' ).find( 'button.remove' ).click( function() {
					var link = '<g:createLink action="removeCycle" />';
					var parts = this.value.split( '-' );
					var parentLabel = parts[ 0 ];
					var childLabel = parts[ 1 ];
					document.location = link + "?parent=" + parentLabel + "&child=" + childLabel<g:if test="${displayAll}"> + '&displayAll=true'</g:if> + "&scroll=" + $( window ).scrollTop();
					return false;
				});


			});

		</g:javascript>

	</head>
	
	<body>

		<g:form action="fixProblems">

			<g:if test="${cyclicalRelationships?.size() > 0}">

				<h1><g:message code="problems.cyclical.header" /></h1>

				<ul id="cyclical-relationship-list" class="variable-list">

					<g:each in="${cyclicalRelationships}" var="${rel}">

						<li class="cyclical-relationship variable-item">

							<div class='mediating-chain'>
								<bn:relationshipChain includeTooltip="false" chain="${rel.relationships}" />
							</div>

							<bnProblems:removeCycleOptions cyclicalRelationship="${rel}" />

						</li>

					</g:each>

				</ul>

			</g:if>

			<g:if test="${redundantRelationships?.size() > 0}">

				<div class="redundant content">

					<h1><g:message code="problems.redundant.header" /></h1>

					<g:if test="${numKeepers > 0}">
						<input
							type="button"
							style="margin-top: 0.3em;"
							id="btnToggleKeepers"
							value="Show ${numKeepers} direct relationships you said are necessary" />
					</g:if>

					<ul id="redundant-relationship-list" class="variable-list">

						<g:each in="${redundantRelationships}" var="${rel}">

							<li class="redundant-relationship variable-item ${ (rel.relationship.isRedundant == Relationship.IS_REDUNDANT_NO) ? 'keeper' : ''}">

								<div class='header'>
									<bn:variable includeDescription="false" var="${rel.redundantParent}" /> <bn:rArrow comment="${rel.relationship?.mostRecentComment?.comment}" /> <bn:variable includeDescription="false" var="${rel.child}" />
								</div>

								<div class='mediating-chain'>
									<g:if test="${rel.relationship.isRedundant == Relationship.IS_REDUNDANT_NO}">
											<g:message code="problems.redundant.previously-kept" />
									</g:if>

									<g:message code="problems.redundant.better-explained-by" />
									<bnProblems:listOfVariableChains chains="${rel.chains}" />
								</div>

								<div class="answers">
									<g:if test="${rel.relationship.isRedundant != Relationship.IS_REDUNDANT_NO}">
										<button class="keep" value="${rel.redundantParent.label}-${rel.child.label}">
											<g:message code="problems.redundant.keep" />
										</button>
									</g:if>

									<button class="remove" value="${rel.redundantParent.label}-${rel.child.label}">
										<g:message code="problems.redundant.remove" />
									</button>
								</div>

							</li>

						</g:each>

					</ul>

				</div>

			</g:if>

		</g:form>

		<g:if test="${displayAll}">
			<button id="btnBack" class="big"><g:message code="main.back-to-list" /></button>
		</g:if>

	</body>
	
</html>
