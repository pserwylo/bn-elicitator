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
		<title>Elicit Parents of Variable</title>

		<g:javascript>

			$( document).ready( function() {

				// We are only getting details for the logged in user, so it should be fine to just cache any info we
				// return from the server...
				var cachedDetails = {};

				var listYes           = $( '#list-yes'           );
				var listNo            = $( '#list-no'            );
				var listUninitialized = $( '#list-uninitialized' );
				var commentDialog     = $( '#comment-dialog'     );
				var textAreaLabel     = $( '#textAreaLabel'      );
				var buttonBack        = $( '#btnBack'            );
				var commentInput      = commentDialog.find( 'textarea' );

				var parentLi      = function( child ) { return $( child ).closest( 'li' ); };
				var parentUl      = function( child ) { return $( child ).closest( 'ul' ); };
				var belongsToList = function( item, list ) { return parentUl( $( item ) ).attr( 'id' ) == $( list ).attr( 'id' ); };

				/**
				 * The currently-being-viewed variable, including its label, readableLabel and comment.
				 */
				var currentVar    = null;

				var getUninitializedCount = function() {
					return listUninitialized.children().length;
				};

				var canFinish = function() {
					return getUninitializedCount() == 0;
				};

				/**
				 * Move a list item from one of our three lists (yes, no, uninitialized) to another.
				 * It does this asynchronisly, so that it can animate the removal and addition.
				 */
				var moveToList = function( li, listDest, callback ) {
					li.hide( 'fast', function() {
						li.detach();
						listDest.append( li );
						showHideLists();
						li.show( 'fast', callback );
					});

					if ( canFinish() ) {
						buttonBack.hide();
					}
				};

				var saveVarDetails = function( parentLabel, exists, comment, callback ) {
					$.ajax({
						type     : 'post',
						url      : '${createLink( action: 'save' )}',
						dataType : 'text json',
						data     : {
							child   : '${variable.label}',
							parent  : parentLabel,
							exists  : exists,
							comment : comment
						},
						error : function( data ) {
							alert( "<g:message code="general.ajax-server-error" />" );
						},
						success : function( data ) {
							var cachedData = getDetailsFromCache( parentLabel );
							if ( cachedData != null ) {
								cachedData.comment = comment;
								updateCache( cachedData );
							}
							callback();
						}
					});
				};

				var updateCache = function( varDetails ) {
					cachedDetails[ varDetails.label ] = varDetails;
				};


				var getDetailsFromCache = function( varLabel ) {
					if ( cachedDetails.hasOwnProperty( varLabel ) ) {
						return cachedDetails[ varLabel ];
					} else {
						return null;
					}
				};


				var loadVarDetails = function( parentLabel, callback ) {
					var cached = getDetailsFromCache( parentLabel );
					if ( cached ) {
						currentVar = cached;
						callback();
					} else {

						$.ajax({
							type : 'get',
							url  : '${createLink(action: 'ajaxGetDetails')}',
							data : {
								child  : '${variable.label}',
								parent : parentLabel
							},
							dataType : 'text json',
							error    : function( data ) {
								alert( "<g:message code="general.ajax-server-error" />" );
							},
							success  : function( data ) {
								currentVar = {
									label         : data.label,
									readableLabel : data.readableLabel,
									comment       : data.comment
								};
								updateCache( currentVar );
								callback();
							}
						});

					}
				};


				var hasChangedComment = function() {
					if ( currentVar == null ) {
						return false;
					}
					return $.trim( currentVar.comment ) != $.trim( commentInput.val() );
				};

				var hasChangedCommentWithAlert = function() {
					var changed = hasChangedComment();
					if ( changed ) {
						alert( "You have an unsaved comment. Please save it before continuing (click the save button on the right)." );
					}
					return changed;
				};

				var isCommentEmpty = function() {
					return $.trim( commentInput.val() ).length == 0;
				};

				var getVarLabelFromLi = function( li ) {
					return $( li ).find( 'input:hidden[name=parent]' ).val();
				};


				var getLiFromVarLabel = function( varLabel ) {
					return $( '#var-' + varLabel );
				};


				var isCurrentVar = function( label ) {
					return currentVar != null && label == currentVar.label;
				};


				var isCurrentLi = function( li ) {
					return isCurrentVar( getVarLabelFromLi( li ) );
				};


				var showDialog = function( dialog, alignWithLi ) {
					unhighlight();
					$( alignWithLi ).addClass( 'highlighted' );
					var offset = $( alignWithLi ).offset().top - dialog.parent().offset().top - 150;
					offset = Math.max( 0, offset );
					dialog.css( 'padding-top', offset + 'px' );
					dialog.show( 'fade' );
				};

				var selectEndOfTextArea = function( textarea ) {
					try {
						var $textarea = $( textarea );
						if ( $textarea.length > 0 ) {

							textarea = $textarea[ 0 ];
							var length = $textarea.val().length;
							if ( textarea.setSelectionRange ) {
								textarea.focus();
								textarea.setSelectionRange( length, length + 1 );
							}
							else if ( textarea.createTextRange ) {
								var range = textarea.createTextRange();
								range.collapse( true );
								range.moveEnd( 'character', length );
								range.moveStart( 'character', length );
								range.select();
							}
						}
					} catch ( error ) {
						// I'm not sure where the exception was, but I was getting:
						// NS_ERROR_FAILURE:
						//   Component returned failure code:
						//     0x80004005 (NS_ERROR_FAILURE) [nsIDOMHTMLTextAreaElement.setSelectionRange]
						// I don't even know what browser it was in,

					}
				};

				var hideDialog = function( dialog, callback ) {
					callback = typeof callback === "undefined" ? $.noop : callback;
					if ( dialog.is( ":visible" ) ) {
						dialog.fadeOut( 100, callback );
					} else {
						callback();
					}
				};

				var showCommentDialog = function( alignWithLi, comment ) {

					var moveAndShowDialog = function() {
						commentInput.val( comment );
						textAreaLabel.html( "Why do you think '" + currentVar.readableLabel + "' influences '${variable.readableLabel.encodeAsJavaScript()}'?" );
						showDialog( commentDialog, alignWithLi );
						selectEndOfTextArea( commentDialog.find( 'textarea' ) )
					};

					if ( commentDialog.is( ":visible" ) ) {
						hideDialog( commentDialog, moveAndShowDialog );
					} else {
						moveAndShowDialog();
					}
				};


				var unhighlight = function() {
					$( listYes ).add( listNo ).add( listUninitialized ).find( 'li.highlighted' ).removeClass( 'highlighted' );
				};


				var closeCommentDialog = function() {
					commentDialog.hide( 'fade', 100 );
					unhighlight();
					currentVar = null;
				};


				var hideIfYesEmpty = $( '.hide-if-yes-empty' );
				var hideIfNoEmpty  = $( '.hide-if-no-empty' );

				var showHideLists = function() {
					if ( listYes.find( 'li' ).length == 0 ) {
						hideIfYesEmpty.hide();
					} else {
						hideIfYesEmpty.show();
					}

					if ( listNo.find( 'li' ).length == 0 ) {
						hideIfNoEmpty.hide();
					} else {
						hideIfNoEmpty.show();
					}

					if ( canFinish() ) {
						$( 'h2.review-uninitialized' ).hide();
						buttonBack.hide();
					} else {
					}
				};


				var buttonsComment      = $( 'button.comment'     );
				var buttonsYes          = $( 'button.yes'         );
				var buttonsNo           = $( 'button.no'          );
				var buttonFinished      = $( '#btnFinished'       );
				var buttonSaveComment   = commentDialog.find( 'button.save'  );
				var buttonCancelComment = commentDialog.find( 'button.close' );


				buttonsYes.click( function() {
					var li = parentLi( this );
					if ( !isCurrentLi( li ) && !hasChangedCommentWithAlert() ) {
						var varLabel = getVarLabelFromLi( li );
						loadVarDetails( varLabel, function() {
							showCommentDialog( li, currentVar.comment );
						});
					}
				});


				buttonSaveComment.click( function() {
					if ( currentVar != null ) {
						if ( isCommentEmpty() ) {
							alert( "You must provide a comment before saving." );
						} else {
							saveVarDetails( currentVar.label, true, $.trim( commentInput.val() ), function() {
								var li = getLiFromVarLabel( currentVar.label );
								if ( !belongsToList( li, listYes ) ) {
									moveToList( li, listYes );
								}
								closeCommentDialog();
							});
						}
					}
				});


				buttonsNo.click( function() {
					if ( belongsToList( this, listNo ) ) {
						return;
					}

					var li = parentLi( this );
					moveToList( li, listNo, $.noop );
					saveVarDetails( getVarLabelFromLi( li ), false, "", $.noop );
				});


				buttonsComment.click( function() {
					var li = parentLi( this );
					if ( belongsToList( this, listYes ) && !isCurrentLi( li ) && !hasChangedCommentWithAlert() ) {
						loadVarDetails( getVarLabelFromLi( li ), function() {
							showCommentDialog( li, currentVar.comment, $.noop );
						})
					}
				});


				buttonCancelComment.click( function() {
					var allowClose = true;

					if ( hasChangedComment() ) {
						allowClose = confirm( "You have an unsaved comment.\nIf you press 'OK', you will discard any changes you made." );
					}

					if ( allowClose ) {
						closeCommentDialog();
					}
				});


				buttonFinished.click( function() {
					if ( !canFinish() ) {
						var count = getUninitializedCount();
						alert( 'You still need to decide whether ' + count + ' variables influence ${variable.readableLabel.encodeAsJavaScript()}.' );
						$( 'body' ).animate( { scrollTop: 0 } );
					} else {
						document.location = '${createLink( action : 'completedVariable', params : [ variable : variable.label ] )}';
					}
				});


				buttonBack.click( function() {
					document.location = '${createLink( action : 'redirectToProblems' )}';
				});


				listUninitialized.find( 'li.exists'       ).detach().appendTo( listYes );
				listUninitialized.find( 'li.doesnt-exist' ).detach().appendTo( listNo  );
				showHideLists();

			});

		</g:javascript>

		<r:require module="elicitParentsFirst" />

	</head>
	
	<body>

		<div class="elicit-parents">

			<div class="column-wrapper">

				<div class="column-left">

					<fieldset class="default">

						<legend>
							${variable.readableLabel} <bn:variableDescription var="${variable}" />
						</legend>

						<p>
							<g:if test="${variable.usageDescription?.length() > 0}">
								${variable.usageDescription.replace( '\n', '<br />' )}
							</g:if>
							<g:else>
								<g:message code="elicit.parents.desc" args="${[variable.readableLabel]}" />
							</g:else>
						</p>
						<br />

						<bnElicit:potentialParentsList potentialParents="${potentialParents}" child="${variable}" />

					</fieldset>

					<div class="button-wrapper">
						<button id="btnFinished" type="button" class="big">
							Finished with ${variable.readableLabel}
						</button>

						<button id="btnBack" type="button">
							Back
						</button>
					</div>

				</div>

				<div class="column-right">

					<div id="comment-dialog" class="floating-dialog" style="display: none;">

						<fieldset class="default ">

							<legend>Comment</legend>

							<label>
								<span id="textAreaLabel"></span>
								<br />
								<textarea name="comment"></textarea>
							</label>

							<bn:saveButtons atTop="false" closeLabel="Cancel" />

						</fieldset>

					</div>

				</div>

				<div class="column-footer">

				</div>

			</div>

		</div>

	</body>
	
</html>
