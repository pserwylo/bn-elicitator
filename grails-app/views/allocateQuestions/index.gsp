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

		<g:javascript>
			$(document).ready( function() {

				var input_n = $( '#slider_n' );
				var input_C = $( '#slider_C' );
				var input_q = $( '#slider_q' );
				var input_t = $( '#slider_t' );

				var output_n = $( '#output_n' );
				var output_C = $( '#output__C' );
				var output_q = $( '#output_q' );
				var output_t = $( '#output_t' );
				var output_T = $( '#output__T' );

				var makeSlider = function( input, value, min, max ) {
					input.slider({
						value: value,
						min: min,
						max: max
					});
				};

				makeSlider( input_n, ${expertCount}, 0, 100 );
				makeSlider( input_C, 3, 1, 20 );
				makeSlider( input_q, 10, 1, ${totalQuestions} );
				makeSlider( input_t, 60, 1, 300 );

				var get_C = function() { return input_C.slider( "value" ); };
				var get_Q = function() { return ${totalQuestions}; };
				var get_n = function() { return input_n.slider( "value" ); };
				var get_q = function() { return input_q.slider( "value" ); };
				var get_t = function() { return input_t.slider( "value" ); };
				var get_T = function() { return get_t() * get_q(); };

				var set_n = function( value ) {
					value = Math.ceil( value );
					input_n.slider( "value", value );
					output_n.html( value );
				};

				var set_C = function( value ) {
					value = Math.ceil( value );
					input_C.slider( "value", value );
					output_C.html( value );
				};

				var set_q = function( value ) {
					value = Math.ceil( value );
					input_q.slider( "value", value );
					output_q.html( value );
				};

				var set_t = function( value ) {
					value = Math.ceil( value );
					input_t.slider( "value", value );
					output_t.html( value );
				};

				var calc_T = function() {
					var seconds = get_t() * get_q();
					output_T.html( Math.round( seconds / 60 ) );
				};

				var calc_q = function() {
					input_q.slider( "value", get_C() * get_Q() / get_n() );
				};

				var calc_C = function() {
					input_q.slider( "value", get_n() * get_q() / get_Q() );
				};

				var calc_n = function() {
					input_q.slider( "value", get_C() * get_Q() / get_q() );
				};

				// C = Number of participants per question
				// Q = Total questions
				// n = Number of Participants
				// q = Questions asked of each participant
				// t = Estimated time per question
				// T = Estimated total time expected for each participant

				var converge_q = function( factor ) {
					var q = get_C() * get_Q() / get_n();
					set_q( get_q() - ( get_q() - q ) * factor );
				};

				var converge_C = function( factor ) {
					var C = get_n() * get_q() / get_Q();
					set_C( get_C() - ( get_C() - C ) * factor );
				};

				var converge_n = function( factor ) {
					var n = get_C() * get_Q() / get_q();
					set_n( get_n() - ( get_n() - n ) * factor );
				};

				var converge = function( toConverge ) {
					var factor     = 0.5;
					var iterations = 10;
					for ( var it = 0; it < iterations; it ++ ) {
						for ( var i = 0; i < toConverge.length; i ++ ) {
							toConverge[ i ]( factor );
						}
					}
				};

				input_n.on( "slidechange", function( event ) {
					refresh_n();
					if ( event.originalEvent) {
						converge_q( 1.0 );
					}
				});

				input_C.on( "slidechange", function( event ) {
					refresh_C();
					if ( event.originalEvent) {
						converge( [ converge_q, converge_n ] );
					}
				});

				input_q.on( "slidechange", function( event ) {
					refresh_q();
					calc_T();
					if ( event.originalEvent) {
						converge_n( 1.0 );
					}
				});

				input_t.on( "slidechange", function( event ) {
					refresh_t();
					calc_T();
				});

				var refresh_n = function() { output_n.html( get_n() ); };
				var refresh_C = function() { output_C.html( get_C() ); };
				var refresh_q = function() { output_q.html( get_q() ); };
				var refresh_t = function() { output_t.html( get_t() ); };

				refresh_n();
				refresh_C();
				refresh_q();
				refresh_t();

				calc_q();
				calc_T();

			});
		</g:javascript>

		<style>
		.ui-slider {
			width: 200px;
			margin: 0.3em 0 0.5em 0;
		}

		.output {
			font-weight: bold;
		}
		</style>

		<title>Admin Dashboard</title>
		<r:require module="admin" />
	</head>
	
	<body>
	
		<h1>Divy up questions</h1>

		<div class="column-wrapper">
			<div class="column-header">
				<fieldset class="default ">

					<legend>Figure it all out...</legend>

					<form>
						<ul>
							<li>
								<label>Q: Total questions</label>
								<span class="output" id="output__Q">${totalQuestions}</span>
							</li>

							<li>
								<label for="slider_n">n: Number of participants</label>
								<span class="output" id="output_n">${expertCount}</span> (currently ${expertCount} participants)
								<div id="slider_n"></div>
							</li>

							<li>
								<label for="slider_C">C: Number of participants per question</label>
								<span class="output" id="output__C"></span> seconds
								<div id="slider_C"></div>
							</li>

							<li>
								<label for="slider_q">q: Number of questions each</label>
								<span class="output" id="output_q"></span>
								<div id="slider_q"></div>
							</li>

							<li>
								<label for="slider_t">t: Estimated time per question</label>
								<span class="output" id="output_t"></span> minutes
								<div id="slider_t"></div>
							</li>

							<li>
								<label for="output__T">T: Estimated total time per participant</label>
								<span class="output" id="output__T"></span> minutes
							</li>
						</ul>
					</form>

				</fieldset>

			</div>
		</div>
	</body>
	
</html>
