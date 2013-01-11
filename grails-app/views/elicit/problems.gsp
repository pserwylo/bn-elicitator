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

		<r:require module="elicit" />

		<bn:preferencesJs />

		<g:javascript>

			$( document).ready( function() {

				var detailsHigh = $( '.details-high' );
				var detailsLow = $( '.details-low' );

				var btn = $( '.btn-toggle-details');
				var animate = false;

				btn.click( function() {

					if ( this.value.substring( 0, 4 ) == 'Hide' )
					{
						this.value = this.value.replace( 'Hide', 'Show' );
						detailsHigh.hide( 'fast' );
						detailsLow.show( 'fast' );
						<bn:setPreference key="show-detailed-explanation" value="false" />
					}
					else
					{
						this.value = this.value.replace( 'Show', 'Hide' );
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

				var radios = $( 'input:radio' );
				radios.change( function() {

					if ( radios.filter( ":checked" ).length == radios.length / 2 )
					{
						$( 'input:submit' ).prop( 'disabled', false );
					}

				});

				// TODO: Refactor toggling code out to JS library and perhaps a matching taglib...
				var keepers = $( '.keeper' );
				var btnKeepers = $( '#btnToggleKeepers');

				var showKeepers = ${displayAll};
				if ( !showKeepers )
				{
					keepers.hide();

					btnKeepers.click( function() {

						if ( this.value.substring( 0, 4 ) == 'Hide' )
						{
							this.value = this.value.replace( 'Hide', 'Show' );
							keepers.hide( 'fast' );
						}
						else
						{
							this.value = this.value.replace( 'Show', 'Hide' );
							keepers.show( 'fast' );
						}

					});
				}
				else
				{
					btnKeepers.hide();
				}


			});

		</g:javascript>

	</head>
	
	<body>

		<g:form action="fixProblems">

			<g:if test="${cyclicalRelationships?.size() > 0}">

				<h1>Illegal relationships (which cause cycles)</h1>

				<ul id="cyclical-relationship-list" class="variable-list">

					<g:each in="${cyclicalRelationships}" var="${rel}">

						<li class="cyclical-relationship variable-item">

							<div class='mediating-chain'>
								<bn:relationshipChain chain="${rel.relationships}" />
							</div>

							<bn:removeCycleCheckboxes cyclicalRelationship="${rel}" />

						</li>

					</g:each>

				</ul>

			</g:if>

			<g:if test="${redundantRelationships?.size() > 0}">

				<div class="redundant content">

					<h1>(Potentially) better explanations</h1>

					<input type="button" class="btn-toggle-details" value="Show detailed explanation" />

					<g:if test="${numKeepers > 0}">
						<input
							type="button"
							style="margin-top: 0.3em;"
							id="btnToggleKeepers"
							value="Show ${numKeepers} direct relationships you said are necessary" />
					</g:if>

					%{--<div class='info details-high'>
						We think the following relationships may be unnecessary. Originally you stated that a variable is <em>directly</em>
						influenced by another. Later on, you also stated that it is <em>indirectly</em> influenced by that
						variable, by virtue of it influencing some intermediate variables.
					</div>

					<div class='info details-high'>
						We presume that the more accurate way to think about the relationships is the indirect version. However,
						there may be times where a variable influences another both directly and indirectly through its
						influence on other variables. An example might be:

						<span class="indent">
							A specific law banning smoking may <em>directly</em> decrease smoking rates, because most people
							do not engage in illegal activities. In addition, it may <em>indirectly</em> decrease smoking rates,
							through extra law enforcement officials to fight under-the-counter tobacco sales therefore reducing
							peoples access to tabacco.
						</span>

					</div>--}%

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
										Potentially better explained by
										<bn:variableChain chain="${rel.mediatingChain}" />
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
									<label>
										<input type="radio" name="${rel.redundantParent.label}-${rel.child.label}-keep" value="remove" /> Remove relationship
									</label>
									<label>
										<input type="radio" name="${rel.redundantParent.label}-${rel.child.label}-keep" value="keep" ${rel.relationship.isRedundant == Relationship.IS_REDUNDANT_NO ? 'checked="checked"' : ''} /> Keep relationship
									</label>
								</div>

							</li>

						</g:each>

					</ul>

				</div>

			</g:if>

			<input
				%{-- Disabled if there is some which do not have prefilled radio buttons --}%
				${redundantRelationships*.relationship.count { it.isRedundant == Relationship.IS_REDUNDANT_UNSPECIFIED } > 0 ? 'disabled="disabled"' : '' }
				type="submit"
				class="big"
				value="Continue" />

		</g:form>

	</body>
	
</html>
