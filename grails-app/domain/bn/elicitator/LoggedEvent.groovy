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

/**
 * Logs events performed by users, e.g. logging in, filling in the form, etc.
 * Because this is first and foremost a research project, we're interested in keeping track of how the system is used.
 */
class LoggedEvent {

	LoggedEventType type

	ShiroUser user

	Date date

	Integer delphiPhase

	String description

	Variable var = null

	Relationship relationship = null

	String toString() {
		date.format( 'dd/MM/yyyy hh:mm' ) + ": " + description
	}
	
    static constraints = {
		var( nullable: true )
    }
	
	static LoggedEvent logLogin() {
		new LoggedEvent(
			type: LoggedEventType.LOGIN,
			user: ShiroUser.current, 
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Logged in" ).save()
	}
	
	static LoggedEvent logViewRelationships( Variable parent ) {
		new LoggedEvent(
			type: LoggedEventType.VIEW_RELATIONSHIPS,
			user: ShiroUser.current, 
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Viewed relationships for '" + parent + "'",
			var: parent ).save()
	}
	
	static LoggedEvent logSaveRelationships( Variable parent ) {
		new LoggedEvent(
			type: LoggedEventType.SAVE_RELATIONSHIPS,
			user: ShiroUser.current, 
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Saved relationships for '" + parent + "'",
			var: parent ).save()
	}

	static LoggedEvent logSaveRelationship( Relationship relationship ) {
		new LoggedEvent(
			type: LoggedEventType.SAVE_RELATIONSHIP,
			user: ShiroUser.current,
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Saved relationship from '" + relationship.parent + "' to '" + relationship.child + "' with " + relationship.confidence + " confidence.",
			relationship: relationship ).save()
	}

	static LoggedEvent logCompleteForm() {
		new LoggedEvent(
			type: LoggedEventType.COMPLETE_FORM,
			user: ShiroUser.current,
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Completed form" ).save()
	}
	
	static void logCreatedVar( Variable var ) {
		new LoggedEvent(
			type: LoggedEventType.CREATED_VAR,
			user: ShiroUser.current,
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			var: var,
			description: "Created var '" + var + "'" ).save()
	}
	
}
