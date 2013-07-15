/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bn.elicitator

import bn.elicitator.auth.User

class ElicitParentsTagLib {

	static namespace = "bnElicit"

	VariableService variableService
	DelphiService   delphiService
	UserService     userService
	AllocateQuestionsService allocateQuestionsService

	/**
	 * @attr var REQUIRED
	 * @attr includeLabel
	 */
	def variableSynonyms = { attrs ->

		Variable var = attrs.var
		boolean includeLabel = attrs.containsKey( 'includeLabel' ) ? attrs.includeLabel : false

		Set<String> synonyms = var.synonyms ?: []

		String output = ""

		if ( !includeLabel )
		{
			// The synonym list includes the readable label, which is not interesting to us here, because we have
			// already rendered it to the screen.
			if ( synonyms.contains( var.readableLabel ) )
			{
				synonyms.remove( var.readableLabel )
			}
		}

		if ( synonyms.size() > 0 )
		{
			output += "<ul class'synonyms'><li>${synonyms.join( '</li><li>' )}</li></ul>"
		}

		out << output

	}

	/**
	 * @attr relationships REQUIRED
	 */
	def reasonsList = { attrs ->

		List<Relationship> relationships = attrs.relationships

		out << """
			<div class='reasons'>
				<span class='header'>
					${message( code: "elicit.parents.reason.header" )} ${img( [ dir: "images/icons", file: "comments.png" ] )}
				</span>
				"""

		boolean hasReasons = false

		out <<  """
			<div  class='list-wrapper'>
				<ul class='reasons-list'>
				"""

		relationships.each {

			if ( it != null ) {

				Comment comment = it.comment
				if ( comment?.comment?.trim()?.size() > 0 )
				{
					hasReasons = true;

					boolean isMine = comment.createdBy == userService.current
					String author = isMine ? "Myself" : "Other participant"
					String className = "phase-" + it.delphiPhase
					className += isMine ? " me" : " other"
					className += it.exists ? " exists" : " doesnt-exist"

					out << """
						<li class='${className}'>
							"${comment.comment}"
							<div class='author'>${message( code: 'elicit.parents.comment-phase', args: [ author, it.delphiPhase ])}</div>
						</li>
						"""
				}
			}
		}

		out << """
				</ul>
			</div>"""

		if ( !hasReasons )
		{
			out << "<div class='no-reasons'>No reasons given.</div>\n"
		}

		out << "</div>"
	}

	/**
	 * Produces three lists:
	 *  - You said yes
	 *  - You said no
	 *  - We *all* said no (which is hidden)
	 * @attr child REQUIRED
	 * @attr potentialParents REQUIRED
	 */
	def potentialParentsListLaterRounds = { attrs ->

		List<Variable> potentialParents = attrs.potentialParents
		Variable child = attrs.child

		List<Variable> listYes           = []
		List<Variable> listNo            = []
		List<Variable> listNoAll         = []
		List<Variable> reviewedVars      = variableService.myReviewedRelationshipsFor( child )*.relationship*.parent
		List<User> usersAllocatedToChild = allocateQuestionsService.getOthersAllocatedTo( child )
		Map<Variable, List<Relationship>> allRelationships = [:]
		Map<Variable, Integer>            allOthersCount   = [:]

		User user = userService.current

		potentialParents.each { parent ->

			List<Relationship> relationships = delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child ).findAll {
				usersAllocatedToChild.contains( it.createdBy ) &&
					// Only include relationships that were from people who actually finished it...
						variableService.hasVisitedAtSomePoint( child, it.createdBy )
			}

			Relationship       myCurrent      = relationships.find    { it.createdBy == user && it.delphiPhase == delphiService.phase }
			Relationship       myPrevious     = relationships.find    { it.createdBy == user && it.delphiPhase == delphiService.previousPhase }
			List<Relationship> othersPrevious = relationships.findAll { it.createdBy != user }
			Relationship       myMostRecent   = myCurrent ?: myPrevious
			Integer            othersCount    = othersPrevious.count { it?.exists }

			allRelationships.put( parent, relationships )
			allOthersCount.put( parent, othersCount )

			if ( myMostRecent?.exists ) {
				listYes.add( parent )
			} else if ( othersCount == 0 ) {
				listNoAll.add( parent )
			} else {
				listNo.add( parent )
			}
		}

		Integer totalUsers = usersAllocatedToChild.size()

		if ( listYes.size() == 0 && listNo.size() == 0 ) {
			String message = "It looks like you and all of the other participants all agreed that " +
					"no variables we proposed influence ${child.readableLabel.encodeAsJavaScript()}. " +
					"Therefore, we will not ask you to complete this variable."

			out << """
				<script type='text/javascript'>
					\$(document).ready( function() {
						alert( '$message' );
						document.location = '${createLink( action: 'completedVariable', params: [ variable : child.label ])}';
					});
				</script>
			"""
		} else {
			def sortYes  = { low, high ->              allOthersCount.get( low ) <=>              allOthersCount.get( high ) }
			def sortNo   = { low, high -> totalUsers - allOthersCount.get( low ) <=> totalUsers - allOthersCount.get( high ) }
			def listItem = { parent, count, alsoSaid ->

				List<String> countClasses = [ "low", "medium", "high" ]
				float countPercent        = count / totalUsers
				int countClassIndex       = ( countClasses.size() - 1 ) - (int)( countPercent * countClasses.size() )
				String reviewedClass      = reviewedVars.contains( parent ) ? "doesnt-need-review" : "needs-review"

				out << """
					<li id='${parent.label}-variable-item' class='variable-item $reviewedClass'>
						<span class='var-summary'>
							<span class='count ${countClasses[ countClassIndex ]}'>
								${message( code: 'elicit.parents.agreement-count', args : [ count, totalUsers, (int)( countPercent * 100 ) ] )}
								<span class='also-said'>
									also said $alsoSaid to
								</span>
							</span>
							<button class='review' value='${parent.label}'>Review</button>
						</span>
						${bn.variable( [ var: parent ] )}
					</li>
				"""
			}

			out << """
					<h2 class='review-other review-yes'>Others who also said "<strong>Yes</strong>"</h2>
					<h2 class='review-list review-yes'>${message( code: 'elicit.parents.you-said-yes' )}</h2>
					<ul id='list-yes' class='review-yes potential-parents-list variable-list'>
					"""
				listYes.sort( sortYes ).each { parent ->
					listItem( parent, allOthersCount.get( parent ), 'yes' )
				}
				out << """
					</ul>
				"""

			out << """
					<h2 class='review-other review-no'>Others who also said "<strong>No</strong>"</h2>
					<h2 class='review-list review-no'>${message( code: 'elicit.parents.you-said-no' )}</h2>
					<ul id='list-no' class='review-no potential-parents-list variable-list'>
					"""
			listNo.sort( sortNo ).each { parent ->
				listItem( parent, totalUsers - allOthersCount.get( parent ), 'no' )
			}
			out << """
					</ul>
				"""

			if ( listNoAll.size() > 0 ) {
				def varPlural = listNoAll.size() == 1 ? "" : "s"
				out << """
					<div class='info' style='margin-top: 0.8em;'>
						There was an additional ${listNoAll.size()} variable$varPlural which you all agreed do not
						influence $child, and are therefore not shown here:<br /><br />${listNoAll*.readableLabel*.encodeAsHTML().join( ', ' )}.
					</div>
					"""
			}
		}
	}

	/**
	 * Iterates over each potentialParents and invokes the potentialParent taglib.
	 * If we are in subsequent phases, we don't show variables which received no love from anybody in the previous phase.
	 * @attr child REQUIRED
	 * @attr potentialParents REQUIRED
	 */
	def potentialParentsList = { attrs ->

		Variable child = attrs.child
		List<Variable> potentialParents = attrs.potentialParents.findAll { parent -> parent != child }

		out << """
			<h2 class='review-uninitialized'></h2>
			<ul id='list-uninitialized' class='potential-parents-list variable-list'>
				${potentialParents.collect { potentialParent( child: child, parent: it ) }.join( "" )}
			</ul>

			<h2 class='hide-if-yes-empty'>${message( code: 'elicit.parents.you-said-yes' )}</h2>
			<ul id='list-yes' class='potential-parents-list variable-list hide-if-yes-empty'></ul>

			<h2 class='hide-if-no-empty'>${message( code: 'elicit.parents.you-said-no' )}</h2>
			<ul id='list-no' class='potential-parents-list variable-list hide-if-no-empty'></ul>
			"""
	}

	/**
	 * If no attributes are specified, then we will render an empty form, prime to be populated via JSON objects.
	 * @attr child
	 * @attr parent
	 * @attr relationship
	 * @attr isSelected
	 */
	def potentialParentDialog = { attrs ->

		Variable child            = null
		Variable parent           = null
		Relationship relationship = null
		Boolean isSelected        = null

		if ( attrs.containsKey( 'child' ) && attrs.containsKey( 'parent' ) && attrs.containsKey( 'relationship' ) && attrs.containsKey( 'isSelected' ) ) {
			child        = attrs.remove( 'child' )
			parent       = attrs.remove( 'parent' )
			relationship = attrs.remove( 'relationship' )
			isSelected   = attrs.remove( 'isSelected' )
		}

		String dialogId     = parent ? parent.label + "-details"    : "details-form"
		String inputIdAttr  = parent ? "id='input-${parent.label}-form'" : ""
		String inputName    = parent ? "parents" : "exists"
		String comment      = relationship?.delphiPhase == delphiService.phase && relationship?.comment?.comment?.length() > 0 ? relationship.comment.comment : ''
		String commentLabel = parent ? message( code: 'elicit.parents.comments.label.round1' ) : message( code: 'elicit.parents.comments.label.later-rounds' )

		out << """
			<div id='$dialogId' class='var-details floating-dialog'>
				<table width="100%" class="form">
					<tr>
						<th></th>
						<td>
							<label>
								<input
									$inputIdAttr
									type='radio'
									${isSelected ? "checked='checked'" : ''}
									name='$inputName'
									value='yes'
									/>
								Yes it does
							</label>
							<br />
							<label>
								<input
									$inputIdAttr
									type='radio'
									${isSelected ? "checked='checked'" : ''}
									name='$inputName'
									value='no'
									/>
								No it doesn't
							</label>
						</td>
					</tr>
					<tr>
						<th>
							<span class='comment-label'>$commentLabel</span>
						</th>
						<td>
							<span class='comment-label'>$commentLabel</span>
							<div class='my-comment'>
								<textarea name='comment'>$comment</textarea>
							</div>
						</td>
					</tr>
				</table>
				<div class='header-wrapper'>
					${bn.saveButtons( [ atTop: true, saveLabel: "Save / Done" ] )}
				</div>
				"""

		List<Relationship> relationshipsToShowCommentsFor = parent ?
			( this.delphiService.hasPreviousPhase ?
				this.delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child, false ) :
				[ relationship ] ) :
			[]

		out << """
			${bnElicit.reasonsList( [ relationships: relationshipsToShowCommentsFor ] )}
		</div>
		"""
	}

	/**
	 * Displays a list element which portrays a variable which is primed to be selected as a parent of child.
	 * If the user has already viewed and saved a relationship for this pair of variables, we will retrieve that.
	 * @attrs child REQUIRED
	 * @attrs parent REQUIRED
	 */
	def potentialParent = { attrs ->

		Variable child   = attrs.child
		Variable parent  = attrs.parent

		Relationship relationship = this.delphiService.getMyCurrentRelationship( parent, child )

		String classes = relationship?.isExistsInitialized ?
			( relationship.exists ?
				"exists" :
				"doesnt-exist" ) :
			"uninitialized"

		out << """
			<li id='var-${parent.label}' class='variable-item $classes'>
				<input type="hidden" name="parent" value="${parent.label}" />
				<div class='var-summary'>
					<button type="button" class="comment">Comment</button>
					<button type="button" class="yes">Yes</button>
					<button type="button" class="no">No</button>
				</div>
				${bn.variable( [ var: parent, includeDescription: false ] )}
				${bn.variableDescription( [ var: parent ] )}
				${bnElicit.variableSynonyms( [ var: parent ] )}
			</li>
			"""
	}
}