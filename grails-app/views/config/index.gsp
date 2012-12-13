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
<%@ page import="bn.elicitator.Variable" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Configure</title>
	</head>
	
	<body>
	
		<h1>Configure</h1>
		
		<p>
			During the configure process, you will be asked to <g:link action="uploadOntology">upload an ontology</g:link>
			which will provide us with the variables from the problem domain. Then you will need to <g:link action="selectVariables">
			select the variables</g:link> of interest from the ontology (it is likely that you are only interested in a small subset
			of variables from the ontology).
		</p>
	
	</body>
	
</html>
