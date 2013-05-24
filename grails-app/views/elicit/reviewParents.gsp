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
<%@ page import="bn.elicitator.AppProperties; bn.elicitator.Variable; bn.elicitator.Comment" %>
<!doctype html>
<html>

	<head>
		<meta name="layout" content="main">
		<title>Review decisions</title>

		<g:javascript>

		function finish() {
			document.location = '${createLink(action: 'finished')}';
		}

		$( document ).ready( function() {

			var form        = $( '#var-details-dialog' );
			var textarea    = form.find( 'textarea' );
			var reasons     = form.find( '.reasons' );
			var reasonsList = reasons.find( '.reasons-list' );

			var currentVar = null;

			var reviewYes     = $( '.review-yes' );
			var reviewNo      = $( '.review-no'  );
			var buttonBack    = $( '#btnBack' );
			var reviewedVars  = ${reviewedVariables*.label as grails.converters.JSON};
			var TOTAL_REVIEWS = $( '#list-yes' ).children().length + $( '#list-no' ).children().length;

			var canFinish = function() {
				return reviewedVars.length == TOTAL_REVIEWS;
			};

			var getReviewsLeft = function() {
				return TOTAL_REVIEWS - reviewedVars.length;
			};

			var hasChangedMind = function() {
				return inputDoesExist() != currentVar.exists;
			};

			var inputDoesExist = function( doesExistValue ) {
				if ( typeof doesExistValue === 'boolean' ) {
					var value = doesExistValue ? 'yes' : 'no';
					form.find( 'input:radio[name=exists][value=' + value + ']' ).prop( 'checked', true );
				} else {
					doesExistValue = form.find( 'input:radio[name=exists]:checked' ).val() == 'yes';
				}
				return doesExistValue;
			};

			var showHideLists = function() {
				if ( reviewYes.find( 'li' ).length == 0 ) {
					reviewYes.hide();
				} else {
					reviewYes.show();
				}

				if ( reviewNo.find( 'li' ).length == 0 ) {
					reviewNo.hide();
				} else {
					reviewNo.show();
				}
			};

			var hasChanged = function() {
				if ( currentVar == null ) {
					return false;
				}
				var changeComment = $.trim( currentVar.comment ) != $.trim( textarea.val() );
				return hasChangedMind() || changeComment;
			};

			var hasChangedWithAlert = function() {
				var changed = hasChanged();
				if ( changed ) {
					alert( "You have unsaved changes. Please save these changes first (click the \"Save/Done\" button on the right)." );
				}
				return changed;
			};

			var generateComment = function( comment ) {
				var delphiPhase = comment.delphiPhase;
				var author      = comment.byMe ? "Myself" : "Other participant";
				var classes     = [ 'phase-' + delphiPhase ];
				classes.push( comment.byMe   ? 'me' : 'other' );
				classes.push( comment.exists ? 'exists' : 'doesnt-exist' );
				var authorString = '${message( code: 'elicit.parents.comment-phase', args: [ '[me]', '[phase]' ])}'.replace( '[phase]', delphiPhase ).replace( '[me]', author );
				return '<li class="' + classes.join( ' ' ) + '">' + comment.comment + '<div class="author">' + authorString + '</div></li>';
			};

			var closeDialog = function() {
				form.hide();
				deselect();
				currentVar = null;
			};

			$( '#btnFinished' ).click( function() {
				if ( !canFinish() ) {
					var count = getReviewsLeft();
					alert( 'You still need to review ' + count + ' variables.' );
					$( 'body' ).animate( { scrollTop: 0 } );
				} else if ( !hasChangedWithAlert() ) {
					document.location = '${createLink( action: 'completedVariable', params: [ variable : variable.label ])}';
				}
			});

			var markAsReviewed = function( varLabel ) {
				if ( reviewedVars.indexOf( varLabel ) == -1 ) {
					reviewedVars.push( varLabel );
				}

				if ( canFinish() ) {
					buttonBack.hide();
				}
			};

			showHideLists();

			form.find( 'button.save' ).click( function() {
				var btnSave = this;
				if ( !hasChanged() ) {
					return;
				}

				var comment = $.trim( textarea.val() );
				if ( hasChangedMind() && comment.length == 0 ) {
					alert( "You must write a comment about why you changed your mind." );
					return;
				}

				$( btnSave ).attr( 'disabled', true );

				$.ajax({
					type: 'post',
					url: '${createLink(action: 'save')}',
					data: {
						child: '${variable.label}',
						parent: currentVar.label,
						comment: comment,
						exists: inputDoesExist()
					},
					dataType: 'text json',
					error: function( data ) {
						$( btnSave ).attr( 'disabled', false );
						alert( "Error while saving. The administrator has been notified." );
					},
					success: function( data ) {
						$( btnSave ).attr( 'disabled', false );
						if ( data.exists != currentVar.exists ) {
							// TODO: Update the other user count and style.
							var li = $( '#' + currentVar.label + "-variable-item" );
							li.detach();

							var ul = $( '#list-' + ( data.exists ? 'yes' : 'no' ) );
							ul.append( li );
							showHideLists();
						}

						markAsReviewed( currentVar.label );

						reasonsList.find( 'li.me.phase-${delphiPhase}' ).remove();
						currentVar.exists  = data.exists;
						currentVar.comment = data.comment;
						if ( data.comment.length > 0 ) {
							var comment = {
								byMe        : true,
								comment     : data.comment,
								exists      : data.exists,
								delphiPhase : ${delphiPhase}
							};
							reasonsList.prepend( generateComment( comment ) );
						}

						$( '#' + currentVar.label + "-variable-item" ).addClass( 'doesnt-need-review' ).removeClass( 'needs-review' );
					}
				});
			});

			form.find( 'button.close' ).click( function() {
				if ( !hasChangedWithAlert() ) {
					closeDialog();
				}
			});

			var deselect = function() {
				inputDoesExist( false );
				textarea.val( "" );
				if ( currentVar != null ) {
					$( '#' + currentVar.label + "-variable-item" ).removeClass( 'highlighted' );
				}
			};

			buttonBack.click( function() {
				document.location = '${createLink( action : 'redirectToProblems' )}';
			});

			if ( canFinish() ) {
				buttonBack.hide();
			}

			$( 'button.review' ).click( function() {
				var btnReview = this;

				if ( hasChangedWithAlert() ) {
					return;
				}

				deselect();

				$.ajax({
					type: 'post',
					url: '<g:createLink action='ajaxGetReviewDetails'/>',
					data: {
						child: '${variable.label}',
						parent: $( btnReview ).val()
					},
					dataType: 'text json',
					error: function( data ) {
						alert( "Error while loading details. The administrator has been notified." );
					},
					success: function( data ) {

						reasons.find( '.no-reasons' ).remove();
						reasonsList.children().remove();
						inputDoesExist( data.exists );
						var currentCommentText = null;

						if ( data.comments.length == 0 ) {
							reasons.append( '<div class="no-reasons">No reasons given.</div>' );
						} else {
							for ( var i = 0; i < data.comments.length; i ++ ) {
								var comment = data.comments[ i ];
								reasonsList.append( generateComment( comment ) );
								if ( comment.delphiPhase == ${delphiPhase} && comment.byMe ) {
									currentCommentText = comment.comment;
									textarea.val( currentCommentText );
								}
							}
						}

						currentVar = {
							label         : $( btnReview ).val(),
							readableLabel : data.parentLabelReadable,
							exists        : data.exists,
							comment       : currentCommentText == null ? "" : currentCommentText
						};

						$( '#' + currentVar.label + "-variable-item" ).addClass( 'highlighted' );

						form.find( 'legend' ).html( 'Does ' + data.parentLabelReadable + '<br />directly influence<br />${variable.readableLabel.encodeAsJavaScript()}?' );

						var offset = $( btnReview ).closest( 'li' ).offset().top - form.parent().offset().top - 250;
						offset = Math.max( 0, offset );
						form.show();
						form.css( 'padding-top', offset + 'px' );
					}
				});
			});
		});

		</g:javascript>

		<r:require module="elicitParents"/>

	</head>

	<body>

		<div class="elicit-parents">

			<div class="column-wrapper">

				<div class="column-left">

					<fieldset class="default">

						<legend>
							${variable.readableLabel} <bn:variableDescription var="${variable}"/>
						</legend>
						<p>
							<g:message code="elicit.parents.review.desc" args="${[variable.readableLabel]}"/>
						</p>
						<br/>

						<bnElicit:potentialParentsListLaterRounds potentialParents="${potentialParents}" child="${variable}"/>

					</fieldset>

					<div class="button-wrapper">
						<button
							id="btnFinished"
							type="button"
							style="margin-top: 5px;"
							class="big">
							Finished with ${variable.readableLabel}
						</button>

						<button id="btnBack" type="button">
							Back
						</button>
					</div>

				</div>

				<div class="column-right">

					<div id="var-details-dialog" class="" style="display: none;">

						<fieldset class="default ">

							<legend class="">Details</legend>

							<div class='contents'>

								<bnElicit:potentialParentDialog/>

							</div>

						</fieldset>

					</div>

				</div>

				<div class="column-footer">

				</div>

			</div>

		</div>

	</body>

</html>
