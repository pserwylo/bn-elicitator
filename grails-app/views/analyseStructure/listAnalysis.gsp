%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 2015 Peter Serwylo (peter.serwylo@monash.edu)
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
		<title>All Past Analysis</title>
		<r:require module="analysis" />
	</head>

	<body>

        <fieldset class="default">
            <legend>All Past Analysis</legend>
            <ul class="variable-list">
                <g:each in="${suites.reverse()}" var="suite">
                    <li class="variable-item">
                        <g:link action="showAnalysis" params="${ [ id : suite.id ] }">View Analysis ${suite.id}</g:link>
                        (${suite.createdDate})
                    </li>
                </g:each>
            </ul>

        </fieldset>
    
	</body>
</html>
