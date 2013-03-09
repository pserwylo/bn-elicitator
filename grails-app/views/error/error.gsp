<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>An error occured</title>
	<meta name="layout" content="main">
	<g:javascript>

		$(document).ready( function() {

			var countdown = $( '#countdown' );

			var count = 21;

			var updateCountdown = function() {
				count --;
				countdown.html( count );
				return count;
			};

			var redirect = function() {
				document.location = "${createLink( controller: 'elicit' )}";
			};

			var step = function() {
				updateCountdown();
				if ( count == 0 ) {
					redirect();
				} else {
					setTimeout( step, 1000 );
				}
			};

			$( '#btnBack' ).click( redirect );

			setTimeout( step, 1000 );

		});

	</g:javascript>

	<style>

		#countdown {
			font-weight: bold;
		}

	</style>

</head>
<body>

	<h1>${error.title}</h1>

	<ul>
		<li class="errors">${error.message}</li>
		<li class="errors">You will be sent back to the main page in <span id="countdown">20</span> seconds.
	</ul>

	<g:if test="${error.exception}">
		<div class="exception" style="display: none;">
			<g:renderException exception="${error.exception}" />
		</div>
	</g:if>

	<button type="button" class="big">Back</button>

</body>
</html>