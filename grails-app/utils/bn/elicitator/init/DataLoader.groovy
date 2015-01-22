package bn.elicitator.init
import bn.elicitator.*
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
import bn.elicitator.auth.Role
import bn.elicitator.auth.User
import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode
import grails.util.Holders
import org.springframework.beans.factory.access.BootstrapException

import javax.servlet.ServletContext

abstract class DataLoader {

	/**
	 * The servletContext is passed in so that you can access files from the app, e.g. a text
	 * file with the explanatory statement.
	 * @param servletContext
	 * @return
	 */
	abstract protected AppProperties getProperties( ServletContext servletContext );

	protected List<Variable> getBackgroundVariables() { [] }
	protected List<Variable> getProblemVariables()    { [] }
	protected List<Variable> getMediatingVariables()  { [] }
	protected List<Variable> getSymptomVariables()    { [] }

	protected Map<String,List<State>> getVariableStates() { [:] }

	protected void initOther() {}

	void init( ServletContext servletContext ) {

		if ( !isInitialized() ) {
			initProperties( servletContext )
			initRolesAndAdminUser()
			initVariableClasses()
			initVariables()
			initFeedbackQuestions()
			initBnStructure()
			initContentPages( servletContext )
			initOther()
		}

		// Performs its own initialization check for each email, so we can add new emails as required...
		initEmailTemplates()
		upgrade()
	}

	private void upgrade() {
		updateContentPages()
		upgradeRoles()
		doUpgrade()
	}

	protected FeedbackQuestionLoader getFeedbackQuestions() {
		return new FeedbackQuestionLoader()
	}

	private void initFeedbackQuestions() {
		FeedbackQuestionLoader questionLoader = feedbackQuestions
		if ( questionLoader ) {
			questionLoader.initQuestions()
		}
	}

	/**
	 * In some cases, we may already know the BN structure, in which case we will create a bunch of
	 * {@link BnNode}'s and {@link BnArc}'s. To do that, specify a list of
	 * lists. The outer list represents arcs, whereby the inner list represents the parent and child variable labels
	 * respectively.
	 * @return If this doesn't return null (like the default implementation does), then we will initialize a bunch
	 * of nodes and arcs, and then the {@link AppProperties#elicitationPhase} will be switched to "Probabilities",
	 * because we already know the structure.
	 */
	protected List<List<String>> getBnArcs() { null }

	private void initBnStructure() {

		def arcs = bnArcs
		if ( arcs != null ) {
			Map<String, Variable> allVariables = Variable.list().collectEntries { Variable variable ->
				new MapEntry( variable.label, variable )
			}

			def eachArc = { Closure closure ->
				arcs.eachWithIndex { it, i ->
					if ( it.size() != 2 ) {
						throw new BootstrapException( "Arcs must be a list of items with two Strings, found ${it.size()} at position $i." )
					}

					String childLabel  = it[ 1 ]
					if ( !allVariables.containsKey( childLabel ) ) {
						throw new BootstrapException( "Could not find child node '$childLabel'." )
					}

					String parentLabel = it[ 0 ]
					if ( !allVariables.containsKey( parentLabel ) ) {
						throw new BootstrapException( "Could not find parent node '$parentLabel'." )
					}

					closure( parentLabel, childLabel )
				}
			}

			Map<String, BnNode> allNodes = [:]
			eachArc { String parentLabel, String childLabel ->
				[ parentLabel, childLabel ].each { String label ->
					if ( !allNodes.containsKey( label ) ) {
						allNodes[ label ] = new BnNode( variable : allVariables[ label ] )
					}
				}
			}

			allNodes.values().each { node ->
				node.save( flush : true, failOnError : true )
			}

			eachArc { String parentLabel, String childLabel ->
				new BnArc( parent : allNodes[ parentLabel ], child : allNodes[ childLabel ], strength : 1.0f ).save( failOnError : true )
			}

			AppProperties.properties.elicitationPhase = AppProperties.ELICIT_3_PROBABILITIES
			AppProperties.properties.save( failOnError : true )
		}
	}

	protected void updateContentPages() {
		ContentPage emptyLastRound = ContentPage.findByAlias( ContentPage.EMPTY_LAST_ROUND )
		if ( !emptyLastRound ) {
			new ContentPage(
				alias     :  ContentPage.EMPTY_LAST_ROUND,
				canDelete : false,
				label     : "Didn't complete the previous round",
				content   : """
<h2>Bummer...</h2>
<p>... our records show that you didn't get a chance to complete the previous round in time.
Thank you for showing an interest in this research project, but unfortunately you will be unable to continue.
</p>

<p>
If this is a mistake, please contact <a href="mailto:peter.serwylo@monash.edu.au">peter.serwylo@monash.edu.au</a>
</p>
"""
			).save()
		}

		ContentPage cantRegisterThisRound = ContentPage.findByAlias( ContentPage.CANT_REGISTER_THIS_ROUND )
		if ( !cantRegisterThisRound ) {
			new ContentPage(
				alias     :  ContentPage.CANT_REGISTER_THIS_ROUND,
				canDelete : false,
				label     : "Can't register this round",
				content   : """
<h2>Bummer...</h2>
<p>
... thanks for showing an interest, but our records show that you didn't participate in the first round, and we are unable to accept any more participants.
</p>

<p>
If this is a mistake, please contact <a href="mailto:peter.serwylo@monash.edu.au">peter.serwylo@monash.edu.au</a>
</p>
"""
			).save()
		}
	}

	private void upgradeRoles() {
		Map<String, Role> roles = [
			(Role.ADMIN)     : Role.findByName( 'admin' ),
			(Role.EXPERT)    : Role.findByName( 'expert' ),
			(Role.CONSENTED) : Role.findByName( 'consented' ),
		]
		roles.each { entry ->
			String name = entry.key
			Role   role = entry.value
			if ( role ) {
				role.name = name
				role.save()
			}
		}
	}

	/**
	 * To be overridden by sub classes if required. For example, if they need to trigger some certain behaviour when
	 * started up after a code change.
	 */
	protected void doUpgrade() {

	}

	protected String replaceContentPlaceholders( String template ) {
		if (!template) {
			return "Empty";
		} else {
			Map<String, String> placeholders = [
				'[serverURL]' : Holders.getGrailsApplication().config.grails.serverURL?.toString()
			]
			placeholders.each {
				template = template.replace( it.key, it.value )
			}
			return template;
		}
	}

	protected String getHomePageContent() {
		""
	}


	protected String getHelpPageContent() {
		""
	}

	protected String getPrivacyPolicyPageContent() {
		""
	}

	protected void initContentPages( ServletContext context ) {

		String homeText    = context.getResourceAsStream( "/WEB-INF/resources/default-home.tpl" )?.text;
		String helpText    = context.getResourceAsStream( "/WEB-INF/resources/default-help-cpt.tpl" )?.text;
		String privacyText = context.getResourceAsStream( "/WEB-INF/resources/default-privacy-policy.tpl" )?.text;

		if ( ! homeText    ) homeText    = homePageContent
		if ( ! helpText    ) helpText    = helpPageContent
		if ( ! privacyText ) privacyText = privacyPolicyPageContent

		new ContentPage(
			label     : "Home",
			alias     : ContentPage.HOME,
			content   : replaceContentPlaceholders( homeText ),
			canDelete : false,
		).save( failOnError : true, flush : true )

		new ContentPage(
			label     : "Help",
			alias     : ContentPage.HELP,
			content   : replaceContentPlaceholders( helpText ),
			canDelete : false,
		).save( failOnError : true )

		new ContentPage(
			label   : "Privacy Policy",
			alias   : ContentPage.PRIVACY_POLICY,
			content : replaceContentPlaceholders( privacyText ),
			canDelete : false,
		).save( failOnError : true )

	}

	private void initEmailTemplates() {

		EmailTemplate firstPhaseStarting = new EmailTemplate(
			name: EmailTemplate.FIRST_PHASE_STARTING,
			description: "Sent to the users when the survey is about to start.",
			placeholderNames: [ EmailTemplate.PH_EXPECTED_PHASE_DURATION, EmailTemplate.PH_START_DATE ],
			subject: "Survey starting",
			body:
				"This email is to notify you that Round 1 of the first aid survey will be starting on ${EmailTemplate.PH_START_DATE}.\n" +
				"I look forward to your contributions.\n"
		)

		EmailTemplate phaseComplete = new EmailTemplate(
			name: EmailTemplate.PHASE_COMPLETE,
			description: "Phase is complete. Sent after all phases <em>except</em> the last, where the ${EmailTemplate.STUDY_COMPLETE} email is sent instead.",
			placeholderNames: [ EmailTemplate.PH_COMPLETED_PHASE, EmailTemplate.PH_NEXT_PHASE, EmailTemplate.PH_EXPECTED_PHASE_DURATION ],
			subject: "Round [NextPhase] started",
			body:
				"This email is to notify you that we've completed Round ${EmailTemplate.PH_COMPLETED_PHASE} and " +
				"are now starting Round ${EmailTemplate.PH_NEXT_PHASE}."
		)

		EmailTemplate studyComplete = new EmailTemplate(
			name: EmailTemplate.STUDY_COMPLETE,
			subject: "Study completed",
			description: "All rounds are finished. You should add a call to action here, so that they feel there is still good to come of their contributions.",
			body:
				"Thank you so much for taking part in this study. Your contributions are valued.\n" +
				"I will keep in touch to and let you know as soon as the results are in, as well as if there are any publications which arise from this."
		)

		EmailTemplate error = new EmailTemplate(
			name: EmailTemplate.ERROR,
			description: "An internal error occurred. This message will be sent to all admin users.",
			placeholderNames: [ EmailTemplate.PH_ERROR_MESSAGE, EmailTemplate.PH_EXCEPTION_MESSAGE, EmailTemplate.PH_EXCEPTION_STACK_TRACE, EmailTemplate.PH_EXCEPTION_TYPE, EmailTemplate.PH_ERROR_USER ],
			subject: "Error",
			body:
				"[ErrorMessage]\n\n[ExceptionType]: [ExceptionMessage]\n\n[ExceptionStackTrace]"
		)

		List<EmailTemplate> templates = [ firstPhaseStarting, phaseComplete, studyComplete, error ]

		String subjectPrefix = AppProperties.properties.title + ": "
		String header = "Hi [User],\n\n"
		String footer = "\n\n" +
			"Thanks\n" +
			"\n" +
			"---\n" +
			"The survey can be accessed online at [Link].\n" +
			"If you no longer wish to take part in this survey, please visit [UnsubscribeLink]."

		templates.each {
			if ( EmailTemplate.countByName( it.name ) == 0 ) {
				it.subject = subjectPrefix + it.subject
				it.body = header + it.body + footer
				it.save()
			}
		}
	}

	/**
	 * Creates an admin, expert and consented role, then adds an admin user with password "I'm an admin user".
	 */
	private void initRolesAndAdminUser() {
		Role adminRole = new Role( name: Role.ADMIN )
		adminRole.save( flush: true, failOnError: true )

		Role expertRole = new Role( name: Role.EXPERT )
		expertRole.save( flush: true, failOnError: true )

		// Once a user has consented, then they are allowed to view the rest of the system...
		Role consentedRole = new Role( name: Role.CONSENTED )
		consentedRole.save( flush: true, failOnError: true )

		User adminUser = new User( realName: "Admin User", email: AppProperties.properties.adminEmail, username: "admin", password: "I'm an admin user" )
		adminUser.save( flush: true, failOnError: true )

		adminRole.addUser( adminUser )
	}

	protected void initVariables() {
		removeVariables()
		saveVariables( backgroundVariables, VariableClass.background );
		saveVariables( problemVariables,    VariableClass.problem    );
		saveVariables( mediatingVariables,  VariableClass.mediating  );
		saveVariables( symptomVariables,    VariableClass.symptom    );
		addVariableStates()
	}

	protected void removeVariables() {
		Variable.list()*.delete()
	}

	protected void saveVariables( List<Variable> vars, VariableClass variableClass ) {
		User admin = User.findByUsername( 'admin' )
		vars*.variableClass    = variableClass
		vars*.createdBy        = admin
		vars*.createdDate      = new Date()
		vars*.lastModifiedBy   = admin
		vars*.lastModifiedDate = new Date()
		vars*.save()

	}

	protected final addVariableStates() {
		variableStates.each {
			String variableLabel = it.key
			List<State> states = it.value

			Variable variable = Variable.findByLabel( variableLabel )

			if ( variable == null ) {
				// throw new Exception( "Could not find variable '$variableLabel' to attach states to. Is it a typo?" )
			} else {

				states.each { State state ->
					variable.addToStates(state)
				}

				variable.save();
			}
		}
	}

	private Boolean isInitialized() {
		AppProperties.count() > 0
	}

	private void initProperties( ServletContext servletContext ) {
		AppProperties props = getProperties( servletContext )
		AppProperties.properties.adminEmail           = props.adminEmail
		AppProperties.properties.url                  = props.url
		AppProperties.properties.title                = props.title
		AppProperties.properties.delphiPhase          = props.delphiPhase
		AppProperties.properties.elicitationPhase     = props.elicitationPhase
		AppProperties.properties.explanatoryStatement = props.explanatoryStatement
		AppProperties.properties.save();
	}

	/**
	 * Helper function if we want to bootstrap a bunch of test users.
	 * @param number
	 * @param hasConsented
	 */
	protected void initTestUsers( int number = 10, boolean hasConsented = true ) {
		Role consented = Role.consented
		Role expert    = Role.expert

		for ( i in 1..number ) {
			String name = "expert" + i
			User user = new User( realName: "Expert " + i, email: AppProperties.properties.adminEmail, username: name, password: name )
			user.save( flush: true )
			expert.addUser( user )
			if ( hasConsented ) {
				consented.addUser( user )
			}
		}
	}

	/**
	 * Initializes the four main variable classes:
	 *  - Background
	 *  - Mediating
	 *  - Problem
	 *  - Symptom
	 * And their relationships among themselves.
	 */
	protected void initVariableClasses() {
		VariableClass symptom = new VariableClass( name: VariableClass.SYMPTOM )
		symptom.save( flush: true )

		VariableClass mediating = new VariableClass( name: VariableClass.MEDIATING, potentialChildren: [ symptom ] )
		mediating.save( flush: true )

		VariableClass problem = new VariableClass( name: VariableClass.PROBLEM, potentialChildren: [ mediating, symptom ] )
		problem.save( flush: true )

		VariableClass background = new VariableClass( name: VariableClass.BACKGROUND, potentialChildren: [ mediating, problem ] )
		background.save( flush: true )

		background.potentialChildren.add( background )
		background.save( flush: true )
	}
}
