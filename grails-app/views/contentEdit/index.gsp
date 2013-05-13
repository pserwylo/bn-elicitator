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
<html>

	<head>
		<meta name="layout" content="main">
		<title>Content pages</title>

		<style type="text/css">
			.column-left {
				width: 40%;
			}
			.column-right {
				margin-left: 42%;
			}
		</style>

		<g:javascript>
			$(document).ready( function() {

				$( '#new-page' ).click( function() {
					document.location = "${createLink(action: "create")}";
				});

			});

			function editPage( id ) {
				document.location = "${createLink(action: "edit")}/" + id;
			}

			function deletePage( id, label ) {
				var message = "Are you sure you want to delete:\n\n  " + label + "?";
				if ( confirm( message ) ) {
					document.location = "${createLink(action: "delete")}/" + id;
				}
			}
		</g:javascript>

		<r:require module="contentEdit" />
		
	</head>
	
	<body>
	
		<h1>Content pages</h1>

		<button id="new-page">New page</button>
		<br />

		<div class="list-pages">
			<div class="column-wrapper">
				<div class="column-header"></div>
				<div class="column-left">

					<fieldset class="default ">
						<legend>Pages</legend>

						<ul class="variable-list ">
							<g:each var="page" in="${pages}">

								<li id="page-${page.id}" class="variable-item">
									<span class="actions">
										<button class="delete" onclick="deletePage( ${page.id}, '${page.label.encodeAsJavaScript()}' );">Delete</button>
										<button class="edit" onclick="editPage( ${page.id} );">Edit</button>
									</span>
									<div class="label">${page.label}</div>
								</li>

							</g:each>
						</ul>

					</fieldset>

				</div>
			</div>
		</div>
	</body>
	
</html>
