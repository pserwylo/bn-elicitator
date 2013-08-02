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
<%@ page import="bn.elicitator.AppProperties; bn.elicitator.Variable; bn.elicitator.Comment" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Likelihood of scenarios involving  ${variable} influencle</title>

		<g:javascript>
			$( document ).ready( function() {
				var scenarios = $( '.scenario' );
				$( '#total-scenarios' ).html( scenarios.length );

				var currentScenarioIndex = 0;

				var currentScenario = function() {
					return $( scenarios[ currentScenarioIndex ] );
				};

				var showCurrentScenario = function() {
					scenarios.hide();
					currentScenario().show();
					$( '#scenario-number' ).html( currentScenarioIndex + 1 );
				};

				showCurrentScenario();

				$( '#btnNext' ).click( function() {
					currentScenarioIndex ++;
					if ( currentScenarioIndex < scenarios.length ) {
						showCurrentScenario();
					}
				});

			})
		</g:javascript>

		<r:require module="elicitChildrenFirst" />

	</head>
	
	<body>

		<div class="elicit-probabilities">

			<div class="column-wrapper">

				<div class="column-left">

					<fieldset class="default">

						<legend>
							Scenarios involving ${variable.readableLabel} <bn:variableDescription var="${variable}" />
						</legend>

						<div id="scenario-header">
							Scenario <span id='scenario-number'></span> of <span id='total-scenarios'></span>:
						</div>

						<bnElicit:scenarios variable="${variable}" />

					</fieldset>

					<div class="button-wrapper">

						<button id="btnNext" type="button">
							Next scenario
						</button>

						<button id="btnBack" type="button">
							Back
						</button>
					</div>

				</div>

				<div class="column-footer">

				</div>

			</div>

		</div>

	</body>
	
</html>
