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
		<title>Admin Dashboard</title>

		<r:require module="admin" />

	</head>
	
	<body>

		<h1>Initialize relationships</h1>

		<div class="message">
			<ul>
				<li>Initialized ${count} relationships</li>
			</ul>
		</div>

		<div class="button-container">
			<input type="button" value="Back" class="big" onclick="document.location = '${createLink( action: 'index' )}'"/>
		</div>

	</body>
	
</html>
