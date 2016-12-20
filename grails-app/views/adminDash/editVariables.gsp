%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 2016 Peter Serwylo (peter.serwylo@monash.edu)
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
		<title>Admin Dashboard - Variables</title>

		<r:require module="adminVariables" />

	</head>
	
	<body>

        <g:if test="${refreshedQuestions}">
            <div class="message">
                <p><strong>Deleted all survey responses and reallocated questions.</strong></p>
                <p>
                    Due to adding a variable or changing the class a variable belonged to, all of the questions in the
                    survey have been reset and will need to be answered again.
                </p>
            </div>
        </g:if>

        <div class="errors">
            <p><strong>Editing variable may reset all survey responses.</strong></p>
            <p>
                If you add a new variable of change the class a variable belongs, then the survey questions will need to be regenerated.
                <em>This means that questions which have already been answered will need to be answered by experts again.</em>
            </p>
            <p>
                Changing the label, description, or usage description of a variable will <em>not</em> require questiosn to be answered again.
            </p>
        </div>

		<fieldset class="default">
			<legend>Variables</legend>

            <ul id="variable-list" class="variable-list">
                <g:each in="${variables}" var="variable">

                    <li class="variable-item">
                        <g:link action="editVariable" params="${['id' : variable.id]}">Edit ${variable.readableLabel}</g:link>
                    </li>

                </g:each>
            </ul>

            <button class="add-button big" onclick="document.location = '${createLink(action: 'addVariable')}'">Add new variable</button>

        </fieldset>

        <button class="back-button" onclick="document.location = '${createLink(action: 'index')}'">Back</button>

    </body>
	
</html>
