<head>
<meta name='layout' content='main' />
	<title><g:message code="springSecurity.denied.title" /></title>
	<g:javascript>
		$( document ).ready( function() {
			$( '#btnBack').click( function() {
				document.location = '${createLink( controller: 'elicit' )}';
			})
		});
	</g:javascript>
</head>

<body>
<div class='body'>
	<div class='errors'><g:message code="springSecurity.denied.message" /></div>
	<button class="big" id="btnBack">Back</button>

	<g:set var="props" value="${bn.elicitator.AppProperties.properties}" />
	<a style="margin-left: 1em;" href="mailto:${props.adminEmail}?subject=${props.title.encodeAsHTML()}: Access denied">
		Contact administrator
	</a>
</div>
</body>
