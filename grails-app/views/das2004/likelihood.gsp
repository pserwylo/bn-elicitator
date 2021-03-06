%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 2013 Peter Serwylo (peter.serwylo@monash.edu)
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
<!doctype html>
<html>

<head>
	<meta name="layout" content="main">
	<title>Likelihood of scenarios involving  ${variable}</title>

	<g:javascript>
		$( document ).ready( function() {

			var manager = new bn.das2004.Manager( '.question.likelihood', '<das2004:afterLikelihood variable="${variable}" />' );

			/**
			 * Extracts the relevant info from the selected radio button from
			 * the "name" attribute (which really looks like: "parentConfigurationId=1,childId=2,childStateId=3")
			 * and the "value" attribute (which is the otherParentStateId).
			 * The info is then posted with AJAX to the server for saving.
			 */
			var saveEstimation = function() {

				var question            = manager.currentQuestion();
				var radio               = question.find( 'input[ type=radio ]:checked' );

				if ( radio.length == 0 ) {
					throw new Error( "Could not find checked radio when saving probability estimation." );
				}

				var probabilityPercentage = parseInt( radio.val() );

				// Example name: "parentConfigurationId=1,childId=2,childStateId=3"
				var nameParts = radio.attr( 'name' ).split( "," );
				var info = { probabilityPercentage : probabilityPercentage };
				for ( var j = 0; j < nameParts.length; j ++ ) {
					var keyValue = nameParts[ j ].split( "=" );
					info[ keyValue[ 0 ] ] = parseInt( keyValue[ 1 ] );
				}

				var link = '${createLink( [ action : 'ajaxSaveProbabilityEstimation' ] )}';
				$.post( link, info );
			};

			var needsTooltips = function() {
				return $( window ).width() >= 800;
			};

			var addTooltipsToCurrent = function() {

				var labels = manager.currentQuestion().find( 'label' );
				if ( !needsTooltips() ) {
					labels.removeAttr( 'title' );
					return;
				}

				labels.qtip({
					content : {
						title : function( api ) {
							return $( this ).attr( 'data-tooltip-title' );
						}
					},
					position : {
						my : "top middle",
						at : "bottom middle"
					}
				});
			};

			var destroyTooltipsOnCurrent = function() {
				var labels = manager.currentQuestion().find( 'label' );
				labels.qtip( 'destroy', true );
			};

			var fieldset = $( 'fieldset' );
			bn.utils.scrollToTop( fieldset );

			var probabilityOptions = $( '.probabilities' );
			probabilityOptions.buttonset();
			probabilityOptions.find( 'input[type=radio]').change( function() {

				if ( manager.isCurrent( this ) ) {
					var parent        = $( this ).closest( 'ul.probabilities' );
					var optionCount   = parent.children().size();
					var selectedCount = parent.find( 'input[ type=radio ]:checked' ).length;
					if ( optionCount == selectedCount ) {
						destroyTooltipsOnCurrent();
						saveEstimation();
						manager.nextQuestion();
						addTooltipsToCurrent();
						bn.utils.scrollToTop( fieldset );
					}
				}

			});

			addTooltipsToCurrent();

		});
	</g:javascript>

	<r:require module="elicitProbabilities" />
</head>

<body>

<div class="elicit-probabilities">

	<help:help index="1" uniqueId="probabilities-das2004-likelihood" targetId="scenario-container" title="How often does this happen?">
		If you were presented with the above scenario 100 times, how often would you expect this outcome?
	</help:help>

	<div class="column-wrapper">

		<div class="column-left">

			<fieldset class="default">

				<legend>
					<bn:htmlMessage code="elicit.probabilities.likelihood.header" />
					<span class='scenario-counters'>
						(<span id='scenario-number'>1</span> of <span id='total-scenarios'></span>)
					</span>
				</legend>

				<div id="scenario-container">
					<das2004:likelihood variable="${variable}" />
				</div>

			</fieldset>

		</div>

		<div class="column-footer">

			<div class="button-wrapper">
				<button id="btnBack" type="button" onclick="document.location = '${createLink( action : 'index' )}'">
					Back
				</button>
			</div>

		</div>

	</div>

</div>

</body>

</html>
