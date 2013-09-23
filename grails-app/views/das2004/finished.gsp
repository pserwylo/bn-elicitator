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
		<title>${bn.elicitator.AppProperties.properties.title}</title>
		<r:require module="elicitList" />
	</head>
	
	<body>
		<div class="message">
			<g:message code='elicit.probabilities.completed-all' />
			<g:if test="${user.canWinPrize()}">
				<g:message code='elicit.probabilities.completed-all.in-prize-draw' />
			</g:if>
		</div>
	</body>
</html>
