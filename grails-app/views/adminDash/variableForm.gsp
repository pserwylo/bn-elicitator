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
		<title>Admin Dashboard - Edit Variable</title>

		<r:require module="adminVariables" />

	</head>
	
	<body>

		<fieldset class="default">
			<legend>
                ${variableId ? "Edit ${variable.label}" : "New variable"}
            </legend>

            <g:form action="saveVariable">

                <g:if test="${variableId}">
                    <input type="hidden" name="id" value="${variable.id}" />
                </g:if>
                <g:else>
                    <input type="hidden" name="add" value="true" />
                </g:else>

                <ul id="variable-form">

                    <li class="input">

                        <label class="input-label" for="var-label">
                            Readable Label
                            <bn:tooltip>Displayed to the user when they view a list of variables assigned to them"</bn:tooltip>
                        </label>
                        <div class="input-value">
                            <input id="var-label" type="text" name="readableLabel" value="${variable.readableLabel}" />
                        </div>

                    </li>

                    <li class="input">

                        <label class="input-label" for="var-usage-description">
                            Usage Description
                            <bn:tooltip></bn:tooltip>
                        </label>
                        <div class="input-value">
                            <textarea id="var-usage-description" name="usageDescription">${variable.usageDescription}</textarea>
                            <div class="message">
                                The "usage description" is used when displaying a list of survey questions to the user.
                                This variable is displayed at the top, followed by its usage description, and then the
                                list of all other variables. It should be something along the lines of:

                                <div class="quote">
                                    "Does the variable ${variable.readableLabel} directly influence any of these?"
                                </div>
                            </div>
                        </div>

                    </li>

                    <li class="input">

                        <label class="input-label" for="var-description">
                            Description
                            <bn:tooltip>If this is not empty, it will be shown as a tooltip next to each variable when it is shown to the user, much like the tooltip you are reading now.</bn:tooltip>
                        </label>
                        <div class="input-value">
                            <textarea id="var-description" name="description">${variable.description}</textarea>
                        </div>

                    </li>

                    <li class="input">

                        <label class="input-label">
                            Variable Class
                            <bn:tooltip></bn:tooltip>
                        </label>

                        <div class="input-value">
                            <ul class="variable-classes">
                                <g:each in="${variableClasses}" var="variableClass">
                                    <li class="variable-class">
                                        <label>
                                            <input type="radio" name="variableClass" value="${variableClass.id}" ${variable.variableClass == null || variable.variableClass?.id == variableClass.id ? 'checked="checked"' : ''}/>
                                            ${variableClass.niceName}
                                            <span class="can-influence">
                                                (can influence ${variableClass.potentialChildren*.niceName.join(', ')})
                                            </span>
                                        </label>
                                    </li>
                                </g:each>
                            </ul>
                        </div>

                    </li>

                </ul>

                <input type="submit" value="cancel" />
                <input type="submit" class="big" value="Save" />

            </g:form>
            
		</fieldset>

    </body>
	
</html>
