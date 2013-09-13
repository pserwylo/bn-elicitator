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

				var nextLink = '${createLink( [ action : 'likelihood', params : [ id : variable.id ] ] )}';
				var manager = new bn.das2004.Manager( '.compatible-configurations', nextLink );

				/**
				 * Goes through each radio button for the current scenario, and extracts the relevant info
				 * from the "name" attribute (which really looks like: "parentId=8,parentStateId=23,otherParentId=7")
				 * and the "value" attribute (which is the otherParentStateId).
				 * The info is then posted with AJAX to the server for saving.
				 */
				var saveStates = function() {

					var scenario            = manager.currentQuestion();
					var radios              = scenario.find( 'input[ type=radio ]:checked' );
					var otherParentStateIds = [];

					// We'll figure these out the first time we crack the loop open below...
					var parentId      = -1;
					var parentStateId = -1;

					for ( var i = 0; i < radios.length; i ++ ) {

						var otherParentStateId = parseInt( radios[ i ].value );
						var info = { otherParentStateId : otherParentStateId };

						// Example name: "variable=8,sibling=7,state=23"
						var nameParts          = radios[ i ].name.split( "," );
						for ( var j = 0; j < nameParts.length; j ++ ) {
							var keyValue = nameParts[ j ].split( "=" );
							info[ keyValue[ 0 ] ] = parseInt( keyValue[ 1 ] );
						}

						if ( parentId < 0 ) {
							parentId      = info.parentId;
							parentStateId = info.parentStateId;
						}

						otherParentStateIds.push( info.otherParentStateId );
					}

					var data = {
						parentStateId       : parentStateId,
						otherParentStateIds : otherParentStateIds
					};

					$.post(
						'${createLink( [ action : 'ajaxSaveCompatibleParentConfiguration' ] )}',
						$.param( data )
					);
				};

				var siblingStates = $( '.sibling-states' );
				siblingStates.buttonset();
				manager.equalizeHeights( siblingStates.find( '.ui-button' ) );
				siblingStates.find( 'input[type=radio]').change( function() {
					var parent        = $( this ).closest( 'ul.siblings' );
					var siblingCount  = parent.children().size();
					var selectedCount = parent.find( 'input[ type=radio ]:checked' ).length;

					if ( siblingCount == selectedCount ) {
						saveStates();
						manager.nextQuestion();
					}
				});

			});
	</g:javascript>

	<r:require module="elicitProbabilities" />

</head>

<body>

<div class="elicit-probabilities">

	<help:help index="1" uniqueId="probabilities-das2004-expected" targetId="scenario-container" title="What do you expect?">
		Imagine you are considering a request to insure somebodies car.
		We have <em>some</em> information, but are missing other info.
		What is the most likely scenario for the missing information.
	</help:help>

	<div class="column-wrapper">

		<div class="column-left">

			<fieldset class="default">

				<legend>
					<bn:htmlMessage code="elicit.probabilities.expected.header" args="${[ "$variable.readableLabel ${bn.variableDescription( [ var : variable ] )}" ]}" />
					(<span id="scenario-number">1</span> of <span id="total-scenarios"></span>)
				</legend>

				<div id="scenario-container">
					<das2004:expected variable="${variable}" />
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
