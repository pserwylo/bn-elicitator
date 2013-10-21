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
		<title>Identify likelihood of scenarios</title>
		<r:require module="elicitList" />
	</head>

	<body>

		<help:help index="1" uniqueId="probability-index-completing-survey" targetId="first-variable" title="Completing the survey">
			<p>The survey will be complete when you have visited each of these links and answered their questions. The <img src='${resource(dir: 'images/icons', file: 'lightbulb.png')}' /> will change to a <img src='${resource(dir: 'images/icons', file: 'accept.png')}' /> to indicate it has been completed.</p>
		</help:help>

		<g:if test="${flash.message}">
			<div class="message">
				${flash.message}
			</div>
		</g:if>

		<das2004:listSummaryProbabilities />
	</body>
</html>
