<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta name="layout" content="main">
	<title>Investigate BN: Arcs</title>

	<style>
		.variableContainer {
			padding: 0.3em;
		}

		.user .name {
			font-weight: bold;
		}

		.user {
			padding: 5px;
			border-radius: 0.5em;
		}

		.user.hover {
			padding: 4px;
			background-color: #e6e5ff;
			border: solid 1px darkgray;
		}

		.user.didntAnswer.hover {
			background-color: white;
			border: solid 1px darkgray;
		}

		.user.answered.exists .name {
			color: green;
		}

		.user.didntAnswer .name,
		.user.didntAnswer .comment {
			color: lightgrey;
		}

		.user.answered.doesntExist .name {
			color: red;
		}

		.user.answered .comment.noComment {
			color: grey;
		}
	</style>

	<g:javascript>
		$( '#parentList' ).change(function() {
			var url = '${createLink(action: 'arcs')}';
			document.location = url + '?parentId=' + $( this ).val();
		});
	</g:javascript>
</head>

<body>
	<h1>Investigate BN: Arcs</h1>
	<fieldset class="default">
		<legend>Specify arc to investigate</legend>
		<div id="parentSelection" class="variableContainer">
			<label>
				Parent node:
				<bnInvestigate:variableList id="parentList" name="parentList" selectedId="${parent ? parent.id : null}" />
			</label>
		</div>

		<div id="childSelection" class="variableContainer">

			<g:if test="${parent}">
				<g:render template="arcChildren" model="${[ parent : parent, child : child ]}" />
			</g:if>

		</div>
	</fieldset>

	<div id="arcSummary">
		<g:if test="${parent && child}">
			<g:if test="${children?.size() > 0}">
				<g:render template="arcSummary" model="${[ parent : parent, child : child, children : children ]}" />
			</g:if>
			<g:else>
				<g:render template="arcSummaryNoChildren" model="${[ variable : child ]}" />
			</g:else>
		</g:if>
	</div>

</body>
</html>