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
		<title>Analysis of ${run}</title>
		<r:require module="analysis" />
	</head>

	<body>

        <fieldset class="default">
            <legend>View CPTs of ${run}</legend>

            <g:link action="normalize" params="${ [ runId : run.id ] }" >Normalize all CPTs for this run</g:link>
            
            <table class="summary">
                <tr>
                    <th>Node</th>
                    <th>Parents</th>
                    <th>CPT</th>
                </tr>
                <g:each in="${run.cpts.sort { it1, it2 -> it1?.variable?.label <=> it2?.variable?.label }}" var="cpt">
                    <tr>
                        <td class="main">${cpt.variable ? cpt.variable.label : "<em>Unknown</em>"}</td>
                        <td>${cpt.probabilities?.size() > 0 && cpt.probabilities[ 0 ].parentStates.size() > 0 ? cpt.probabilities[ 0 ].parentStates*.variable*.label.join( ", ") : "<em>No parents</em>"}</td>
                        <td>
                            <ul>
                                <g:each in="${cpt.probabilities}" var="prob">
                                    <li>${prob}</li>
                                </g:each>
                            </ul>
                        </td>
                    </tr>
                </g:each>
            </table>

        </fieldset>
    
	</body>
</html>
