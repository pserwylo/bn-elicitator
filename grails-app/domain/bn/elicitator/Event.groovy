package bn.elicitator

/**
 * Logs events performed by users, e.g. logging in, filling in the form, etc.
 * Because this is first and foremost a research project, we're interested in keeping track of how the system is used.
 */
class Event {

	static enum Type {
		LOGIN,
		VIEW_RELATIONSHIPS,
		SAVE_RELATIONSHIPS,
		SAVE_RELATIONSHIP,
		COMPLETE_FORM,
		CREATED_VAR
	}
	
	Type type

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
	
	static Event logLogin() {
		new Event( 
			type: Type.LOGIN, 
			user: ShiroUser.current, 
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Logged in" ).save()
	}
	
	static Event logViewRelationships( Variable parent ) {
		new Event( 
			type: Type.VIEW_RELATIONSHIPS, 
			user: ShiroUser.current, 
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Viewed relationships for '" + parent + "'",
			var: parent ).save()
	}
	
	static Event logSaveRelationships( Variable parent ) {
		new Event( 
			type: Type.SAVE_RELATIONSHIPS, 
			user: ShiroUser.current, 
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Saved relationships for '" + parent + "'",
			var: parent ).save()
	}

	static Event logSaveRelationship( Relationship relationship ) {
		new Event(
			type: Type.SAVE_RELATIONSHIP,
			user: ShiroUser.current,
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Saved relationship from '" + relationship.parent + "' to '" + relationship.child + "' with " + relationship.confidence + " confidence.",
			relationship: relationship ).save()
	}

	static Event logCompleteForm() {
		new Event( 
			type: Type.COMPLETE_FORM,
			user: ShiroUser.current,
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			description: "Completed form" ).save()
	}
	
	static void logCreatedVar( Variable var ) {
		new Event(
			type: Type.CREATED_VAR,
			user: ShiroUser.current,
			date: new Date(),
			delphiPhase: AppProperties.properties.delphiPhase,
			var: var,
			description: "Created var '" + var + "'" ).save()
	}
	
}
