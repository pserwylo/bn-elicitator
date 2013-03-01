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
				description: "Was the student attentive during driver training? Did they barely pass or get a perfect score?" ),

			new Variable(
				label: "Age",
				readableLabel: "Age of client" ),

			new Variable(
				label: "SocioEcon",
				readableLabel: "Socio-economic Status",
				description: "What sort of background does the client come from?" ),

			new Variable(
				label: "RiskAversion",
				readableLabel: "Risk Aversion",
				description: "Is the client likely to take risks when driving? Or drive more carefully and conservatively?" ),

			new Variable(
				label: "VehicleYear",
				readableLabel: "Vehicle Year",
				description: "When was the clients vehicle made? Recently or some time ago?" ),

			new Variable(
				label: "ThisCarDam",
				readableLabel: "Damage to clients car",
				description: "The damage acquired by the car owned by the client." ),

			new Variable(
				label: "RuggedAuto",
				readableLabel: "Car Strength",
				description: "Will the clients car break like an eggshell or stay together like a tank?" ),

			new Variable(
				label: "Accident",
				readableLabel: "Accident",
				description:  "Will the client have an accident? Will it be mild or severe?" ),

			new Variable(
				label: "MakeModel",
				readableLabel: "Car Make and Model",
				description:  "Is the clients car a sports Car, economy, family sedan or a luxury car?" ),

			/*
			new Variable(
				label: "DrivQuality",
				readableLabel: "DrivQuality",
				description:  "Dunno... " ),
			*/

			new Variable(
				label: "Mileage",
				readableLabel: "Mileage",
				description: "How far has the clients car driven since the engine was built?" ),

			new Variable(
				label: "Antilock",
				readableLabel: "Car has Antilock Brakes",
				description: "Does the clients car have antilock breaks installed? This feature helps prevent uncontrollable skidding." ),

			new Variable(
				label: "DrivingSkill",
				readableLabel: "Driving Skill",
				description: "Is the client a good driver? Do they have the skills necessary to drive properly?" ),

			/*
			new Variable(
				label: "SeniorTrain",
				readableLabel: "SeniorTrain",
				description: "Dunno..." ),
			*/

			new Variable(
				label: "ThisCarCost",
				readableLabel: "Cost of Clients Car" ),

			new Variable(
				label: "HomeBase",
				readableLabel: "Place of Residence",
				description: "Where does the car live, Secure parking, City, Rural, Street parking?" ),

			new Variable(
				label: "AntiTheft",
				readableLabel: "Anti Theft Device Installed" ),

			new Variable(
				label: "Other Car Cost",
				readableLabel: "Cost of the other car",
				description: "If in an accident with another car, how much is the other car worth?" ),

			new Variable(
				label: "OtherCar",
				readableLabel: "Other Car Involvement",
				description: "Was another car involved in an accident?" ),

			/*
			new Variable(
				label: "Cushioning",
				readableLabel: "Cushioning"
			*/

			new Variable(
				label: "Airbag",
				readableLabel: "Airbag",
				description: "Is there any airbags installed in the clients car?" ),

			/*
			new Variable(
				label: "ILiCost",
				readableLabel: "Liability Costs",
				variableClass: VariableClass.problem
			*/

			new Variable(
				label: "DrivHist",
				readableLabel: "Driver History",
				description: "If the client has a history of insurance claims" ),
		]

	}

	protected List<Variable> getProblemVariables() {

		[

			new Variable(
				label: "PropCost",
				readableLabel: "Expected Property Cost",
				description:
					"The amount of money which you (as the insurance company) would expect to pay in response to this having an accident (e.g. crashing into a car or building).\n\n" +
					"The more you would expect to pay, the more excess and premium you should charge them to cater for that risk." ),

			new Variable(
				label: "MedCost",
				readableLabel: "Expected Medical Costs",
				description:
					"The amount of money which you (as the insurance company) would expect to pay to this client for medical costs.\n\n" +
					"The more you would expect to pay to them in medical costs, the more excess and premium you should charge them to cater for that risk." ),

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

		new AppProperties(
			adminEmail          : "peter@serwylo.com",
			url                 : "http://firstaid.infotech.monash.edu/survey/run",
			title               : "The car insurance company",
			delphiPhase         : 1,
			elicitationPhase    : AppProperties.ELICIT_2_RELATIONSHIPS,
			explanatoryStatement: explanatoryStatement
		)
	}

}
