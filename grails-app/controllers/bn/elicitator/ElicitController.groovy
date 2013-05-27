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

/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 11/12/12 1:13 PM.$year Peter Serwylo (peter.serwylo@monash.edu)
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

import bn.elicitator.auth.*
import bn.elicitator.events.CreatedVariableEvent
import bn.elicitator.events.FinishedRoundEvent

import bn.elicitator.events.RemovedCycleEvent

import bn.elicitator.events.SaveRelationshipEvent
import bn.elicitator.events.SaveRelationshipLaterRoundEvent
import bn.elicitator.events.ViewRelationshipsEvent
import grails.converters.JSON

class ElicitController {

	VariableService     variableService
	DelphiService       delphiService
	BnService           bnService
	UserService         userService

	def beforeInterceptor = {
		if ( !userService.current.hasConsented ) {
			redirect( controller: 'explain', action: 'statement' )
		}
	}

	/**
	 * Mark this user as completed (for this round), then redirect to the main list so that they can still play around
	 * until next round.
	 */
	def completed = {
		if ( checkProblems( createLink( action: 'completed' ) ) ) {
			List<Variable> stillToComplete = variableService.getStillToVisit()
			if ( stillToComplete.size() > 0 ) {
				render "Still need to review: " + stillToComplete.toString()
				return;
			}

			CompletedPhase phase = delphiService.completed
			if ( !phase ) {
				phase = new CompletedPhase( completedBy: userService.current, completedDate: new Date(), delphiPhase: delphiService.phase )
				phase.save( failOnError: true )
				FinishedRoundEvent.logEvent()
			}
			redirect( action: 'index' )
		}
	}

	def completedVariable = {

		Variable var = Variable.findByLabel( (String)params[ 'variable' ] )
		if ( var == null ) {
			throw new Exception( "Not found: " + params['variable'] )
		} else {
			this.variableService.finishVariable( var )
			redirect( action: 'problems' )
		}
	}

	def fixProblems =
	{
		List<BnService.CyclicalRelationship> cyclicalRelationships = bnService.getCyclicalRelationships()
		List<String>                         errors                = []
		List<List<Variable>>                 cyclicalLosers        = []

		for ( BnService.CyclicalRelationship cyclicalRelationship in cyclicalRelationships ) {
			Boolean removeAny = false

			for ( Integer i = 0; i <  cyclicalRelationship.chain.size()-1; i ++ ) {
				Variable parent = cyclicalRelationship.chain.get( i )
				Variable child = cyclicalRelationship.chain.get( i + 1 )

				String key = "remove-" + parent.label + "-" + child.label

				if ( params[ key ] == "1" ) {
					removeAny = true
					// TODO: THIS IS BACKWARDS FOR SOME REASON...
					cyclicalLosers.add( [ parent, child ] )
				}
			}

			if ( !removeAny ) {
				errors.add( "Must specify a relationship to remove from: " + bn.variableChain( chain: cyclicalRelationship.chain, includeTooltip: false ) )
			}
		}

		if ( errors.size() > 0 ) {
			render errors.join( "<br />" )
			return
		} else {
			bnService.fixProblems( cyclicalLosers )
		}

		def targetUri = params['redirectTo'] ?: createLink( action: 'index' )
		redirect( targetUri: targetUri )
	}

	def removeCycle = {
		Variable parent = Variable.findByLabel( (String)params["parent"] )
		Variable child  = Variable.findByLabel( (String)params["child"] )

		if ( parent == null || child == null ) {
			String label = parent == null ? (String)params["parent"] : (String)params["child"]
			throw new Exception( "Not found: $label" )
		} else {
			bnService.removeCycle( parent, child )
			RemovedCycleEvent.logEvent( parent, child )
			redirectToProblems()
		}
	}

	def redirectToProblems = {
		def newParams = [:]
		if ( params.containsKey( "scroll" ) ) {
			flash.scroll = params.remove( "scroll" )
		}
		if ( params.containsKey( "displayAll" ) ) {
			newParams.put( "displayAll", params.remove( "displayAll" ) )
		}
		redirect( action: "problems", params: newParams )
	}

	/**
	 * Check if there are any cyclical relationships and present them to the user to resolve.
	 * @return
	 */
	def problems = {
		if ( checkProblems( createLink( action: 'problems' ) ) ) {
			redirect( action: 'index' );
		}
	}

	private Boolean checkProblems( redirectTo ) {

		List<BnService.CyclicalRelationship> cyclicalRelationships = bnService.getCyclicalRelationships()

		boolean show = cyclicalRelationships.size() > 0
		if ( show ) {
			show = true
			render (
				view: 'problems',
				model: [
					redirectTo            : redirectTo,
					cyclicalRelationships : cyclicalRelationships,
					scroll                : flash.containsKey( "scroll" ) ? flash["scroll"] : 0
				])
		}
		return !show
	}

	def ajaxGetDetails = {
		if ( delphiService.hasPreviousPhase ) {
			return
		}

		Variable parent = null
		Variable child  = null

		if ( params.containsKey( "parent" ) && params.containsKey( "child" ) ) {
			parent = Variable.findByLabel( (String)params['parent'] )
			child  = Variable.findByLabel( (String)params['child'] )
		}

		if ( parent == null || child == null )
		{
			String label = parent == null ? params['parent'] : params['child']
			throw new Exception( "Not found: $label" )
		}
		else
		{
			Relationship relationship = delphiService.getMyCurrentRelationship( parent, child )
			def result = [
				comment       : relationship?.comment?.comment ?: "",
				label         : parent.label,
				readableLabel : parent.readableLabel,
			]

			render result as JSON
		}
	}

	def ajaxGetReviewDetails = {

		if ( !delphiService.hasPreviousPhase ) {
			return
		}

		Variable parent = null
		Variable child  = null

		if ( params.containsKey( "parent" ) && params.containsKey( "child" ) ) {
			parent = Variable.findByLabel( (String)params['parent'] )
			child  = Variable.findByLabel( (String)params['child'] )
		}

		if ( parent == null || child == null )
		{
			String label = parent == null ? params['parent'] : params['child']
			throw new Exception( "Not found: $label" )
		}
		else
		{
			User user = userService.current
			List<Relationship> relationships = delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child, false )
			def comments = relationships.findAll { it.comment?.comment?.size() > 0 }.collect { rel ->
				[
					comment     : rel.comment.comment,
					delphiPhase : rel.delphiPhase,
					exists      : rel.exists,
					byMe        : rel.createdBy == user,
				]
			}

			def result = [
				parentLabel         : parent.label,
				parentLabelReadable : parent.readableLabel,
				exists              : relationships.find { it.createdBy == user }?.exists,
				comments            : comments
			]

			render result as JSON
		}

	}

	/**
	 * Shows a form where one variable is displayed, and a list of all potential parents.
	 * This does not mean all other variables, as we are trying very hard to reduce this
	 * workload on the expert. Instead, we use the configuration of constraints to restrict
	 * the variables which are shown as potential parents.
	 *
	 * The variable we show is deduced from the 'for' query param. However if none is specified,
	 * we will try to pull the first variable off the rank and then redirect to a screen which uses that.
	 */
    def parents = {

		Variable var = ( params["for"] == null ) ? null : Variable.findByLabel( (String)params["for"] )
		if ( var == null ) {
			throw new Exception( "Not found: ${params['for']}" )
		}

		List<Variable> potentialParents = this.variableService.getPotentialParents( var )
		String view = delphiService.hasPreviousPhase ? "reviewParents" : "parents"
		ViewRelationshipsEvent.logEvent( var )

		def model = [
			variable         : var,
			delphiPhase      : delphiService.phase,
			potentialParents : potentialParents,
			totalUsers       : userService.expertCount,
		]

		if ( delphiService.hasPreviousPhase ) {
			model.reviewedVariables = variableService.myReviewedRelationshipsFor( var )*.relationship*.parent
		}

		render( view  : view, model : model )
	}

	/**
	 * Usually in response to an AJAX post, this will just dump the data into the database so that we can continue with
	 * the interaction in the view.
	 */
	def save = { SaveRelationshipCommand cmd ->

		User user = userService.current
		Variable child = Variable.findByLabel( cmd.child );
		Variable parent = Variable.findByLabel( cmd.parent );
		if ( child == null || parent == null ) {
			String label = child ? cmd.parent : cmd.child
			throw new Exception( "Not found: $label" )
		} else {
			// Try to use the existing relationship (from this round) if we have one, to prevent doubling up. Then we
			// can presume that there will only be one relationship per set of variables per phase per participant.
 			Relationship relationship = this.delphiService.getMyCurrentRelationship( parent, child );
			if ( !relationship ) {
				relationship = new Relationship(
					parent: parent,
					child: child,
					delphiPhase: AppProperties.properties.delphiPhase,
					createdBy: user
				)
			}

			Boolean hasChangedMind = relationship.exists != cmd.exists

			relationship.exists = cmd.exists
			relationship.isExistsInitialized = true

			String commentText = cmd.comment?.trim()
			if ( commentText ) {
				Comment comment = relationship.comment ?: new Comment()
				comment.comment = commentText
				comment.createdBy = user
				comment.createdDate = new Date()
				comment.lastModifiedBy = user
				comment.lastModifiedDate = new Date()
				comment.save( flush: true )
				relationship.comment = comment
			} else {
				if ( relationship.comment ) {
					relationship.comment.delete()
				}
				relationship.comment = null;
			}

			relationship.save( flush: true )

			if ( delphiService.hasPreviousPhase ) {

				ReviewedRelationship review = ReviewedRelationship.findByDelphiPhaseAndReviewedByAndRelationship( delphiService.phase, user, relationship )
				if ( review == null ) {
					new ReviewedRelationship( relationship: relationship, delphiPhase : delphiService.phase, reviewedBy : user ).save()
				}

				def allRelationships                        = delphiService.getAllPreviousRelationshipsAndMyCurrent( relationship.parent, relationship.child, false )
				def othersRelationships                     = allRelationships.findAll { it.createdBy != user }
				def othersPreviousRelationships             = othersRelationships.findAll { it.delphiPhase == delphiService.previousPhase }
				def othersPreviousRelationshipsWithComments = othersPreviousRelationships.findAll { it.comment?.comment != null }

				int totalOthers                  = userService.expertCount - 1
				int numOthersWhoSaidYes          = othersPreviousRelationships.count { it.exists }
				int numOthersWhoSaidNo           = totalOthers - numOthersWhoSaidYes
				int numOthersWhoAgreeNow         = relationship.exists ? numOthersWhoSaidYes : numOthersWhoSaidNo
				int numExistsComments            = othersPreviousRelationshipsWithComments.count { it.exists }
				int numDoesntExistComments       = othersPreviousRelationshipsWithComments.size() - numExistsComments

				SaveRelationshipLaterRoundEvent.logEvent(
					relationship,
					hasChangedMind,
					numOthersWhoAgreeNow,
					totalOthers,
					numExistsComments,
					numDoesntExistComments,
				)


			} else {
				SaveRelationshipEvent.logEvent( relationship )
			}

			def data = [
				exists  : relationship.exists,
				comment : relationship.comment?.comment ?: ""
			]
			render data as JSON
		}
	}

	def index = {
		this.variableService.initRelationships()
		List<Variable> varList = this.variableService.getAllChildVars()

		return [
			user                      : userService.current,
			delphiPhase               : delphiService.phase,
			variables                 : varList,
			hasPreviousPhase          : delphiService.hasPreviousPhase,
			stillToVisit              : delphiService.getStillToVisit( varList ),
			completed                 : delphiService.completed,
		]
	}

	/**
	 * Creates the new variable, saves it, then redirects to {@link ElicitController#parents} for the variable we
	 * were viewing when we added this new variable.
	 */
	def addVariable = { AddVariableCommand cmd ->

		if ( !cmd.label.trim() || !cmd.description.trim() ) {
			throw new Exception( "Invalid input: No label or description received." )
		}

		User user = userService.current
		Variable var = new Variable(
			createdBy        : user,
			createdDate      : new Date(),
			lastModifiedBy   : user,
			lastModifiedDate : new Date(),
			readableLabel    : cmd.label,
			label            : cmd.label,
			description      : cmd.description,
			variableClass    : cmd.variableClass
		)

		Variable duplicate = Variable.findByLabelOrReadableLabel( var.label, var.readableLabel )
		if ( duplicate ) {
			throw new Exception( "Invalid input: Variable '$duplicate.label' ($duplicate.readableLabel) already exists." );
		}

		var.save( failOnError: true, flush: true )

		Variable returnTo = null;
		if ( params['returnToVar'] ) {
			returnTo = Variable.findByLabel( params['returnToVar'] as String )
		}

		if ( !returnTo ) {
			returnTo = var
		}

		CreatedVariableEvent.logEvent( var, returnTo )
		redirect( action: parents, params: [ for: params['returnToVar'] ] )
	}

}

class AddVariableCommand {

	String label

	String description

	/**
	 * The variable for which we want to return to the elicitation screen after saving.
	 */
	String returnToVar

	public VariableClass getVariableClass()
	{
		return VariableClass.findByName( params[ 'variableClassName' ] as String )
	}

}

class SaveRelationshipCommand {

	/**
	 * The label of the child variable.
	 */
	String child

	/**
	 * The label of the parent variable.
	 */
	String parent

	/**
	 * Textarea input which gives them the chance to explain why they feel the way they do.
	 */
	String comment

	/**
	 * If the checkbox was clicked, then we say that the relationship exists.
	 */
	Boolean exists

}
