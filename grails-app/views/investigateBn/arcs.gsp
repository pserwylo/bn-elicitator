<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<meta name="layout" content="main">
	<title>Investigate BN: Arcs</title>

	<style>

		.quality-bar .outer-bar {
			border: solid 1px rgba(0, 0, 0, 0.30);
			display: inline-block;
			width: 50px;
			height: 10px;
			margin-right: 0.75em;
		}

		.quality-bar .inner-bar {
			background-color: #c5ffc7;
			display: block;
			height: 100%;
		}

		.quality-bar {
			position: relative;
		}

		.quality-bar .value {
			display: none;
			font-size: 0.6em;
			color: #666;
			position: absolute;
			left: 2px;
			top: 2px;
			bottom: 2px;
		}

		.user.hover .quality-bar .value {
			display: inline;
		}

		.didntAnswer .quality-bar .outer-bar {
			border: solid 1px rgba(0, 0, 0, 0.15)
		}

		.didntAnswer .quality-bar .inner-bar {
			background-color: #e0e0e0;
		}

		.didntAnswer .quality-bar .value {
			color: rgba(0, 0, 0, 0.30);
		}

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
			var url = '${createLink( action : 'arcs', params : [ userId : user?.id ?: 0 ] )}';
			document.location = url + '&parentId=' + $( this ).val();
		});

		$( '#userList' ).change(function() {
			var url = '${createLink( action : 'arcs' )}';
			document.location = url + '?userId=' + $( this ).val();
		});

	</g:javascript>
</head>

<body>
	<h1>Investigate BN: Arcs</h1>
	<fieldset class="default">
		<legend>Specify arc to investigate</legend>

		<div class="userContainer">
			<label>
				Relationships assigned to:
				<g:select
					id="userList"
					from="${users.collect { "User $it.id" } }"
					keys="${users.collect { it.id } }"
					name="user"
					noSelection="${ [ 0 : "All users" ] }"
					value="${user ? user.id : 0}" />
			</label>
		</div>

		<div id="parentSelection" class="variableContainer">
			<label>
				Parent node:
				<bnInvestigate:parentVariableList id="parentList" name="parentList" selectedId="${parent ? parent.id : null}" assignedToUser="${user}" />
			</label>
		</div>

		<div id="childSelection" class="variableContainer">

			<g:if test="${parent}">
				<g:render template="arcChildren" model="${[ parent : parent, child : child, user : user ]}" />
			</g:if>

		</div>
	</fieldset>

	<div id="arcSummary">
		<g:if test="${parent && child}">
			<g:if test="${children?.size() > 0}">
				<g:render template="arcSummary" model="${[ parent : parent, child : child, children : children, user : user ]}" />
			</g:if>
			<g:else>
				<g:render template="arcSummaryNoChildren" model="${[ variable : child, user : user ]}" />
			</g:else>
		</g:if>
	</div>

</body>
</html>