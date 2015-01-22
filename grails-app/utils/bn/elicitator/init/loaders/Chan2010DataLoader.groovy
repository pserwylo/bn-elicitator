package bn.elicitator.init.loaders
/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2013 Peter Serwylo (peter.serwylo@monash.edu)
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

import bn.elicitator.AppProperties
import bn.elicitator.ContentPage
import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.VariableClass
import bn.elicitator.init.DataLoader

import javax.servlet.ServletContext

/**
 * Toying with the idea of using additional variable classes beyond Background, Mediating, Problem and Symptom.
 * Based on the network from:
 * 
 *  Chan, T.; Ross, H.; Hoverman, S. & Powell, B. (2010). "Participatory development of a Bayesian network model for catchment-based water resource management". Water Resources Research, 46.
 *
 */
class Chan2010DataLoader extends DataLoader {

	protected void initOther() {
		updateHomePage();
	}

	protected void updateHomePage() {
		String homeText = """
			<h2>Chan et al 2010</h2>

			<div style='text-align: center; margin-top: 2em;'>
				<p>
					Does this sound like something you can help with?
					<br />
					<button class='big' onclick='document.location = "[serverURL]/explain"'>Register / Log in</button>
				</p>
			</div>
		"""

		ContentPage page = ContentPage.findByAlias( ContentPage.HOME )
		page.content = replaceContentPlaceholders( homeText )
		page.save( failtOnError : true, flush : true )

	}

	protected void doUpgrade() {
	}

	@Override
	protected Map<String,List<State>> getVariableStates() { [:] }

	protected void initVariableClasses() {

		VariableClass waterQuality = new VariableClass( name: WATER_QUALITY_VARIABLE_CLASS, potentialChildren: [] )
		waterQuality.save( flush : true, failOnError : true )

		VariableClass problem = new VariableClass( name: PROBLEM_VARIABLE_CLASS, potentialChildren: [] )
		problem.save( flush : true, failOnError : true )

		VariableClass humanActivity = new VariableClass( name: HUMAN_ACTIVITY_VARIABLE_CLASS, potentialChildren: [] )
		humanActivity.save( flush : true, failOnError : true )

		VariableClass affordability = new VariableClass( name: AFFORDABILITY_VARIABLE_CLASS, potentialChildren: [] )
		affordability.save( flush : true, failOnError : true )

		VariableClass sustainability = new VariableClass( name: SUSTAINABILITY_VARIABLE_CLASS, potentialChildren: [] )
		sustainability.save( flush : true, failOnError : true )

		VariableClass management = new VariableClass( name: MANAGEMENT_VARIABLE_CLASS, potentialChildren: [] )
		management.save( flush : true, failOnError : true )

		waterQuality.addToPotentialChildren( waterQuality )
		waterQuality.addToPotentialChildren( problem )
		waterQuality.save( flush : true, failOnError : true )

		affordability.addToPotentialChildren( affordability )
		affordability.addToPotentialChildren( problem )
		affordability.save( flush : true, failOnError : true )

		management.addToPotentialChildren( management )
		management.addToPotentialChildren( affordability )
		management.addToPotentialChildren( humanActivity )
		management.save( flush : true, failOnError : true )

		sustainability.addToPotentialChildren( sustainability )
		sustainability.addToPotentialChildren( waterQuality )
		sustainability.save( flush : true, failOnError : true )

		humanActivity.addToPotentialChildren( humanActivity )
		humanActivity.addToPotentialChildren( waterQuality )
		humanActivity.save( flush : true, failOnError : true )

		problem.addToPotentialChildren( problem )
		problem.save( flush : true, failOnError : true )

	}

	private static final String PROBLEM_VARIABLE_CLASS        = "Problem Variables"
	private static final String WATER_QUALITY_VARIABLE_CLASS  = "Water Quality"
	private static final String HUMAN_ACTIVITY_VARIABLE_CLASS = "Human Activities"
	private static final String AFFORDABILITY_VARIABLE_CLASS  = "Affordability"
	private static final String SUSTAINABILITY_VARIABLE_CLASS = "Sustainability"
	private static final String MANAGEMENT_VARIABLE_CLASS     = "Management"

	protected void initVariables() {
		removeVariables()
		saveVariables( problemVariables,        VariableClass.findByName( PROBLEM_VARIABLE_CLASS ) );
		saveVariables( waterQualityVariables,   VariableClass.findByName( WATER_QUALITY_VARIABLE_CLASS ) );
		saveVariables( humanActivityVariables,  VariableClass.findByName( HUMAN_ACTIVITY_VARIABLE_CLASS ) );
		saveVariables( affordabilityVariables,  VariableClass.findByName( AFFORDABILITY_VARIABLE_CLASS ) );
		saveVariables( sustainabilityVariables, VariableClass.findByName( SUSTAINABILITY_VARIABLE_CLASS ) );
		saveVariables( managementVariables,     VariableClass.findByName( MANAGEMENT_VARIABLE_CLASS ) );
		addVariableStates()
	}

	protected List<Variable> getWaterQualityVariables() {
		[

			new Variable(
				label: "Flooding",
				readableLabel: "Flooding", ),

			new Variable(
				label: "SoilErosion",
				readableLabel: "Soil Erosion", ),

			new Variable(
				label: "RunoffQuantity",
				readableLabel: "Runoff Quantity", ),

			new Variable(
				label: "Agriculture",
				readableLabel: "Agriculture", ),

			new Variable(
				label: "SupplyQualitySediment",
				readableLabel: "Supply Quality (Sediment)", ),

			new Variable(
				label: "Sanitation",
				readableLabel: "Sanitation", ),

			new Variable(
				label: "AnimalWaste",
				readableLabel: "Animal Waste", ),

			new Variable(
				label: "OtherPollutants",
				readableLabel: "Other Pollutants", ),

			new Variable(
				label: "RunoffQualitySediment",
				readableLabel: "Runoff Quality (Sediment)", ),

			new Variable(
				label: "SupplyQuantity",
				readableLabel: "Supply Quantity", ),

			new Variable(
				label: "RunnoffQualityMicrobial",
				readableLabel: "Runnoff Quality (Microbial)", ),

			new Variable(
				label: "SupplyQualityMicrobial",
				readableLabel: "Supply Quality( Microbial)", ),

		]
	}

	protected List<Variable> getHumanActivityVariables() {
		[

			new Variable(
				label: "TreatmentSettling",
				readableLabel: "Treatment (Settling)", ),

			new Variable(
				label: "TreatmentChlorination",
				readableLabel: "Treatment (Chlorination)", ),

			new Variable(
				label: "TraditionalCustoms",
				readableLabel: "Traditional Customs / Culture", ),

			new Variable(
				label: "SocialDisputes",
				readableLabel: "Social Disputes (e.g. land, ethnic)", ),

			new Variable(
				label: "Education",
				readableLabel: "Education", ),

			new Variable(
				label: "CatchmentPopulationIncrease",
				readableLabel: "Catchment Population Increase", ),

			new Variable(
				label: "UrbanPopulationIncrease",
				readableLabel: "Urban Population Increase", ),

			new Variable(
				label: "LoggingArea",
				readableLabel: "Logging Area", ),

		]
	}

	protected List<Variable> getProblemVariables() {

		[
			new Variable(
				label: "WaterForHumanSurvival",
				readableLabel: "Water for Human Survival", ),

			new Variable(
				label: "EnvironmentHealth",
				readableLabel: "Environment/Ecosystem Health", ),

			new Variable(
				label: "WaterQuantity",
				readableLabel: "Water Quantity", ),

			new Variable(
				label: "WaterQuality",
				readableLabel: "Water Quality", ),


		]

	}

	protected List<Variable> getAffordabilityVariables() {
		[

			new Variable(
				label: "AvailabilityOfWater",
				readableLabel: "Access/Availability of Water", ),

			new Variable(
				label: "Electricity",
				readableLabel: "Electricity", ),

			new Variable(
				label: "HouseholdDemand",
				readableLabel: "Household Demand/Usage", ),

			new Variable(
				label: "Employment",
				readableLabel: "Employment", ),

			new Variable(
				label: "HouseholdIncome",
				readableLabel: "Household Income", ),

			new Variable(
				label: "CostOfElectricity",
				readableLabel: "Cost of Electricity", ),

			new Variable(
				label: "TreatmentAndInfrastructureCost",
				readableLabel: "Cost of Treatment and Infrastructure", ),

			new Variable(
				label: "SupplyCost",
				readableLabel: "Cost of Supply", ),

			new Variable(
				label: "WaterPrice",
				readableLabel: "Price of Water", ),

			new Variable(
				label: "Affordability",
				readableLabel: "Affordability", ),

		]
	}

	protected List<Variable> getSustainabilityVariables() {
		[

			new Variable(
				label: "LeakageAndLosses",
				readableLabel: "Leakage and Losses", ),

			new Variable(
				label: "ClimateVariability",
				readableLabel: "Climate Variability", ),

			new Variable(
				label: "GeologicalChange",
				readableLabel: "Geological Change", ),

			new Variable(
				label: "Rainfall",
				readableLabel: "Rainfall", ),

		]
	}

	protected List<Variable> getManagementVariables() {
		[

			new Variable(
				label: "Maintenance",
				readableLabel: "Maintenance", ),

			new Variable(
				label: "LeadershipAtAllLevels",
				readableLabel: "Leadership at all levels/sectors", ),

			new Variable(
				label: "PolicyAndPlanning",
				readableLabel: "Policy and Planning", ),

			new Variable(
				label: "LandTenureType",
				readableLabel: "Land Tenure Type", ),

			new Variable(
				label: "LandTenureRecognition",
				readableLabel: "Land Tenure Recognition", ),

			new Variable(
				label: "RoyaltyPayments",
				readableLabel: "Royalty Payments", ),

			new Variable(
				label: "LeaseAgreementPartner",
				readableLabel: "Lease Agreement/Management Partner", ),

			new Variable(
				label: "GovernmentManagement",
				readableLabel: "Government Management", ),

			new Variable(
				label: "DependabilityHhSupply",
				readableLabel: "Dependability of Hh Supply", ),

			new Variable(
				label: "WaterUseAwareness",
				readableLabel: "Water Use Awareness", ),

		]

	}

	protected AppProperties getProperties( ServletContext servletContext )
	{
		String explanatoryStatement = """
<h2>Demo survey</h2>
<p>

</p>

<img src="http://www.monash.edu.au/assets/images/template/monash-logo.png" alt="Monash university logo"/>
"""

		new AppProperties(
			adminEmail          : "peter.serwylo@monash.edu",
			url                 : "http://survey.infotech.monash.edu/water",
			title               : "Demo survey",
			delphiPhase         : 1,
			elicitationPhase    : AppProperties.ELICIT_2_RELATIONSHIPS,
			explanatoryStatement: explanatoryStatement
		)
	}

}
