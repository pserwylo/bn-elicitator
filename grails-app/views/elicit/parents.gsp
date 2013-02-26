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
				var newVarDialog      = $( '#new-var-dialog'     );
				var textAreaLabel     = $( '#textAreaLabel'      );
				var buttonBack        = $( '#btnBack'            );
				var commentInput      = commentDialog.find( 'textarea' );
				var newVarNameInput   =  newVarDialog.find( 'input:text' );
				var newVarDescInput   =  newVarDialog.find( 'textarea' );
				var newVarClassInput  =  newVarDialog.find( 'input[name=variableClassName], select[name=variableClassName]' );

				var parentLi      = function( child ) { return $( child ).closest( 'li' ); };
				var parentUl      = function( child ) { return $( child ).closest( 'ul' ); };
				var belongsToList = function( item, list ) { return parentUl( $( item ) ).attr( 'id' ) == $( list ).attr( 'id' ); };

				/**
				 * The currently-being-viewed variable, including its label, readableLabel and comment.
				 */
				var currentVar    = null;

				var getUninitializedCount = function() {
				 	// There will always be at least one: the "add a new variable" one.
					return listUninitialized.children().length - 1;
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

				var isNewVarEmpty = function( notifyErrors ) {

					notifyErrors = ( typeof notifyErrors === "undefined" ) ? false : notifyErrors;

					var isEmpty = false;
					var emptyFields = [];

					if ( $.trim( newVarNameInput.val() ) == "" ) {
						isEmpty = true;
						emptyFields.push( "name" );
					}

					if ( $.trim( newVarDescInput.val() ) == "" ) {
						isEmpty = true;
						emptyFields.push( "description" );
					}

					if ( isEmpty && notifyErrors ) {
						alert( "You must enter a " + emptyFields.join( " and " ) + " for the variable before saving." );
					}

					return isEmpty;
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


				var hideDialog = function( dialog, callback ) {
					callback = typeof callback === "undefined" ? $.noop : callback;
					if ( dialog.is( ":visible" ) ) {
						dialog.fadeOut( 100, callback );
					} else {
						callback();
					}
				};


				var showNewVarDialog = function() {
					hideDialog( commentDialog, function() {
						showDialog( newVarDialog, $( '#add-variable-item') );
					});
				};


				var showCommentDialog = function( alignWithLi, comment ) {

					var moveAndShowDialog = function() {
						commentInput.val( comment );
						textAreaLabel.html( "Why do you think '" + currentVar.readableLabel + "' influences '${variable.readableLabel.encodeAsJavaScript()}'?" );
						showDialog( commentDialog, alignWithLi );
					};

					if ( commentDialog.is( ":visible" ) ) {
						hideDialog( commentDialog, moveAndShowDialog );
					} else if ( newVarDialog.is( ":visible" ) ) {
						hideDialog( newVarDialog, moveAndShowDialog );
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
						buttonBack.hide();
					}
				};


				var buttonsComment      = $( 'button.comment'     );
				var buttonsYes          = $( 'button.yes'         );
				var buttonsNo           = $( 'button.no'          );
				var buttonFinished      = $( '#btnFinished'       );
				var addVariableItem     = $( '#add-variable-item' ).find( 'a' );
				var buttonSaveComment   = commentDialog.find( 'button.save'  );
				var buttonCancelComment = commentDialog.find( 'button.close' );
				var buttonSaveVar       =  newVarDialog.find( 'button.save'  );
				var buttonCancelVar     =  newVarDialog.find( 'button.close' );


				addVariableItem.click( function() {
					showNewVarDialog();
				});


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


				buttonSaveVar.click( function() {
					if ( !isNewVarEmpty( true ) ) {
						$( '#newVarForm' ).submit();
					}
				});


				buttonCancelVar.click( function() {
					hideDialog( newVarDialog );
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
					document.location = '${createLink( action : 'index' )}';
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

					<div id="new-var-dialog" class="floating-dialog" style="display: none;">

						<fieldset class="default">

							<legend>Add new variable</legend>
								%{--I guess if we're getting picky, we really shouldn't be here (because we
								can't elicit parents for variables with no potential parents--}%
								<g:if test="${variable.variableClass.potentialParents.size() == 0}">
									<p>
										Sorry, but because ${variable.readableLabel} is a ${variable.variableClass.name}
										variable, we wont be modelling any other variables which influence it.
									</p>
									<button type="button" class="close">Okay</button>
								</g:if>
								<g:else>
									<form id="newVarForm" action="${createLink( [ action: "addVariable" ] )}">
										<input type="hidden" name="returnToVar" value="${variable.label}" />
										%{--We don't need to ask if there is only one possibility --}%
										<g:if test="${variable.variableClass.potentialParents.size() == 1}">
											<label>
												Name:
												<br />
												<input type="text" id="inputNewVariableLabel" name="label" />
											</label>
											<input type='hidden' name='variableClassName' value='${variable.variableClass.potentialParents[ 0 ].name}' />
										</g:if>
										<g:else>
											<label for="newVarClass">Type</label>
											<br />
											<select id="newVarClass" name="variableClassName">
												<g:each var="varClass" in="${variable.variableClass.potentialParents}">
													<option value='${varClass.name}'>${varClass.niceName} variable</option>
												</g:each>
											</select>

											<bn:tooltip>This helps us decide which other variables your new one will be allowed to influence. We will describe them using examples from a model of diagnosing lung cancer:

											- Problem Variables: The variables of interest (e.g. does the patient have cancer?).

											- Background variables: Information available before the problem variables occur (e.g. does the patient smoke?).

											- Symptom variables: Observable consequences of problem variables (e.g. shortness of breath).

											- Mediating variables: unobservable variables which may also cause the same symptoms as the problem variables (e.g. are they asthmatic?). This helps to correctly model the relationship between problem and symptom variables</bn:tooltip>
										</g:else>

										<br />
										<br />
										<label>
											Description:
											<br />
											<textarea id="newVarDescription" name="description"></textarea>
										</label>

										<bn:saveButtons atTop="${false}" closeLabel="Cancel" />
									</form>
								</g:else>

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
