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

			var nextScreen = function() {
				document.location = '<das2004:afterLikelihood variable="${variable}" />';
			};

			var questions = $( '.question.likelihood' );
			var parent = questions.parent();
			questions.detach().sort( function() {
				return Math.random() - 0.5;
			}).appendTo( parent );

			if ( questions.length == 0 ) {
				nextScreen();
				return;
			}

			$( '#total-scenarios' ).html( questions.length );

			var currentQuestionIndex = 0;

			var currentQuestion = function() {
				return $( questions[ currentQuestionIndex ] );
			};

			currentQuestion().show();

			var nextQuestion = function() {
				currentQuestion().hide( 'slide', { direction : 'right', duration : 200 }, function() {
					currentQuestionIndex ++;
					if ( currentQuestionIndex < questions.length ) {
						currentQuestion().show( 'slide', { direction : 'left', duration : 200 } );
						$( '#scenario-number' ).html( currentQuestionIndex + 1 );
					} else {
						nextScreen();
					}
				});
			};

			/**
			 * Extracts the relevant info from the selected radio button from
			 * the "name" attribute (which really looks like: "parentConfigurationId=1,childId=2,childStateId=3")
			 * and the "value" attribute (which is the otherParentStateId).
			 * The info is then posted with AJAX to the server for saving.
			 */
			var saveEstimation = function() {

				var question            = currentQuestion();
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

			var probabilityOptions = $( '.probabilities' );
			probabilityOptions.buttonset();
			probabilityOptions.find( 'input[type=radio]').change( function() {
				var parent        = $( this ).closest( 'ul.probabilities' );
				var optionCount   = parent.children().size();
				var selectedCount = parent.find( 'input[ type=radio ]:checked' ).length;

				if ( optionCount == selectedCount ) {
					saveEstimation();
					nextQuestion();
				}
			});

		});
	</g:javascript>

	<r:require module="elicitProbabilities" />
</head>

<body>

<div class="elicit-probabilities">

	<div class="column-wrapper">

		<div class="column-left">

			<fieldset class="default">

				<legend>
					<g:message code="elicit.probabilities.likelihood.header" args="${[ "$variable.readableLabel ${bn.variableDescription( [ var : variable ] )}" ]}" />
				</legend>

				<div id="scenario-header">
					<g:message code="elicit.probabilities.expected.current-total" args="${[
							"<span id='scenario-number'>1</span>",
							"<span id='total-scenarios'></span>",
					]}" />
				</div>

				<das2004:likelihood variable="${variable}" />

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
