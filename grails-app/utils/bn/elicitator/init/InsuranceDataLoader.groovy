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
import bn.elicitator.init.DataLoader;
import javax.servlet.ServletContext;

class InsuranceDataLoader extends DataLoader {

	protected List<Variable> getBackgroundVariables() {

		[
			new Variable(
				label: "GoodStudent",
				readableLabel: "Good Student",
				description: "Whether the student was attentive as they learnt to drive. For example, did they ace their driving test, or just barely pass?" ),

			new Variable(
				label: "Age",
				readableLabel: "Age of the client" ),

			new Variable(
				label: "SocioEcon",
				readableLabel: "Socio-economic Status",
				description: "The background that the client comes from. Can include multiple factors, such as their occupation, income or education." ),

			new Variable(
				label: "RiskAversion",
				readableLabel: "Risk Aversion",
				description: "How cautious/adventurous the client is. " ),

			new Variable(
				label: "VehicleYear",
				readableLabel: "Vehicle Age",
				description: "The age of the clients vehicle." ),

			new Variable(
				label: "ThisCarDam",
				readableLabel: "Damage to clients car",
				description: "The damage acquired by the car owned by the client, in the event of an accident (none, mild, moderate or severe)." ),

			new Variable(
				label: "RuggedAuto",
				readableLabel: "Car Strength",
				description: "Whether the clients car would break like an eggshell or hold together like a tank in a crash." ),

			new Variable(
				label: "Accident",
				readableLabel: "Accident",
				description:  "Whether or not the client will have an accident." ),

			new Variable(
				label: "MakeModel",
				readableLabel: "Car type",
				description: "General category of the car, such as sports car, economy car, family sedan or a luxury car. This is defined by the body of the car (e.g. hatchback, station wagon) and the features it has." ),

			new Variable(
				label: "DrivQuality",
				readableLabel: "Quality of Clients Driving",
				description:  "Whether the client is a safe driver, or prone to accidents." ),

			new Variable(
				label: "Mileage",
				readableLabel: "Mileage",
				description: "The distance the clients car driven since the engine was built." ),

			new Variable(
				label: "Antilock",
				readableLabel: "Car has Antilock Brakes",
				description: "Whether the clients car has antilock breaks installed or not. These help prevent uncontrollable skidding." ),

			new Variable(
				label: "DrivingSkill",
				readableLabel: "Driving Skill",
				description: "The clients ability to drive a vehicle in varying conditions, such as rain or traffic congestion." ),

			new Variable(
				label: "SeniorTrain",
				readableLabel: "Advanced Driver Training",
				description: "Whether the client has undergone additional training after obtaining their license. Some companies may refer to this as \"Skilled Driving\" or \"Defensive Driving\" courses." ),

			new Variable(
				label: "ThisCarCost",
				readableLabel: "Cost to Insurer for Client's Car",
				description: "The damage cost for the client's car, in the event of an accident."),

			new Variable(
				label: "HomeBase",
				readableLabel: "Primary Parking Location",
				description: "The location the client's car is usually parked. For example, in secure parking/garage, or on a city, suburban or rural street." ),

			new Variable(
				label: "AntiTheft",
				readableLabel: "Anti Theft Device Installed" ),

			new Variable(
				label: "Theft",
				readableLabel: "Car Stolen",
				description: "Whether or not the clients car will get stolen." ),

			new Variable(
				label: "Other Car Cost",
				readableLabel: "Cost to Insurer for Other Cars",
				description: "In the event of an accident involving other cars, this is the amount the client's insurer must pay for damage to them." ),

			new Variable(
				label: "OtherCar",
				readableLabel: "Other Cars Involvement",
				description: "Whether other cars are involved in an accident with the client's car." ),

			/*
			Removed for same reason as the MedicalCost: Government deals with medical insurance in Victoria, Australia.
			In the paper by Binder et al, this pretty much just influences medical cost, and is influenced by two other
			latent variables. As such, I think that it wont effect the overall structure much to remove it.
			new Variable(
				label: "Cushioning",
				readableLabel: "Cushioning",
				description: "The level of cushioning the clients car provides for the people inside in the event of an accident." ),
			*/

			new Variable(
				label: "Airbag",
				readableLabel: "Airbag",
				description: "Whether or not there are any airbags installed in the clients car." ),

			new Variable(
				label: "DrivHist",
				readableLabel: "Driver History",
				description: "If the client has a history of insurance claims" ),
		]

	}

	protected List<Variable> getProblemVariables() {

		[

			/*
			Not included for Victoria (Australia) study, because  medical bills are dealt with by
			the government run TAC, and not the clients insurance company.

			 new Variable(
				 label: "MedCost",
				 readableLabel: "Expected Medical Costs",
				 description:
				 	"The amount of money the insurance company would be expected to pay to this client for medical costs arising from an accident." ),
			 */

			new Variable(
					label: "ILiCost",
					readableLabel: "Cost to Insurer for Liability/Property",
					description:
						"The total cost to the insurer for 3rd party property damage, due to an accident caused by the client." ),

			new Variable(
				label: "PropCost",
				readableLabel: "Total Cost to Insurer for All Cars",
				description:
					"The total cost to the insurer for fixing all cars involved in an accident caused by the client." ),



		]

	}

	protected List<Variable> getMediatingVariables() { [] }

	protected List<Variable> getSymptomVariables()   { [] }

	protected AppProperties getProperties( ServletContext servletContext )
	{
		String explanatoryStatement = """
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
					<li>All of the above</li>
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
				You will be presented with a list of variables, and each one will be potentially influenced by other
				variables. You will be asked which variables you think influence others.
			</p>
		"""

		new AppProperties(
			adminEmail          : "peter.serwylo@monash.edu",
			url                 : "http://survey.infotech.monash.edu/insurance",
			title               : "The car insurance company",
			delphiPhase         : 1,
			elicitationPhase    : AppProperties.ELICIT_2_RELATIONSHIPS,
			explanatoryStatement: explanatoryStatement
		)
	}

}
