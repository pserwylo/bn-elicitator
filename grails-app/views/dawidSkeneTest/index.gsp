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
		<title>Analyse BNs</title>
		<r:require module="analysis" />
	</head>

	<body>

        <h1>Analyse Bayesian Networks</h1>
    
        <input type="button" value="Conduct new analysis" onclick="document.location = '${createLink( action : 'startAnalysis' )}'"/>
    
        <fieldset class="default">
            <legend>Previous analysis</legend>
        </fieldset>
    
	</body>
</html>
