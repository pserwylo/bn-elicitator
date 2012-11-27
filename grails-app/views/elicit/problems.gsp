
<%@ page import="bn.elicitator.Relationship" %>
<!doctype html>

<g:set var="hasDetails" value="${delphiPhase > 1 || variables.size() > stillToVisit?.size()}" />

<html>

	<head>

		<meta name="layout" content="main">

		<title>These relationships may not be needed</title>

		<r:require module="elicit" />

		<g:preferencesJs />

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
						<g:setPreference key="show-detailed-explanation" value="false" />
					}
					else
					{
						this.value = this.value.replace( 'Show', 'Hide' );
						detailsHigh.show( 'fast' );
						detailsLow.hide( 'fast' );
						<g:setPreference key="show-detailed-explanation" value="true" />
					}

				});

				if ( '<g:preferenceValue key="show-detailed-explanation" />' == 'true' )
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

			});

		</g:javascript>

	</head>
	
	<body>

		<g:form action="fixProblems">

			<g:if test="${cyclicalRelationships?.size() > 0}">

				<h1>Illegal relationships (which cause cycles)</h1>

				<ul id="cyclical-relationship-list" class="variable-list">

					<g:each in="${cyclicalRelationships}" var="${chain}">

						<li class="cyclical-relationship variable-item">

							<div class='mediating-chain'>
								However, you also said that <g:variableChain chain="${chain}" />
							</div>

						</li>

					</g:each>

				</ul>

			</g:if>

			<g:if test="${redundantRelationships?.size() > 0}">

				<div class="redundant content">

					<h1>(Potentially) unnecessary relationships</h1>

					<input type="button" class="btn-toggle-details" value="Show detailed explanation" />

					<div class='info details-high'>
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

					</div>

					<ul id="redundant-relationship-list" class="variable-list">

						<g:each in="${redundantRelationships}" var="${rel}">

							<li class="redundant-relationship variable-item">

								<div class='header'>
									<g:variable var="${rel.redundantParent}" /> &rarr; <g:variable var="${rel.child}" />
								</div>

								<div class='mediating-chain'>
									<span class="details-high">
										You said:
										<span class='indent'>
											<ul>
												<g:each in="${rel.chains}" var="chain" status="i">
													<li>
														<g:variableChain chain="${chain}" />
														<g:if test="${i < rel.chains.size() - 1}">
															and
														</g:if>
													</li>
												</g:each>
											</ul>
										</span>
									</span>
									<span class="details-low">
										Unnecessary due to <g:variableChain chain="${rel.mediatingChain}" separator=" &rarr; "/>
									</span>
								</div>

								<div class='redundant details-high'>
									However, you also said:
									<span class='indent'>
										<g:variable var="${rel.redundantParent}" /> <em>directly</em> influences <g:variable var="${rel.child}" />
									</span>
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
