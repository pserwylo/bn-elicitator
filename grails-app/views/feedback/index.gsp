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

			var getOptionIds = function( question ) {
				var optionIds = [];
				question.find( '.response.options' ).each( function( i, item ) {
					optionIds.push( getOptionId( $( item ).find( 'input[ type=radio ]' ) ) );
				});
				return optionIds;
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

			var hideQuestionAndDependents = function( optionId ) {
				var nonDependentQuestion = getDependentQuestionId( optionId );
				if ( nonDependentQuestion != null ) {
					nonDependentQuestion.hide( 'fast' );
					var options = getOptionIds( nonDependentQuestion );
					if ( options.length > 0 ) {
						for ( var i = 0; i < options.length; i ++ ) {
							hideQuestionAndDependents( options[ i ] );
						}
					}
				}
			};

			$( '.response.options' ).buttonset().change( function( item ) {
				var selected          = $( item.target );
				var dependentQuestion = getDependentQuestionId( getOptionId( selected ) );
				if ( dependentQuestion != null ) {
					dependentQuestion.show( 'fast' );
				}

				var others = selected.closest( '.response' ).find( 'input[type=radio]:not( :checked )' );
				others.each( function( i, otherRadio ) {
					hideQuestionAndDependents( getOptionId( otherRadio ) )
				});
			});

			$( '.ui-buttonset:visible' ).each( function( i, item ) {
				bn.utils.equalizeHeights( $( item ).find( '.ui-button' ) );
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
			<input type="submit" id="btnDone" class="big" value="Done" />
		</g:form>
	</fieldset>

</body>
</html>