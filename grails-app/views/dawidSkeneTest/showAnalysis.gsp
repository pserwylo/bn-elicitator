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
		<title>Analysis</title>
		<r:require module="analysis" />
	</head>

	<body>

        <fieldset class="default">
            <legend>View Bayesian Network Analysis</legend>

            <div class="message" style="clear: both;">
                <strong>Current status: </strong>
                Completed ${analysis.analysisRuns.size()} analysis runs.
            </div>

            <table class="summary">
                <tr>
                    <th rowspan="2">Analysis</th>
                    <th rowspan="2">Original<br /> arcs</th>
                    <th rowspan="2">Collated<br /> arcs</th>
                    <th rowspan="2">Acyclic<br /> arcs</th>
                    <th colspan="4">SHD</th>
                </tr>
                <tr>
                    <th>+</th>
                    <th>-</th>
                    <th><-></th>
                    <th>Total</th>
                </tr>
                <g:each in="${analysis.analysisRuns}" var="run">
                    <g:set var="shd" value="${goldStandard.calcShd( run.acyclicNetwork )}" />
                    <tr>
                        <td class="main">${run}</td>
                        <td>${run.startingNetwork.arcs.size()}</td>
                        <td>${run.collatedNetwork.arcs.size()}</td>
                        <td>${run.acyclicNetwork.arcs.size()}</td>
                        <td>${shd.added.size()}</td>
                        <td>${shd.removed.size()}</td>
                        <td>${shd.reversed.size()}</td>
                        <td>${shd.shd}</td>
                    </tr>
                </g:each>
            </table>

        </fieldset>
    
	</body>
</html>
