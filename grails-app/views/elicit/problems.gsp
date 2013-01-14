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

				var detailsHigh = $( '.details-high' );
				var detailsLow = $( '.details-low' );

				var btn = $( '.btn-toggle-details');
				var animate = false;

				var msgShow = "<g:message code='general.show' />";
				var msgHide = "<g:message code='general.hide' />";

				btn.click( function() {

					if ( this.value.substring( 0, 4 ) == msgHide )
					{
						this.value = this.value.replace( msgHide, msgShow );
						detailsHigh.hide( 'fast' );
						detailsLow.show( 'fast' );
						<bn:setPreference key="show-detailed-explanation" value="false" />
					}
					else
					{
						this.value = this.value.replace( msgShow, msgHide );
						detailsHigh.show( 'fast' );
						detailsLow.hide( 'fast' );
						<bn:setPreference key="show-detailed-explanation" value="true" />
					}

				});

				if ( '<bn:preferenceValue key="show-detailed-explanation" />' == 'true' )
				{
					detailsHigh.show();
					detailsLow.hide();
					btn.trigger( 'click' );
				}
				else
				{
					detailsHigh.hide();
					detailsLow.show();
				}

				// TODO: Refactor toggling code out to JS library and perhaps a matching taglib...
				var keepers = $( '.keeper' );
				var btnKeepers = $( '#btnToggleKeepers');

				var showKeepers = ${displayAll};
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
					document.location = link + "?parent=" + parentLabel + "&child=" + childLabel<g:if test="${displayAll}"> + '&displayAll=true'</g:if>;
					return false;
				});

				$( '#cyclical-relationship-list' ).find( 'button.remove' ).click( function() {
					var link = '<g:createLink action="removeCycle" />';
					var parts = this.value.split( '-' );
					var parentLabel = parts[ 0 ];
					var childLabel = parts[ 1 ];
					document.location = link + "?parent=" + parentLabel + "&child=" + childLabel<g:if test="${displayAll}"> + '&displayAll=true'</g:if>;
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
								<bn:relationshipChain chain="${rel.relationships}" />
							</div>

							<bn:removeCycleOptions cyclicalRelationship="${rel}" />

						</li>

					</g:each>

				</ul>

			</g:if>

			<g:if test="${redundantRelationships?.size() > 0}">

				<div class="redundant content">

					<h1><g:message code="problems.redundant.header" /></h1>

					<input type="button" class="btn-toggle-details" value="${message( code: "general.show")} ${message( code: "general.detailed-explanation")}" />

					<g:if test="${numKeepers > 0}">
						<input
							type="button"
							style="margin-top: 0.3em;"
							id="btnToggleKeepers"
							value="Show ${numKeepers} direct relationships you said are necessary" />
					</g:if>

					<div class='info details-high'>
						<g:message code="problems.redundant.desc" />
					</div>

					<ul id="redundant-relationship-list" class="variable-list">

						<g:each in="${redundantRelationships}" var="${rel}">

							<li class="redundant-relationship variable-item ${ (rel.relationship.isRedundant == Relationship.IS_REDUNDANT_NO) ? 'keeper' : ''}">

								<div class='header'>
									<bn:variable var="${rel.redundantParent}" /> <bn:rArrow comment="${rel.relationship?.comment?.comment}" /> <bn:variable var="${rel.child}" />
								</div>

								<div class='mediating-chain'>
									<span class="details-high">
										You said:
										<span class='indent'>
											<ul>
												<g:each in="${rel.chains}" var="chain" status="i">
													<li>
														<bn:variableChain chain="${chain}" />
														<g:if test="${i < rel.chains.size() - 1}">
															and
														</g:if>
													</li>
												</g:each>
											</ul>
										</span>
									</span>

									<span class="details-low">
										<g:message code="problems.redundant.better-explained-by" args="${[ bn.variableChain( chain: rel.mediatingChain ) ]}" />
										%{--<bn:variableChain chain="${rel.mediatingChain}" />--}%
									</span>
								</div>

								<div class='redundant details-high'>
									However, you also said:
									<span class='indent'>
										<bn:variable var="${rel.redundantParent}" /> <em>directly</em> influences <bn:variable var="${rel.child}" />
									</span>
								</div>

								<div class='redundant details-high'>
									If you think that the way in which <bn:variable var="${rel.redundantParent}" /> influences
									<bn:variable var="${rel.child}"/> is purely because it influences <bn:variable var="${rel.mediatingChain[ 1 ]}" />,
									then you should remove this <em>direct</em> relationship (it doesn't provide as useful information as the
									indirect alternative you provided).
								</div>

								<div class="answers">
									<span class='details-high'>
										Would you like to:
									</span>

									<g:if test="${rel.relationship.isRedundant != Relationship.IS_REDUNDANT_NO}">
										<button class="keep" value="${rel.redundantParent.label}-${rel.child.label}">
											<g:message code="problems.redundant.keep" />
										</button>
									</g:if>

									<button class="remove" value="${rel.redundantParent.label}-${rel.child.label}">
										<g:message code="problems.redundant.remove" />
										<g:if test="${rel.relationship.isRedundant == Relationship.IS_REDUNDANT_NO}">
											<g:message code="problems.redundant.previously-kept" />
										</g:if>
									</button>

									%{--<label>
										<input type="radio" name="${rel.redundantParent.label}-${rel.child.label}-keep" value="remove" /> Remove relationship
									</label>
									<label>
										<input type="radio" name="${rel.redundantParent.label}-${rel.child.label}-keep" value="keep" ${rel.relationship.isRedundant == Relationship.IS_REDUNDANT_NO ? 'checked="checked"' : ''} /> Keep relationship
									</label>--}%
								</div>

							</li>

						</g:each>

					</ul>

				</div>

			</g:if>

		</g:form>

	</body>
	
</html>
