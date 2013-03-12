package bn.elicitator.init
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

import bn.elicitator.*
import bn.elicitator.auth.User
import bn.elicitator.init.DataLoader;
import javax.servlet.ServletContext;

class MgmcDataLoader extends DataLoader {

	protected List<Variable> getBackgroundVariables() {

		List<Variable> backgroundVars = [

			new Variable(
				label         : "Temperature",
				readableLabel : "Temperature" ),

			new Variable(
				label         : "RelativeHumidity",
				readableLabel : "Humidity" ),

			new Variable(
				label         : "WindChill",
				readableLabel : "Wind chill" ),

			new Variable(
				label         : "RainfallAmount",
				readableLabel : "Rainfall" ),

			new Variable(
				label         : "AlcoholUse",
				description   : "The alcohol consumption may be through legitimate means (i.e. sold at the event), or smuggled into the event. Either way, people may be intoxicated.",
				readableLabel : "Consumption of alcohol" ),

			new Variable(
				label         : "DrugUse",
				readableLabel : "Use of illicit drugs" ),

			new Variable(
				label         : "Attendance",
				description   : "The number of people who attend the event.",
				readableLabel : "Crowd attendance" ),

			new Variable(
				label         : "CrowdMood",
				description   : "The crowd mood describes the general feeling amongst the crowd. For example, it could be energetic, quiet, aggressive, etc.",
				readableLabel : "Crowd mood" ),

			new Variable(
				label         : "Age",
				description   : "The average age of people attending the event.",
				readableLabel : "Crowd age" ),

			new Variable(
				label            : "EventDuration",
				usageDescription : "Do any of the following variables <em>directly</em> influence the Duration of an event?",
				readableLabel    : "Event duration" ),

			new Variable(
				label         : "VenueType",
				readableLabel : "Setting (outdoors or indoors)" ),

			new Variable(
				label            : "Seated",
				usageDescription : "Do any of the following variables <em>directly</em> influence whether the attendees sit or stand at an event?",
				readableLabel    : "Seated or standing" ),

			new Variable(
				label            : "EventVenue",
				usageDescription : "Do any of the following variables <em>directly</em> influence the [This] an event is held at?",
				readableLabel    : "Venue" ),

			new Variable(
				label            : "GatheringType",
				description      : "Examples of event types include concerts, sporting events, or local festivals.",
				usageDescription : "Do any of the following variables <em>directly</em> influence the [This]?",
				readableLabel    : "Event type" ),

			new Variable(
				label            : "Bounded",
				description      : "A fenced event has some sort of wall or structure that constrains where people can move, such as a building. An open event is one where attendees can roam around, such as a music festival.",
				usageDescription : "Do any of the following variables <em>directly</em> influence whether an event is [This]?",
				readableLabel    : "Fenced or open" ),

			new Variable(
				label            : "SiteDetails",
				description      : "Some events have multiple points of interest, such as a music festival with multiple stages. Others have only one focal point, such as musical theater.",
				usageDescription : "Do any of the following variables <em>directly</em> whether the event has [This]?",
				readableLabel    : "Multiple points of interest" ),

		]

		String usageBackground = "Do any of the following variables <em>directly</em> influence the [This] at an event?";
		backgroundVars.each {
			if ( !it.usageDescription ) {
				it.usageDescription = usageBackground
			}
		}

		return backgroundVars

	}

	protected List<Variable> getProblemVariables() {

		List<Variable> problemVars = [

			new Variable(
				label         : "Fainting",
				readableLabel : "Fainting",
				synonyms      : [ "Syncope" ] ),

			new Variable(
				label         : "Wounds",
				readableLabel : "Wounds",
				synonyms      : [ "Laceration", "Abrasion", "Minor trauma", "Foreign body" ] ),

			new Variable(
				label         : "EyeInjury",
				readableLabel : "Eye injuries",
				synonyms      : [ "Periocular injury" ] ),

			new Variable(
				label         : "HeatRelatedProblems",
				description   : "Heat related problems encompass heat exhaustion and heat stroke.",
				readableLabel : "Heat related problems",
				synonyms      : [ "Effects of heat" ] ),

			new Variable(
				label         : "Headaches",
				readableLabel : "Headaches",
				synonyms      : [ "Head pain", "Pain in head", "Cephalalgia" ] ),

			new Variable(
				label         : "DifficultyBreathing",
				readableLabel : "Difficulty breathing",
				synonyms      : [ "Respiratory distress", "Shortness of breath", "Dyspnoea", "Asthma" ] ),

			new Variable(
				label         : "Musculoskeletal",
				readableLabel : "Musculoskeletal injuries",
				synonyms      : [ "Sprain of ligament", "Soft tissue injuries" ] ),

			new Variable(
				label         : "ChestPains",
				description   : "Non-cardiac related chest pains, such as acid reflux",
				readableLabel : "Chest pains" ),

			new Variable(
				label         : "AbdominalPains",
				readableLabel : "Abdominal pains" ),

			new Variable(
				label         : "InsectBites",
				readableLabel : "Insect bites" ),

			new Variable(
				label         : "HeadOrNeckInjuries",
				readableLabel : "Head or neck injuries",
				description   : "Traumatic injuries around the head or neck region of the body.",
				synonyms      : [ "Closed head injury", "Concussion" ] ),

			new Variable(
				label         : "SubstanceAbuse",
				readableLabel : "Substance abuse",
				synonyms      : [ "Alcohol abuse", "Drug abuse" ] ),

			new Variable(
				label         : "Diabetes",
				readableLabel : "Diabetes related problems",
				synonyms      : [ "Hyperglycemia", "Hypoglycemia" ] ),

			new Variable(
				label         : "CardiacArrest",
				readableLabel : "Cardiac arrest" ),

			new Variable(
				label         : "Seizures",
				readableLabel : "Seizures",
				synonyms      : [ "Fit", "Convulsion" ] ),

			new Variable(
				label         : "GI",
				readableLabel : "Gastrointestinal problems",
				synonyms      : [ "Nausea and/or vomiting" ] ),

		]

		String usageProblem = "Do any of the following variables <em>directly</em> influence the chance of patients suffering from [This] at an event?";
		problemVars.each {
			if ( !it.usageDescription ) {
				it.usageDescription = usageProblem
			}
		}

		return problemVars

	}

	protected List<Variable> getMediatingVariables() { [] }

	protected List<Variable> getSymptomVariables()   { [] }

	protected AppProperties getProperties( ServletContext servletContext )
	{
		new AppProperties(
			adminEmail          : "peter@serwylo.com",
			url                 : "http://uni.peter.serwylo.com:8080/bn-elicitator",
			title               : "First aid project",
			delphiPhase         : 1,
			elicitationPhase    : AppProperties.ELICIT_2_RELATIONSHIPS,
			explanatoryStatement: servletContext.getResourceAsStream( "/WEB-INF/resources/explanatoryStatement.txt" )?.text 
		)
	}

	protected void initOther() {

		VariableClass background = VariableClass.background
		background.readableLabel = "Variables about the public event"
		background.priority      = 1
		background.save()

		VariableClass problem = VariableClass.problem
		problem.readableLabel = "Patient injury variables"
		problem.priority      = 2
		problem.save()

	}

	private void initTestMgmcRelationships()
	{
		for ( i in 1..10 )
		{
			User user = User.findByUsername( 'expert' + i )

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
}
