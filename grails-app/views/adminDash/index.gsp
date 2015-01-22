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
<!doctype html>
<html xmlns="http://www.w3.org/1999/html">

	<head>
		<meta name="layout" content="main">
		<title>Admin Dashboard</title>
		<style type="text/css">
			.column-left {
				width  : 350px;
			}

			.variable-item {
				padding-left: 0.5em;
			}

			.overview .info {
				margin-top: 0.5em;
			}

			.overview .info .label {
				font-weight: bold;
			}

			.widgets {
				margin-bottom: 0.3em;
			}

		</style>

		<g:javascript>

		$( document).ready( function() {

			var mainContainer  = $( '.column-footer' );
			var imageContainer = mainContainer.find( '.image' );
			var htmlMatrix     = mainContainer.find( '#htmlMatrix' );

			var scaleSvg = function() {
				var svg          = imageContainer.find( 'svg' );
				var targetWidth  = mainContainer.width() - 40;
				var ratio        = targetWidth / svg.width();
				svg.width( targetWidth );
				svg.height( svg.height() * ratio );
			};

			var loadImage = function( delphiPhase, minUsers ) {
				var graphStats = $( '#graphStats' );

				graphStats.html( "Loading statistics..." );
				imageContainer.html( "Loading image..." );

				var svgLink    = "${createLink( controller: 'output', action: 'svgDiagram' )}";
				var statsLink  = "${createLink( controller: 'output', action: 'jsonStats' )}";
				var matrixLink = "${createLink( controller: 'output', action: 'htmlMatrix' )}";
				var params     = { phase : delphiPhase, minUsers : minUsers };
				imageContainer.load( svgLink, params, scaleSvg );
				htmlMatrix.load( matrixLink, params );
				$.get( statsLink, params, function( data ) { graphStats.html( "Variables: " + data.totalNodes + ", relationships: " + data.totalEdges) } );
			};

			$( window ).resize( scaleSvg );

			mainContainer.find( 'select' ).change( function() {
				var phase    = $( '#graphPhase'    ).val();
				var strength = $( '#graphMinUsers' ).val();
				loadImage( phase, strength );
			});

			loadImage( ${appProperties.delphiPhase}, 1 );

			$( '#targetParticipantsPerQuestion' ).change( function() {
				var params = { participantsPerQuestion: this.value };
				$.post( '${createLink( action: "ajaxSaveParticipantsPerQuestion" )}', params );
			});

		});

		</g:javascript>
	</head>
	
	<body>
	
		<h1>Admin Dashboard</h1>

		<g:if test="${flash.messages?.size() > 0}">
			<div class="message">
				<ul>
					<g:each in="${flash.messages}" var="message">
						<li>${message}</li>
					</g:each>
				</ul>
			</div>
		</g:if>

		<div class="column-wrapper">

			<div class="column-header">
				<fieldset class="default ">
					<legend>Study overview</legend>

					<div class="overview">
						<div class="info">
							<span class="label">Name:</span> ${appProperties.title}
						</div>
						<div class="info">
							<span class="label">Admin Email:</span> ${appProperties.adminEmail}
						</div>
						<div class="info">
							<span class="label">Prizes:</span> ${appProperties.arePrizesEnabled() ? "Enabled" : "Disabled"}
						</div>
						%{--<div class="info">
							<span class="label">Delphi Phase:</span> ${appProperties.delphiPhase}
						</div>--}%
					</div>

					<input type="button" value="Edit details" onclick="document.location = '${createLink( action: 'editStudyDetails' )}'" />
					%{--<input type="button" value="Advance to next delphi phase" onclick="document.location = '${createLink( action: 'confirmAdvanceDelphiPhase' )}'" />--}%
					%{--<input type="button" value="Init relationships" onclick="document.location = '${createLink( action: 'initRelationships' )}'" />--}%

				</fieldset>
			</div>

			<div class="column-left">
				<fieldset class="default ">
					<legend>Participants</legend>

					<input type="button" class="" value="Manage Users" onclick="document.location = '${createLink( controller: 'user', action: 'list' )}'" />

					<div class="overview">
						<div class="info">
							<span class="label">Total users:</span> ${userList.size()}
						</div>
						<div class="info">
							<span class="label">Completed this round:</span> ${completedCurrentRound.size()}
						</div>
					</div>

					<ul class="variable-list ">
						<g:each in="${userList}" var="${user}">
							<li class=" variable-item">
								${user.username}
								<span class="stats">
									<bnUser:completedInfo user="${user}" />
									<g:if test="${user.canWinPrize()}">
										<img src="${fam.icon( [ name : "money" ] )}" />
									</g:if>
								</span>
							</li>
						</g:each>
					</ul>

				</fieldset>
			</div>

			<div class="column-right">
				<fieldset class="default ">
					<legend>Variable Allocations</legend>

					<div class="overview">
						<div class="info">
							<label class="label">Participants per question:
								<input id="targetParticipantsPerQuestion" type="text" style="width: 30px;" value="${appProperties.targetParticipantsPerQuestion}"/>
							</label>
							${questionsPerUser} (<g:link controller="allocateQuestions">details</g:link>)
						</div>
					</div>

					<bnAdmin:allocationList />

				</fieldset>
			</div>

			<div class="column-footer">
				<fieldset class="default ">
					<legend>Completed BN</legend>
					<bnAdmin:completedBn />
				</fieldset>
			</div>

			<g:if test="${appProperties.elicitationPhase == bn.elicitator.AppProperties.ELICIT_2_RELATIONSHIPS}">

				<div class="column-footer">
					<fieldset class="default ">
						<legend>Results</legend>

						<div class="widgets">
							<form>
								<select id="graphPhase" name="phase">
									<g:each in="${1..appProperties.delphiPhase}" var="phase">
										<option value="${phase}" ${phase == appProperties.delphiPhase ? 'selected="selected"' : ''}>Phase ${phase}</option>
									</g:each>
								</select>
								<select id="graphMinUsers" name="minUsers">
									<g:each in="${1..totalExperts}" var="count">
										<option value="${count}">Min ${count} experts</option>
									</g:each>
								</select>
							</form>
						</div>

						<span id="graphStats" class="info"></span>
						<div id="htmlMatrix"></div>
						<div class="image stats"></div>

					</fieldset>
				</div>

			</g:if>

		</div>
	
	</body>
	
</html>
