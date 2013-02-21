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

import grails.converters.JSON

class ElicitController {

	VariableService     variableService
	DelphiService       delphiService
	BnService           bnService

	/**
	 * Mark this user as completed (for this round), then redirect to the main list so that they can still play around
	 * until next round.
	 */
	def completed = {

		List<Variable> stillToComplete = delphiService.getStillToVisit( variableService.allChildVars )

		if ( stillToComplete.size() > 0 )
		{
			render "Still need to review: " + stillToComplete.toString()
			return;
		}

		CompletedPhase phase = delphiService.completed
		if ( !phase )
		{
			phase = new CompletedPhase( completedBy: ShiroUser.current, completedDate: new Date(), delphiPhase: delphiService.phase )
			phase.save( failOnError: true )
		}

		redirect( action: 'problems' )
	}

	def completedVariable = {

		Variable var = Variable.findByLabel( (String)params[ 'variable' ] )
		if ( var == null )
		{
			response.status = 404
			render "Not Found"
		}
		else
		{
			this.variableService.visitVariable( var )
			redirect( action: 'problems' )
		}

	}

	def fixProblems =
	{
		List<BnService.RedundantRelationship> redundantRelationships = bnService.getRedundantRelationships()
		List<BnService.CyclicalRelationship> cyclicalRelationships = bnService.getCyclicalRelationships()

		List<String>                          errors           = []
		List<BnService.RedundantRelationship> redundantKeepers = []
		List<BnService.RedundantRelationship> redundantLosers  = []
		List<List<Variable>>                  cyclicalLosers   = []

		for ( BnService.RedundantRelationship redundantRelationship in redundantRelationships )
		{
			String key = redundantRelationship.redundantParent.label + "-" + redundantRelationship.child.label + "-keep";
			String keep = params[ key ];
			if ( keep == null )
			{
				errors.add( "Error: Should have specified a relationship for '" + redundantRelationship.relationship + "'" );
			}
			else
			{
				if ( keep == "keep" )
				{
					redundantKeepers.add( redundantRelationship )
				}
				else
				{
					redundantLosers.add( redundantRelationship )
				}
			}
		}

		for ( BnService.CyclicalRelationship cyclicalRelationship in cyclicalRelationships )
		{
			Boolean removeAny = false

			for ( Integer i = 0; i <  cyclicalRelationship.chain.size()-1; i ++ )
			{
				Variable parent = cyclicalRelationship.chain.get( i )
				Variable child = cyclicalRelationship.chain.get( i + 1 )

				String key = "remove-" + parent.label + "-" + child.label

				if ( params[ key ] == "1" )
				{
					removeAny = true
					// TODO: THIS IS BACKWARDS FOR SOME REASON...
					cyclicalLosers.add( [ parent, child ] )
				}
			}

			if ( !removeAny )
			{
				errors.add( "Must specify a relationship to remove from: " + bn.variableChain( chain: cyclicalRelationship.chain, includeTooltip: false ) )
			}
		}

		if ( errors.size() > 0 )
		{
			render errors.join( "<br />" )
			return
		}
		else
		{
			bnService.fixProblems( redundantKeepers, redundantLosers, cyclicalLosers )
		}

		redirect( action: 'index' )
	}

	private void fixRedundant( Boolean keep, String parentLabel, String childLabel, Boolean displayAll ) {
		Variable parent = Variable.findByLabel( parentLabel )
		Variable child  = Variable.findByLabel( childLabel )

		if ( parent == null || child == null )
		{
			String label = parent == null ? parentLabel : childLabel
			response.sendError( 404, "Variable '$label' not found" )
		}
		else
		{
			List<BnService.RedundantRelationship> redundantRelationships = bnService.getRedundantRelationships()
			BnService.RedundantRelationship rel = redundantRelationships.find {
				it.relationship.child == child && it.relationship.parent == parent
			}

			if ( keep ) {
				bnService.keepRedundantRelationship( rel )
			} else {
				bnService.removeRedundantRelationship( rel )
			}
		}

		redirectToProblems()

	}

	def keepRedundant = {
		fixRedundant( true, (String)params["parent"], (String)params["child"], (Boolean)params["displayAll"] )
	}

	def removeRegular = {
		Variable parent = Variable.findByLabel( (String)params["parent"] )
		Variable child = Variable.findByLabel( (String)params["child"] )

		if ( parent == null || child == null )
		{
			String label = parent == null ? parentLabel : childLabel
			response.sendError( 404, "Variable '$label' not found" )
		}
		else
		{
			bnService.removeRegularRelationship( parent, child )
			redirectToProblems()
		}
	}

	def removeRedundant = {
		fixRedundant( false, (String)params["parent"], (String)params["child"], (Boolean)params["displayAll"] )
	}

	def removeCycle = {
		Variable parent = Variable.findByLabel( (String)params["parent"] )
		Variable child  = Variable.findByLabel( (String)params["child"] )

		if ( parent == null || child == null ) {
			String label = parent == null ? (String)params["parent"] : (String)params["child"]
			response.sendError( 404, "Variable '$label' not found" )
		} else {
			bnService.removeCycle( parent, child )
			redirectToProblems()
		}
	}

	def redirectToProblems = {

		def newParams = [:]

		if ( params.containsKey( "scroll" ) )
		{
			flash.scroll = params.remove( "scroll" )
		}

		if ( params.containsKey( "displayAll" ) )
		{
			newParams.put( "displayAll", params.remove( "displayAll" ) )
		}

		redirect( action: "problems", params: newParams )
	}

	/**
	 * Check if there are any (potentially) redundant relationships and present them to the user for confirmation.
	 * @return
	 */
	def problems = {

		List<BnService.CyclicalRelationship> cyclicalRelationships = bnService.getCyclicalRelationships()
		List<BnService.RedundantRelationship> redundantRelationships = []
		Integer numKeepers = 0

		def showProblems = false
		def displayAll = false
		if ( params.containsKey( "displayAll" ) )
		{
			displayAll = params.remove( "displayAll" )
		}

		if ( cyclicalRelationships.size() > 0 )
		{
			showProblems = true
		}
		else
		{
			redundantRelationships = bnService.getRedundantRelationships()

			numKeepers = redundantRelationships.count {
				it.relationship.isRedundant == Relationship.IS_REDUNDANT_NO
			}

			if ( redundantRelationships.size() > 0 ) {
				// If all of the relationships have been explicitly marked as okay by the user, don't bother them each time
				// by reshowing them to the users...
				if ( numKeepers < redundantRelationships.size() || displayAll )
				{
					showProblems = true
				}
			}
		}

		if ( showProblems )
		{
			[
				redundantRelationships: redundantRelationships,
				cyclicalRelationships: cyclicalRelationships,
				displayAll: displayAll,
				numKeepers: numKeepers,
				scroll: flash.containsKey( "scroll" ) ? flash["scroll"] : 0
			]
		}
		else
		{
			redirect( action: 'index' );
		}
	}

	def ajaxSaveDetails = {

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
			response.sendError( 404, "Variable '$label' not found" )
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
			response.sendError( 404, "Variable '$label' not found" )
		}
		else
		{
			List<Relationship> relationships = delphiService.getAllPreviousRelationshipsAndMyCurrent( parent, child, false )
			def comments = relationships.findAll { it.comment?.comment?.size() > 0 }.collect { rel ->
				[
					comment     : rel.comment.comment,
					delphiPhase : rel.delphiPhase,
					exists      : rel.exists,
					byMe        : rel.createdBy == ShiroUser.current,
				]
			}

			def result = [
				parentLabel         : parent.label,
				parentLabelReadable : parent.readableLabel,
				exists              : relationships.find { it.createdBy == ShiroUser.current }?.exists ? true : false,
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

		Variable var = null

		if ( params["for"] != null )
		{
			var = Variable.findByLabel( (String)params["for"] )
		}

		if ( var == null )
		{
			response.sendError( 404, "Could not find variable '${params["for"]}" )
			return null
		}
		else
		{
			List<Variable> potentialParents = this.variableService.getPotentialParents( var )
			String view = delphiService.hasPreviousPhase ? "reviewParents" : "parents"
			render(
				view: view,
				model: [
					variable         : var,
					delphiPhase      : delphiService.phase,
					potentialParents : potentialParents,
					totalUsers       : ShiroUser.count()
				]
			)
		}

	}

	/**
	 * Usually in response to an AJAX post, this will just dump the data into the database so that we can continue with
	 * the interaction in the view.
	 */
	def save = { SaveRelationshipCommand cmd ->

		Variable child = Variable.findByLabel( cmd.child );
		Variable parent = Variable.findByLabel( cmd.parent );
		if ( child == null || parent == null )
		{
			response.status = 404
			render child ? cmd.parent : cmd.child + " Not Found"
		}
		else
		{
			// Try to use the existing relationship (from this round) if we have one, to prevent doubling up. Then we
			// can presume that there will only be one relationship per set of variables per phase per participant.
 			Relationship relationship = this.delphiService.getMyCurrentRelationship( parent, child );
			if ( !relationship )
			{
				relationship = new Relationship(
					parent: parent,
					child: child,
					delphiPhase: AppProperties.properties.delphiPhase,
					createdBy: ShiroUser.current
				)
			}

			relationship.exists = cmd.exists
			relationship.isExistsInitialized = true

			String commentText = cmd.comment?.trim()
			if ( commentText )
			{
				Comment comment = relationship.comment ? relationship.comment : new Comment()
				comment.comment = commentText
				comment.createdBy = ShiroUser.current
				comment.createdDate = new Date()
				comment.lastModifiedBy = ShiroUser.current
				comment.lastModifiedDate = new Date()
				comment.save( flush: true )

				relationship.comment = comment
			}
			else
			{
				if ( relationship.comment )
				{
					relationship.comment.delete()
				}

				relationship.comment = null;
			}

			relationship.save( flush: true )
			LoggedEvent.logSaveRelationship( relationship )

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

		[
			user                      : ShiroUser.current,
			delphiPhase               : delphiService.phase,
			variables                 : varList,
			keptRedunantRelationships : bnService.countKeptRedundantRelationships(),
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
			response.sendError( 400, "No label or description received." )
			return
		}

		Variable var = new Variable(
			createdBy        : ShiroUser.current,
			createdDate      : new Date(),
			lastModifiedBy   : ShiroUser.current,
			lastModifiedDate : new Date(),
			readableLabel    : cmd.label,
			label            : cmd.label,
			description      : cmd.description,
			variableClass    : cmd.variableClass
		)

		Variable duplicate = Variable.findByLabelOrReadableLabel( var.label, var.readableLabel )
		if ( duplicate ) {
			response.sendError( 400, "Variable '$duplicate.label' ($duplicate.readableLabel) already exists." );
			return
		}

		var.save( failOnError: true )
		LoggedEvent.logCreatedVar( var )
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
