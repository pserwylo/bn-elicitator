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

import bn.elicitator.auth.*
import grails.converters.JSON

class AdminDashController {

	def adminService
	def delphiService
	def userService
	def springSecurityService
	def variableService
	def allocateStructureQuestionsService

	/**
	 * Show some summary statistics, including the current state of the app (e.g. delphi phase), the list of users
	 * and how many have completed the current phase.
	 * @return
	 */
	def index() {

		List<User> userList = User.list()
		Integer totalExperts = userService.expertCount

		[
			appProperties         : AppProperties.properties,
			userList              : userList,
			totalExperts          : totalExperts,
			completedCurrentRound : delphiService.completedCurrentRound,
		]

	}

    def editVariables() {
        return [
            variables: Variable.list(),
			refreshedQuestions: flash.refreshedQuestions,
        ]
    }

	def addVariable() {
		render(view: 'variableForm', model: [
			variableId: null,
			variable: new Variable(
				readableLabel: "New Variable",
				usageDescription: "Does the state of New Variable directly influence any of these?",
			),
			variableClasses: VariableClass.list(),
		])
	}

    def editVariable() {
        if (!params.containsKey('id')) {
            redirect(action: 'editVariables')
        }

		Variable variable = Variable.get(params['id'] as Long)
		if (variable == null) {
			redirect(action: 'editVariables')
		}

        render(view: 'variableForm', model: [
			variableId: variable.id,
            variable: variable,
            variableClasses: VariableClass.list(),
        ])
    }

    def saveVariable() {

		Variable variable = null

		boolean requiresRefresh = false

		if (params.containsKey('id')) {
			variable = Variable.get(Long.parseLong(params['id'] as String))
		} else if (params.containsKey('add')) {
			requiresRefresh = true
			variable = new Variable(
				createdBy: userService.current,
				createdDate: new Date(),
				states: [],
			)
		}

        if (variable == null) {
            redirect(action: 'editVariables')
        }

        def requiredKeys = ["description", "readableLabel", "usageDescription", "variableClass"]
        if (requiredKeys.find({ !params.containsKey(it) })) {
            redirect(action: 'editVariables')
        }

        VariableClass newVariableClass = VariableClass.get(params['variableClass'] as Long)
        String newReadableLabel = params['readableLabel']
        String newDescription = params['description']
        String newUsageDescription = params['usageDescription']
		String newLabel = newReadableLabel.replaceAll("[^a-zA-Z0-9]", "")

        requiresRefresh = requiresRefresh || newVariableClass.id != variable.variableClass?.id

        variable.readableLabel = newReadableLabel
        variable.label = newLabel
        variable.description = newDescription
        variable.usageDescription = newUsageDescription
        variable.variableClass = newVariableClass

		variable.lastModifiedBy = userService.current
		variable.lastModifiedDate = new Date()

        variable.save(failOnError: true, flush: true)

		if (requiresRefresh) {
			allocateStructureQuestionsService.reassignQuestions()
			flash.refreshedQuestions = true
		}

        redirect(action: 'editVariables')
    }

	def editStudyDetails() {
		
		return [
			appProperties : AppProperties.properties
		]
		
	}
	
	def saveStudyDetails() {
		
		AppProperties.properties.title = params.title
		AppProperties.properties.adminEmail = params.adminEmail
		AppProperties.properties.save()
		
		forward( action: 'index' )

	}
	
	def initRelationships() {
		int count = 0
		User.list().each { user ->
			if ( variableService.initRelationships( user ) ) {
				count ++
			}
		}

		[ count : count ]
	}

	def ajaxSaveParticipantsPerQuestion() {
		boolean success = false
		int value       = -1
		if ( params.containsKey( "participantsPerQuestion" ) ) {
			int participantsPerQuestion = params.remove( "participantsPerQuestion" ) as int
			if ( participantsPerQuestion < 0 ) {
				participantsPerQuestion = 0
			}
			AppProperties.properties.targetParticipantsPerQuestion = participantsPerQuestion
			AppProperties.properties.save( flush: true )
			value = participantsPerQuestion
			success = true
		}

		def result = [ success: success ]
		if ( success ) {
			result.value = value
		}
		render result as JSON
	}

	/**
	 * If there are users who have  not completed the round, ask the admin to confirm, otherwise just redirect to
	 * the 'advanceDelphiPhase' action.
	 * @return
	 */
	def confirmAdvanceDelphiPhase() {

		List<User> yetToComplete = userService.getUsersYetToComplete( delphiService.phase )

		[ appProperties: AppProperties.properties, yetToComplete: yetToComplete ]

	}

	def yesAdvanceDelphiPhase() {

		flash.confirmed = true
		advanceDelphiPhase()

	}

	def advanceDelphiPhase() {

		// The browser was probably refreshed, or navigated here manually.
		// In this case, can we just shown them what we did last time?
		if ( flash.confirmed )
		{
			this.adminService.advanceDelphiPhase()
			flash.messages = [ "Delphi phase is now " + delphiService.phase + ". The participants have been notified via email." ]
		}

		redirect( action: 'index' )

	}

	def switchUser() {
		int id = params.id
		User user = User.get(id)
		springSecurityService.currentUser
	}

}
