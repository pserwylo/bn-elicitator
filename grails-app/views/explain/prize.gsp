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
<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
<head>
	<meta name="layout" content="main">
	<title>Prize draw</title>
	<r:require module="explain" />
</head>

<body>

<div id="prize-consent" class="explain-content">

	<h1>Prizes</h1>

	<h3>
		After the final round of this survey is complete, a prize of $100 will be awarded to one participant who
		completed the survey.
	</h3>

	<p>
		The winner will be drawn randomly from all participants who completed every round of
		the survey. Participants who withdrew before the end of the survey will not be eligible.
	</p>

	<p>
		The winner will be contacted via the email address used to signup (or the email used to register their
		Facebook account – if they signed up for the survey using Facebook). If we are unable to contact the winner
		via this email after 1 month, they will forfeit the prize and a new winner will be drawn.
	</p>

	<p>
		Your contact email is <strong>${user.email}</strong>. If you would like to change it, please contact
		<a href="mailto:${adminEmail}">${adminEmail}</a>.
	</p>

	<p>
		To ensure transparency, the winners name will be displayed at this website after they have been awarded their
		prize. To be eligible for the prize, you will need to consent to your name being displayed if you are drawn
		as the winner.
	</p>

	<br />

	<g:form action="consentPrizes">
		<div class="consent-checkbox">
			<input id="consent" type="checkbox" name="consent" value="1" checked="checked"/>
			<label for="consent">
				I understand the conditions above and wish to eligible to win the $100 prize.
				<br />
				<span class="smaller">
					(You are still able to continue with this survey without being eligible for a prize).
				</span>
			</label>
		</div>

		<br />
		<br />

		<input id="continue" type="submit" class="big" value="Continue" />
	</g:form>


</div>

</body>
</html>