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

import bn.elicitator.*;
import grails.util.*;
import org.apache.shiro.crypto.hash.*
import javax.servlet.ServletContext;

class BnBootStrap {
	
	OwlService owlService

	enum TestData {
		MGMC,
		INSURANCE
	}

	private Boolean doInitTestData = true
	private TestData doTestData = TestData.INSURANCE
	
	def init = { ServletContext servletContext ->

		if ( !isInitialized() )
		{
			initProperties( servletContext );
			initRolesAndAdminUser();
			initVariableClasses();
			initEmailTemplates();

			// Just for demoing, we will put in some expert users by default too...
			if ( doInitTestData || Environment.current == Environment.DEVELOPMENT )
			{
				if ( doTestData == TestData.MGMC )
				{
					initTestUsers( 10 );
					initMgmcVariables()
					initTestMgmcRelationships()
				}
				else if ( doTestData == TestData.INSURANCE )
				{
					initTestUsers( 2, false );
					initInsuranceVariables()
					initInsuranceAppInfo()
				}
			}
		}

	}

	private void initEmailTemplates()
	{

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

		List<EmailTemplate> templates = [ firstPhaseStarting, phaseComplete, studyComplete ]

		String subjectPrefix = AppProperties.properties.title + ": "
		String header = "Hi [User],\n\n"
		String footer = "\n\n" +
			"Thanks\n" +
			"\n" +
			"---\n" +
			"The survey can be accessed online at [Link].\n" +
			"If you no longer wish to take part in this survey, please visit [UnsubscribeLink]."

		templates.each {
			it.subject = subjectPrefix + it.subject
			it.body = header + it.body + footer
			it.save()
		}
	}

	/**
	 * Creates an admin, expert and consented role, then adds an admin user with password "I'm an admin user".
	 */
	private void initRolesAndAdminUser()
	{
		ShiroRole adminRole = new ShiroRole( name: "admin" )
		adminRole.addToPermissions( "*:*" )
		adminRole.save()

		ShiroRole expertRole = new ShiroRole( name: "expert" )
		expertRole.addToPermissions( "explain:*" ) // explanatory statement`
		expertRole.addToPermissions( "preference:*" )
		expertRole.save()

		// Once a user has consented, then they are allowed to view the rest of the system...
		ShiroRole consentedRole = new ShiroRole( name: "consented" )
		consentedRole.addToPermissions( "elicit:*" )
		consentedRole.addToPermissions( "data:*" ) // for ajax requests...
		consentedRole.save()

		ShiroUser adminUser = new ShiroUser( realName: "Admin User", email: AppProperties.properties.adminEmail, username: "admin", passwordHash: new Sha256Hash( "I'm an admin user" ).toHex() )
		adminUser.addToRoles( adminRole )
		adminUser.save()

	}

	private void initMgmcVariables()
	{
		// TODO: This is really just specific to the MGMC case, it should be removed if generalising the
		// system for other people to use...
		Variable.list()*.delete()

		def possibleVars = owlService.getAllVariables()

		ShiroUser admin = ShiroUser.findByUsername( 'admin' )
		possibleVars*.createdBy = admin
		possibleVars*.createdDate = new Date()
		possibleVars*.lastModifiedBy = admin
		possibleVars*.lastModifiedDate = new Date()

		initialVariables.each() { key, value ->

			boolean isInjuryType = ( key == "Injury Variables" );
			value.each() { varLabel ->

				Variable var = possibleVars.find { v -> v.label == varLabel }
				if ( var != null )
				{
					var.variableClass = isInjuryType ? VariableClass.problem : VariableClass.background
					var.save()
				}

			}

			group.save()
		}
	}

	private void initInsuranceAppInfo()
	{

		AppProperties.properties.title = "The car insurance company"
		AppProperties.properties.url = "http://firstaid.infotech.monash.edu/survey/run"

		AppProperties.properties.explanatoryStatement = """
			<h2>Who are you?</h2>
			<p>
				You should pretend to be running a car insurance company.
			</p>

			<h2>What does the insurance company want?</h2>
			<p>
				Like other insurance companies, this one is greedy, and wants to make as much money as possible. That
				means that you need to understand the clients you will be signing up.
			</p>

			<h2>How can clients cost you money?</h2>
			<p>
				Clients can be expensive and cost you money in several ways, for example:
				<ul class='bullet'>
					<li>They could crash their car, and it will need to be fixed</li>
					<li>They could crash into somebody else's car and that will need to be fixed</li>
					<li>They can crash into buildings and other things which will need to be fixed</li>
					<li>They can injure themselves, which will cost money in medical bills</li>
				</ul>
			</p>

			<h2>What are you going to be doing?</h2>
			<p>
				We are building a model which identifies the factors involved in assessing clients. Certain clients will
				be more risky than others, and therefore we will charge them a higher excess and premium.
			</p>

			<h2>How will you do this?</h2>
			<p>
				At this point, we are purely interested in how the various factors which determine risk fit together.
				You will be presented with a list of variables (which will start off small). As you answer questions
				about how these variables are influenced, you will be asked about more variables. This is because as
				you provide information to the system while answering questions, the system better understands the problem
				at hand, and will have more questions for you.
			</p>

			<h2>Should I be scared?</h2>
			<p>
				No. At first it may seem like the survey is never ending, but it will quickly plateau out and you will
				in fact complete it in a reasonable time frame.
			</p>
		"""

		AppProperties.properties.save()

	}

	private void initInsuranceVariables()
	{

		Variable.list()*.delete()

		VariableClass.background.potentialParents.push( VariableClass.background )
		VariableClass.background.save()

		List<Variable> vars = []

		ShiroUser admin = ShiroUser.findByUsername( 'admin' )

		/*vars.push(
			new Variable(
				label: "GoodStudent",
				readableLabel: "Good Student",
				description: "Was the student attentive during driver training? Did they barely pass or get a perfect score?",
				variableClass: VariableClass.background
			)
		)*/

		vars.push( 
			new Variable(
				label: "Age",
				readableLabel: "Age of client",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "SocioEcon",
				readableLabel: "Socio-economic Status",
				description: "What sort of background does the client come from?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "RiskAversion",
				readableLabel: "Risk Aversion",
				description: "Is the client likely to take risks when driving? Or drive more carefully and conservatively?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "VehicleYear",
				readableLabel: "Vehicle Year",
				description: "When was the clients vehicle made? Recently or some time ago?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "ThisCarDam",
				readableLabel: "Damage to clients car",
				description: "The damage acquired by the car owned by the client.",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "RuggedAuto",
				readableLabel: "Car Strength",
				description: "Will the clients car break like an eggshell or stay together like a tank?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "Accident",
				readableLabel: "Accident",
				description:  "Will the client have an accident? Will it be mild or severe?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "MakeModel",
				readableLabel: "Car Make and Model",
				description:  "Is the clients car a sports Car, economy, family sedan or a luxury car?",
				variableClass: VariableClass.background
			)
		)

		/*vars.push(
			new Variable(
				label: "DrivQuality",
				readableLabel: "DrivQuality",
				description:  "Dunno... ",
				variableClass: VariableClass.background
			)
		)*/

		vars.push(
			new Variable(
				label: "Mileage",
				readableLabel: "Mileage",
				description: "How far has the clients car driven since the engine was built?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "Antilock",
				readableLabel: "Car has Antilock Brakes",
				description: "Does the clients car have antilock breaks installed? This feature helps prevent uncontrollable skidding.",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "DrivingSkill",
				readableLabel: "Driving Skill",
				description: "Is the client a good driver? Do they have the skills necessary to drive properly?",
				variableClass: VariableClass.background
			)
		)

		/*vars.push(
			new Variable(
				label: "SeniorTrain",
				readableLabel: "SeniorTrain",
				description: "Dunno...",
				variableClass: VariableClass.background
			)
		)*/

		vars.push(
			new Variable(
				label: "ThisCarCost",
				readableLabel: "Cost of Clients Car",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "HomeBase",
				readableLabel: "Place of Residence",
				description: "Where does the car live, Secure parking, City, Rural, Street parking?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "AntiTheft",
				readableLabel: "Anti Theft Device Installed",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "PropCost",
				readableLabel: "Expected Property Cost",
				description:
					"The amount of money which you (as the insurance company) would expect to pay in response to this having an accident (e.g. crashing into a car or building).\n\n" +
					"The more you would expect to pay, the more excess and premium you should charge them to cater for that risk.",
				variableClass: VariableClass.problem
			)
		)

		vars.push(
			new Variable(
				label: "Other Car Cost",
				readableLabel: "Cost of the other car",
				description: "If in an accident with another car, how much is the other car worth?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "OtherCar",
				readableLabel: "Other Car Involvement",
				description: "Was another car involved in an accident?",
				variableClass: VariableClass.background
			)
		)

		vars.push(
			new Variable(
				label: "MedCost",
				readableLabel: "Expected Medical Costs",
				description:
					"The amount of money which you (as the insurance company) would expect to pay to this client for medical costs.\n\n" +
					"The more you would expect to pay to them in medical costs, the more excess and premium you should charge them to cater for that risk.",
				variableClass: VariableClass.problem
			)
		)

		/*vars.push(
			new Variable(
				label: "Cushioning",
				readableLabel: "Cushioning",
				variableClass: VariableClass.background
			)
		)*/

		vars.push(
			new Variable(
				label: "Airbag",
				readableLabel: "Airbag",
				description: "Is there any airbags installed in the clients car?",
				variableClass: VariableClass.background
			)
		)

		/*vars.push(
			new Variable(
				label: "ILiCost",
				readableLabel: "Liability Costs",
				variableClass: VariableClass.problem
			)
		)*/

		vars.push(
			new Variable(
				label: "DrivHist",
				readableLabel: "Driver History",
				description: "If the client has a history of insurance claims",
				variableClass: VariableClass.background
			)
		)

		vars*.createdBy = admin
		vars*.createdDate = new Date()
		vars*.lastModifiedBy = admin
		vars*.lastModifiedDate = new Date()

		vars*.save()

	}

	private Map<String, List<String>> getInitialVariables()
	{
		[
			"Environmental Factors": [
				"Temperature",
				"RelativeHumidity",
				"WindChill",
				"RainfallAmount",
				"AlcoholUse",
				"DrugUse",
				"Attendance",
				"Capacity",         // Venue capacity
				"Age",              // Crowd Age
				"CrowdMood",
				"VenueType",        // Indoors/Outdoors
				"Seated",           // Seated/Mobile?
				"EventVenue",
				"GatheringType",    // Event type (e.g. Concert, Festival, Marathon, Parade, etc...)
				"Bounded",          // Bounded/Unbounded
				"SiteDetails",      // Extended/Focused
			],
			"Injury Variables": [
				// USED BY ARBON 2002...
				"CardiovascularProblem",
				// "RespiratoryNonAsthma",
				"Asthma",
				"HeatRelatedCondition",
				"Laceration",
				"Fracture",
				"DrugAlcohol",
				// "MinorInjury",
				// "MinorProblem"

				// RANDOMLY CHOSEN FROM ONTOLOGY...
				/*"AbdominalCramps",
				"Headache",
				"Burns",
				"Suffocating",
				"Asthma",
				"CardiovascularProblem",
				"Abrasion",
				"Hypothermia",
				"Seizure",
				"HeatRelatedCondition",
				"ChestPain",
				"Laceration",
				"Bites",
				"Rash",
				"Diarrhoea",
				"Vomiting"*/
			]
		]

	}

	private Boolean isInitialized()
	{
		// If the admin role doesn't exist, we presume this is the first time we've started the application...
		// If that is the case, then we will populate the database with variables and constraints to make my
		// life easier.
		ShiroRole adminRole = ShiroRole.findByName( 'admin' )
		return adminRole != null;
	}

	private void initProperties( ServletContext servletContext )
	{
		AppProperties.properties.adminEmail = "peter@serwylo.com";
		AppProperties.properties.url = "http://uni.peter.serwylo.com:8080/bn-elicitator";
		AppProperties.properties.title = "First aid project";
		AppProperties.properties.delphiPhase = 1;
		AppProperties.properties.elicitationPhase = AppProperties.ELICIT_2_RELATIONSHIPS;
		AppProperties.properties.explanatoryStatement = servletContext.getResourceAsStream( "/WEB-INF/resources/explanatoryStatement.txt" )?.text;
		AppProperties.properties.save();
	}

	private void initTestUsers( int number = 10, boolean hasConsented = true )
	{

		for ( i in 1..number )
		{
			String name = "expert" + i
			ShiroUser user = new ShiroUser( realName: "Expert " + i, email: AppProperties.properties.adminEmail, username: name, passwordHash: new Sha256Hash( name ).toHex() )
			user.addToRoles( ShiroRole.findByName( 'expert' ) )

			if ( hasConsented )
			{
				user.addToRoles( ShiroRole.findByName( 'consented' ) )
			}

			user.save()
		}
	}

	private void initTestMgmcRelationships()
	{
		for ( i in 1..10 )
		{
			ShiroUser user = ShiroUser.findByUsername( 'expert' + i )

			// This will be the odd user out...
			if ( i == 3 )
			{
				new Relationship(
						delphiPhase:  AppProperties.properties.delphiPhase,
						parent: Variable.findByLabel( "Seating" ),
						child:  Variable.findByLabel( "Asthma" ),
						createdBy: user,
						comment: new Comment(
								createdBy: user,
								lastModifiedBy: user,
								createdDate: new Date(),
								lastModifiedDate: new Date(),
								comment: "When people are moving around, asthma will become more of a problem."
						).save()
				).save()

				new Relationship(
						delphiPhase:  AppProperties.properties.delphiPhase,
						parent: Variable.findByLabel( "AlcoholUse" ),
						child:  Variable.findByLabel( "Asthma" ),
						createdBy: user,
						comment: new Comment(
								createdBy: user,
								lastModifiedBy: user,
								createdDate: new Date(),
								lastModifiedDate: new Date(),
								comment: "I can't quite put my finger on it, but it feels like this would have an effect..."
						).save()
				).save()

				new Relationship(
						delphiPhase:  AppProperties.properties.delphiPhase,
						parent: Variable.findByLabel( "RelativeHumidity" ),
						child:  Variable.findByLabel( "Asthma" ),
						createdBy: user,
						comment: new Comment(
								createdBy: user,
								lastModifiedBy: user,
								createdDate: new Date(),
								lastModifiedDate: new Date(),
								comment: "Nasties in the air..."
						).save()
				).save()
			}
			else
			{
				List<String> comments = [
						"Vivamus fermentum semper porta.",
						"Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit.",
						"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur.",
				]
				List<String> environmentalVariables = initialVariables[ "Environmental Factors" ]
				List<String> injuryVariables = initialVariables[ "Injury Variables" ]
				for ( String injury in injuryVariables )
				{
					for ( j in 0..environmentalVariables.size() - 1 )
					{
						if ( j % 2 == 0 )
						{
							String enviroVar = environmentalVariables[ j ]
							new Relationship(
									delphiPhase: AppProperties.properties.delphiPhase,
									parent: Variable.findByLabel( enviroVar ),
									child: Variable.findByLabel( injury ),
									createdBy: user,
									comment: new Comment(
										createdBy: user,
										lastModifiedBy: user,
										createdDate: new Date(),
										lastModifiedDate: new Date(),
										comment: comments.get( (int)( Math.random() * ( comments.size() - 1 ) ) )
									).save()
							).save()
						}
					}
				}
			}
		}

		AppProperties.properties.delphiPhase ++
		AppProperties.properties.save()
	}

	/**
	 * Initializes the four main variable classes:
	 *  - Background
	 *  - Mediating
	 *  - Problem
	 *  - Symptom
	 * And their relationships among themselves.
	 */
	private void initVariableClasses()
	{
		VariableClass background = new VariableClass( name: VariableClass.BACKGROUND )
		background.save( flush: true )

		VariableClass problem = new VariableClass( name: VariableClass.PROBLEM, potentialParents: [ background ] )
		problem.save( flush: true )

		VariableClass mediating = new VariableClass( name: VariableClass.MEDIATING, potentialParents: [ background, problem ] )
		mediating.save( flush: true )

		VariableClass symptom = new VariableClass( name: VariableClass.SYMPTOM, potentialParents: [ background, mediating, problem ] )
		symptom.save( flush: true )
	}
}
