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
			var checkbox    = form.find( 'input:checkbox' );
			var textarea    = form.find( 'textarea' );
			var reasons     = form.find( '.reasons' );
			var reasonsList = reasons.find( '.reasons-list' );

			var currentVar = null;

			var reviewYes = $( '.review-yes' );
			var reviewNo  = $( '.review-no'  );

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
				var changeExists  = currentVar.exists  != checkbox.prop( 'checked' );
				var changeComment = $.trim( currentVar.comment ) != $.trim( textarea.val() );
				return changeExists || changeComment;
			};

			var hasChangedWithAlert = function() {
				var changed = hasChanged();
				if ( changed ) {
					alert( "You have unsaved changes. Please save these changes first (click the save button on the right)." );
				}
				return changed;
			};

			var generateComment = function( comment ) {
				var delphiPhase = comment.delphiPhase;
				var author      = comment.byMe ? "Myself" : "Other participant";
				var classes     = [ 'phase-' + delphiPhase ];
				classes.push( comment.byMe   ? 'me' : 'other' );
				classes.push( comment.exists ? 'exists' : 'doesnt-exist' );
				return '<li class="' + classes.join( ' ' ) + '">' + comment.comment + '<div class="author">- ' + author + '</div></li>';
			};

			$( '#btnFinished' ).click( function() {
				if ( !hasChangedWithAlert() ) {
					document.location = '${createLink( action: 'completedVariable', params: [ variable : variable.label ])}';
				}
			});

			showHideLists();

			form.find( 'button.save' ).click( function() {
				var btnSave = this;
				if ( hasChanged() ) {
					$.ajax({
						type: 'post',
						url: '${createLink(action: 'save')}',
						data: {
							child: '${variable.label}',
							parent: currentVar.label,
							comment: $.trim( textarea.val() ),
							exists: checkbox.prop( 'checked' )
						},
						dataType: 'text json',
						error: function( data ) {
							alert( "Error while saving. The administrator has been notified." );
						},
						success: function( data ) {

							if ( data.exists != currentVar.exists ) {
								// TODO: Update the other user count and style.
								var li = $( '#' + currentVar.label + "-variable-item" );
								li.detach();

								var ul = $( '#list-' + ( data.exists ? 'yes' : 'no' ) );
								ul.append( li );

								showHideLists();
							}

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
						}
					});
				}
			});

			form.find( 'button.close' ).click( function() {
				if ( !hasChangedWithAlert() ) {
					form.hide();
				}
			});

			$( 'button.review' ).click( function() {
				var btnReview = this;

				if ( hasChangedWithAlert() ) {
					return;
				}

				checkbox.prop( 'checked', false );
				textarea.val( "" );

				if ( currentVar != null ) {
					$( '#' + currentVar.label + "-variable-item" ).removeClass( 'highlighted' );
				}

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
						checkbox.prop( 'checked', data.exists );
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

						if ( currentVar != null ) {
							$( '#' + currentVar.label + "-variable-item" ).addClass( 'highlighted' );
						}

						form.find( 'legend' ).html( "Does " + data.parentLabelReadable + "<br />directly influence<br />${variable.readableLabel}?" );

						var offset = $( btnReview ).closest( 'li' ).offset().top - form.parent().offset().top - 50;
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

						<bnElicit:potentialParentsList potentialParents="${potentialParents}" child="${variable}"/>

					</fieldset>

					<input id="btnFinished"
						   type="button" style="margin-top: 5px;" value="Finished with ${variable.readableLabel}" class="big" />

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
