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
<%@ page import="java.text.DecimalFormat" %>
<!doctype html>

<html>
	<head>
		<meta name="layout" content="main">
		<title>Verifying weighted sum calculations</title>
		<r:require module="analysis" />
	</head>

	<body>

        <fieldset class="default">
            <legend>Weighted sum calculations vs Explicit elicitations</legend>

            <table class="summary">
                <tr>
                    <th>Probability</th>
                    <th>User</th>
                    <th>Explicit elicitation</th>
                    <th>Weighted Sum Calculation</th>
                </tr>
                <g:set var="formatter" value="${new DecimalFormat('0.000')}" />
                <g:each in="${results}" var="entry">
                    <g:each in="${entry}" var="pair">
                        <tr>
                            <td class="main">${pair.elicited.toStringWithoutValue()}</td>
                            <td>User ${pair.elicited.createdById}</td>
                            <td>${formatter.format(pair.elicited.probability)}</td>
                            <td>${formatter.format(pair.estimated.probability)}</td>
                        </tr>
                    </g:each>
                </g:each>
            </table>

        </fieldset>
    
	</body>
</html>
