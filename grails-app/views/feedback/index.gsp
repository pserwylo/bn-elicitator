<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>Feedback</title>
	<meta name="layout" content="main">

	<r:require module="feedback" />

	<g:javascript>
		$( document ).ready( function() {

			var dependsOn = ${questions.findAll { it.dependsOn != null }.collect { [ it.id, it.dependsOn.id ] } as grails.converters.JSON};

			var getOptionId = function( radio ) {
				var domId    = $( radio ).attr( 'id' );
				var parts    = domId.split( '-' );
				return ( parts.length == 2 ) ? parseInt( parts[ 1 ] ) : 0;
			};

			var getDependentQuestionId = function( optionId ) {
				var question = null;
				if ( optionId > 0 ) {
					for ( var i = 0; i < dependsOn.length; i ++ ) {
						var dependentQuestionId = dependsOn[ i ][ 0 ];
						var dependentOnOptionId = dependsOn[ i ][ 1 ];
						if ( dependentOnOptionId == optionId ) {
							question = $( '#question-' + dependentQuestionId );
							if ( question.length == 0 ) {
								question = null;
							}
							break;
						}
					}
				}
				return question;
			};

			$( '.response.options' ).buttonset().change( function( item ) {
				var selected          = $( item.target );
				var dependentQuestion = getDependentQuestionId( getOptionId( selected ) );
				if ( dependentQuestion != null ) {
					dependentQuestion.show( 'fast' );
				}

				var others = selected.closest( '.response' ).find( 'input[type=radio]:not( :checked )' );
				others.each( function( i, otherRadio ) {
					var nonDependentQuestion = getDependentQuestionId( getOptionId( otherRadio ) );
					if ( nonDependentQuestion != null ) {
						nonDependentQuestion.hide( 'fast' );
					}
				});
			});
		});
	</g:javascript>

	<style>
		.conditional {
			display: none;
		}
	</style>

</head>
<body>

	<fieldset class="default">
		<legend>Feedback</legend>
		<g:form action="save">
			<bnFeedback:survey questions="${questions}" />
			<button id="btnDone" class="big">Done</button>
		</g:form>
	</fieldset>

</body>
</html>