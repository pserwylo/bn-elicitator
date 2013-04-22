%{--
  - Bayesian Network (BN) Elicitator
  - Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
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
<g:set var="title" value="${page ? "Edit page \"$page.label\"" : "New page"}" />
<html>

	<head>
		<meta name="layout" content="main">
		<title>${title}</title>
		<r:require module="contentEdit" />
		<ckeditor:resources />

		<ckeditor:config var="toolbar_CustomToolbar">
			[
				["Source"],
				["Cut","Copy","Paste"],
				["Undo","Redo","-","Find","Replace","-","RemoveFormat"],
				["Link","Unlink","Anchor"],
				["Image","Table","SpecialChar"],
				"\/",
				["Format","FontSize","TextColor","BGColor","Blockquote"],
				["Bold","Italic","Underline"],
				["NumberedList","BulletedList","-","Outdent","Indent"],
				["JustifyLeft","JustifyCenter","JustifyRight","JustifyBlock"]
			]
		</ckeditor:config>

	</head>
	
	<body>
	
		<h1>${title}</h1>

		<g:form action="save">
			<fieldset class="default ">
				<legend>Details</legend>

				<g:if test="${page?.id}">
					<input type="hidden" name="id" value="${page.id}" />
				</g:if>

				<div class="input">
					<label for="inputLabel">Label:</label>
					<input id="inputLabel" type="text" value="${page?.label ?: ""}" name="label" />
				</div>

				<div class="input">
					<label for="inputAlias"> URL Alias:</label>
					<input id="inputAlias" type="text" value="${page?.alias ?: ""}" name="alias" />
					<div class="info">
						i.e. "http://example.com/content/[alias]"
					</div>
				</div>

				<ckeditor:editor
					name="content"
					height="640px" width="95%"
					toolbar="CustomToolbar">${page?.content ?: ""}</ckeditor:editor>

				<button onclick="document.location = '${createLink( controller: 'contentEdit' )}';">Cancel</button>
				<button type="submit">Save</button>
			</fieldset>
		</g:form>

	</body>
	
</html>
