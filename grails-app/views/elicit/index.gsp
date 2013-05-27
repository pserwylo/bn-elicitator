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

<g:set var="hasReviewedSome" value="${variables.size() > stillToVisit?.size()}" />
<g:set var="canFinish" value="${!completed && stillToVisit.size() == 0}" />

<html>

	<head>
		<meta name="layout" content="main">
		<title>Identify relationships between variables</title>
		<r:require module="elicitList" />
	</head>
	
	<body>

		<bnIcons:key>

			<bnIcons:icon
				label="${message( code: "icon-key.needs-review.label")}"
				iconPath="${resource([ dir: "images/icons/", file: "lightbulb.png" ])}"><g:message code="icon-key.needs-review" /></bnIcons:icon>

			<bnIcons:icon
				label="${message( code: "icon-key.doesnt-need-review.label")}"
				iconPath="${resource([ dir: "images/icons/", file: "accept.png" ])}"
				display="${hasReviewedSome}"><g:message code="icon-key.doesnt-need-review" /></bnIcons:icon>

		</bnIcons:key>

		<g:if test="${!hasPreviousPhase}">

			<bn:listSummaryFirstPhase variables="${variables}" stillToVisit="${stillToVisit}"/>

		</g:if>
		<g:else>

			<div class="message" style="clear: both;">
				<strong>What's different from last time?</strong>
				<p>
					Although this page looks the same as last time, if you click on the variables below, you will be
					asked to review your answers in light of comments made by other participants.
				</p>
			</div>
			<bn:listSummary variables="${variables}" stillToVisit="${stillToVisit}"/>

		</g:else>

		<g:if test="${completed}">
			<div class="message">
				<ul>
					<li>
						<g:message code="elicit.list.info.round-complete" args="${[user.email]}" />
					</li>
				</ul>
			</div>
		</g:if>
		<g:elseif test="${stillToVisit.size() > 0}">
			<div class="message">
				<ul>
					<li>
						<g:message code="elicit.list.info.round-incomplete" />
					</li>
				</ul>
			</div>
		</g:elseif>
		<g:else>
			<button
				type="button"
				onclick="document.location = '${createLink( action: 'completed' )}'"
				class='big '
				${stillToVisit.size() > 0 ? 'disabled="disabled"' : ''}>
				<g:message code="elicit.list.finish-round" />
			</button>
		</g:else>
	</body>
</html>
