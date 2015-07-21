package bn.elicitator.init.loaders
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
import bn.elicitator.init.DataLoader

import javax.servlet.ServletContext

/**
 * Uses variables (and relationships based on the "Car Insurance" Bayesian network:
 *
 *   Binder J, Koller D, Russell S, Kanazawa K (1997). "Adaptive Probabilistic Networks with Hidden Variables". Machine Learning, 29(2-3), 213-244.
 *      
 *   http://www.cs.huji.ac.il/site/labs/compbio/Repository/Datasets/insurance/insurance.htm
 */
class InsuranceDataLoader extends DataLoader {

	protected void initOther() {
		updateHomePage();
	}

	protected List<List<String>> getBnArcs() {
		[
			[ "Age", "GoodStudent" ],
			[ "Age", "SocioEcon" ],
			[ "Age", "RiskAversion" ],
			[ "Age", "SeniorTrain" ],
			[ "Age", "DrivingSkill" ],
			/*[ "Age", "MedCost" ],*/
			[ "RiskAversion", "SeniorTrain" ],
			[ "RiskAversion", "DrivHist" ],
			[ "RiskAversion", "DrivQuality" ],
			[ "RiskAversion", "MakeModel" ],
			[ "RiskAversion", "VehicleYear" ],
			[ "RiskAversion", "HomeBase" ],
			[ "RiskAversion", "AntiTheft" ],
			[ "SocioEcon", "GoodStudent" ],
			[ "SocioEcon", "AntiTheft" ],
			[ "SocioEcon", "HomeBase" ],
			[ "SocioEcon", "VehicleYear" ],
			[ "SocioEcon", "MakeModel" ],
			[ "SocioEcon", "RiskAversion" ],
			[ "SocioEcon", "OtherCar" ],
			[ "AntiTheft", "Theft" ],
			[ "HomeBase", "Theft" ],
			[ "Mileage", "CarValue" ],
			[ "Mileage", "Accident" ],
			[ "CarValue", "Theft" ],
			[ "CarValue", "ThisCarCost" ],
			[ "VehicleYear", "CarValue" ],
			[ "VehicleYear", "RuggedAuto" ],
			[ "VehicleYear", "Antilock" ],
			[ "VehicleYear", "Airbag" ],
			[ "MakeModel", "CarValue" ],
			[ "MakeModel", "RuggedAuto" ],
			[ "MakeModel", "Antilock" ],
			[ "MakeModel", "Airbag" ],
			[ "SeniorTrain", "DrivingSkill" ],
			[ "DrivingSkill", "DrivHist" ],
			[ "DrivingSkill", "DrivQuality" ],
			[ "DrivQuality", "Accident" ],
			[ "Accident", "ThisCarDam" ],
			[ "Accident", "OtherCarCost" ],
			[ "Accident", "IliCost" ],
			/*[ "Accident", "MedCost" ],*/
			[ "OtherCarCost", "PropCost" ],
			[ "ThisCarDam", "ThisCarCost" ],
			[ "ThisCarCost", "PropCost" ],
			[ "RuggedAuto", "ThisCarDam" ],
			[ "RuggedAuto", "OtherCarCost" ],
			/*[ "RuggedAuto", "Cushioning" ],*/
			[ "Antilock", "Accident" ],
			/*[ "Airbag", "Cushioning" ],
			[ "Cushioning", "MedCost" ],*/
			[ "Theft", "ThisCarCost" ],
		]
	}

	protected void updateHomePage() {
		String homeText = """
			<h2>How long should this take?</h2>
			<p>
				It should take approximately 20 mins to complete.
			</p>

			<h2>Who are you?</h2>
			<p>
				For the purpose of this study, you should put yourself in the shoes of somebody who runs an insurance company.
				It doesn't matter if you don't have experience dealing with this, we're just after your best guess for each question.
			</p>

			<h2>What does the insurance company want?</h2>
			<p>
				Like most businesses, this one wants to make money. 
				That means that you need to understand the clients you will be signing up, and how risky they are.
			</p>

			<h2>Where does this survey fit in?</h2>
			<p>
				We are building a model which identifies the factors involved in assessing clients. 
				Certain clients will be more risky than others, and therefore we will charge them a higher excess and premium.
			</p>
			<br />
			<p>
				We will ask you questions to ascertain how likely certain scenarios are.
			</p>

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
		if ( State.count() == 0 ) {
			addVariableStates()
		}
	}

	@Override
	protected Map<String,List<State>> getVariableStates() {

		[
		    "GoodStudent" : [
				new State( "True",  "Client is a <em>good student</em>" ),
				new State( "False", "Client is a <em>poor student</em>" ),
		    ],
		    "Age" : [
				new State( "Adolescent", "Client is a <em>young adult</em>" ),
				new State( "Adult",      "Client is an <em>adult</em>" ),
				new State( "Senior",     "Client is a <em>senior</em>" ),
		    ],
		    "SocioEcon" : [
				new State( "Prole",  "Client has a <em>lower socio-economic status</em>" ),
				new State( "Middle", "Client is from the <em>middle class</em>" ),
				/*new State( "UpperMiddle" ),*/
				new State( "Wealthy", "Client is from the <em>upper class</em>" ),
		    ],
		    "RiskAversion" : [
				new State( "Psychopath",  "Client is <em>extremely adventurous</em>"  ),
				new State( "Adventurous", "Client is <em>adventurous</em>" ),
				new State( "Normal",      "Client has <em>normal risk aversion</em>" ),
				new State( "Cautious",    "Client is <em>very cautious</em>" ),
		    ],
		    "VehicleYear" : [
				new State( "Current", "Client's car is a <em>current</em> model" ),
				new State( "Older",   "Client's car is an <em>older</em> model" ),
		    ],
		    "ThisCarDam" : [
				new State( "None",     "Client's car <em>doesn't get damaged</em>" ),
				new State( "Mild",     "Client's car <em>receives mild damage</em>" ),
				new State( "Moderate", "Client's car gets <em>moderately damaged</em>" ),
				new State( "Severe",   "Client's car gets <em>severely damaged</em>" ),
		    ],
		    "RuggedAuto" : [
				new State( "EggShell", "Client's car is <em>not rugged</em> at all" ),
				new State( "Football", "Client's car is <em>not particularly weak or strong</em>, just regular" ),
				new State( "Tank",     "Client's car is <em>particularly strong</em>" ),
		    ],
		    "Accident" : [
				new State( "None",     "Client will <em>not get into an accident</em>" ),
				new State( "Mild",     "Client will get themselves into a <em>mild accident</em>" ),
				new State( "Moderate", "Client will end up in a <em>moderate accident</em>" ),
				new State( "Severe",   "Client will get into a <em>severe accident</em>"  ),
		    ],
		    "MakeModel" : [
				new State( "SportsCar",  "Client's car is a <em>sports car</em>" ),
				new State( "Economy",    "Client's car is an <em>economy car</em>" ),
				new State( "FamilySedan", "Client's car is a <em>family sedan</em>" ),
				new State( "Luxury",      "Client's car is a <em>luxury car</em>" ),
				/*new State( "SuperLuxury" ),*/
		    ],
		    "DrivQuality" : [
				new State( "Poor",      "Quality of clients driving is <em>poor</em>" ),
				new State( "Normal",    "Quality of clients driving is <em>normal</em>" ),
				new State( "Excellent", "Quality of clients driving is <em>excellent</em>" ),
		    ],
		    "Mileage" : [
				new State( "FiveThou",   "Client's car has driven <em>less than 10,000km</em>" ),
				new State( "TwentyThou", "Client's car has driven <em>between 10,000km and 40,000km</em>" ),
				new State( "FiftyThou",  "Client's car has driven <em>between 40,000km and 100,000km</em>"  ),
				new State( "Domino",     "Client's car has driven <em>over 100,000km</em>" ),
		    ],
		    "Antilock" : [
				new State( "True",  "Client's car <em>has anti-lock brakes</em> installed" ),
				new State( "False", "Client's car <em>does not have anti-lock</em> brakes" ),
		    ],
		    "DrivingSkill" : [
				new State( "SubStandard", "Client <em>doesn't</em> have very good <em>driving skills</em>" ),
				new State( "Normal",      "Client has <em>normal driving skills</em>" ),
				new State( "Expert",      "Client has <em>excellent driving skills</em>" ),
		    ],
		    "SeniorTrain" : [
				new State( "True",  "Client <em>has</em> received <em>advanced driver training</em>" ),
				new State( "False", "Client <em>does not have</em> any <em>advanced driver training</em> beyond a license test" ),
		    ],
		    "ThisCarCost" : [
				new State( "Thousand",    "Client will <em>claim less than \$1,000</em> to fix <em>their</em> car" ),
				new State( "TenThou",     "Client will <em>claim between \$1,000 and \$10,000</em> to fix <em>their</em> car" ),
				new State( "HundredThou", "Client will <em>claim between \$10,000 and \$100,000</em> to fix <em>their</em> car" ),
				new State( "Million",     "Client will <em>claim over \$100,000</em> to fix <em>their</em> car" ),
			],
		    "Theft" : [
				new State( "True",  "Client's car <em>will</em> get stolen" ),
				new State( "False", "Client's car <em>will not</em> get stolen" ),
		    ],
		    "CarValue" : [
				new State( "Thousand",    "Client's car is <em>worth less than \$1,000</em> (at time of insuring)" ),
				new State( "TenThou",     "Client's car is <em>worth between \$1,000 and \$10,000</em> (at time of insuring)" ),
				new State( "HundredThou", "Client's car is <em>worth between \$10,000 and \$100,000</em> (at time of insuring)" ),
				new State( "Million",     "Client's car is <em>worth more than \$100,000</em> (at time of insuring)" ),
		    ],
		    "HomeBase" : [
				new State( "Secure", "Client usually leaves their car in <em>secure parking</em>" ),
				new State( "City",   "Client usually parks their car in <em>city streets</em>" ),
				new State( "Suburb", "Client usually parks their car in <em>suburban streets</em>" ),
				new State( "Rural",  "Client usually parks their car in <em>rural streets</em>"  ),
		    ],
		    "AntiTheft" : [
				new State( "True",  "Client's car <em>has anti-theft device</em> installed"  ),
				new State( "False", "Client's car <em>does not have</em> any <em>anti-theft devices</em> installed" ),
		    ],
		    "PropCost" : [
				new State( "Thousand",    "Client will cause <em>less than \$1,000</em> in property damage" ),
				new State( "TenThou",     "Client will cause <em>between \$1,000 and \$10,000</em> in property damage" ),
				new State( "HundredThou", "Client will cause <em>between \$10,000 and \$100,000</em> in property damage" ),
				new State( "Million",     "Client will cause <em>over \$100,000</em> in property damage" ),
		    ],
		    "OtherCarCost" : [
				new State( "Thousand",    "Client will <em>claim less than \$1,000</em> to fix <em>someone else's</em> car involved in an accident" ),
				new State( "TenThou",     "Client will <em>claim between \$1,000 and \$10,000</em> to fix <em>someone else's</em> car involved in an accident" ),
				new State( "HundredThou", "Client will <em>claim between \$10,000 and \$100,000</em> to fix <em>someone else's</em> car involved in an accident" ),
				new State( "Million",     "Client will <em>claim over \$100,000</em> to fix <em>someone else's</em> car involved in an accident" ),
		    ],
		    "OtherCar" : [
				new State( "True",  "Client <em>owns multiple cars</em>"  ),
				new State( "False", "Car being insured is <em>client's only car</em>" ),
		    ],
			/*"MedCost" : [
				new State( "Thousand"    ),
				new State( "TenThou"     ),
				new State( "HundredThou" ),
				new State( "Million"     ),
			],
			"Cushioning" : [
				new State( "Poor"      ),
				new State( "Fair"      ),
				new State( "Good"      ),
				new State( "Excellent" ),
			],*/
			"Airbag" : [
				new State( "True",  "Clients car <em>has airbags</em>" ),
				new State( "False", "Clients car <em>doesn't have airbags</em>" ),
			],
			"IliCost" : [
				new State( "Thousand",    "Client will <em>claim less than \$1,000</em> to fix <em>buildings or property</em> damaged in an accident" ),
				new State( "TenThou",     "Client will <em>claim between \$1,000 and \$10,000</em> to fix <em>buildings or property</em> damaged in an accident" ),
				new State( "HundredThou", "Client will <em>claim between \$10,000 and \$100,000</em> to fix <em>buildings or property</em> damaged in an accident" ),
				new State( "Million",     "Client will <em>claim over \$100,000</em> to fix <em>buildings or property</em> damaged in an accident" ),
			],
			"DrivHist" : [
				new State( "Zero", "Client has <em>zero</em> previous insurance claims" ),
				new State( "One",  "Client has <em>had one</em> previous insurance claim" ),
				new State( "Many", "Client has <em>made several</em> insurance claims in the past" ),
			],
		]

	}

	protected List<Variable> getBackgroundVariables() {

		[
			new Variable(
				label: "GoodStudent",
				readableLabel: "Good student",
				usageDescription: "Does the fact that the client was a good student when the learnt to drive <em>directly</em> influence any of the following?",
				description: "Whether the student was attentive as they learnt to drive. For example, did they ace their driving test, or just barely pass?" ),

			new Variable(
				label: "Age",
				usageDescription: "Does the [This] <em>directly</em> influence any of these?",
				readableLabel: "Age of the client" ),

			new Variable(
				label: "SocioEcon",
				readableLabel: "Socio-economic status",
				usageDescription: "Does the [This] of the client <em>directly</em> influence any of these?",
				description: "The background that the client comes from. Can include multiple factors, such as their occupation, income or education." ),

			new Variable(
				label: "RiskAversion",
				readableLabel: "Risk aversion",
				usageDescription: "Does the [This] of the client <em>directly</em> influence any of these?",
				description: "How cautious/adventurous the client is. " ),

			new Variable(
				label: "VehicleYear",
				readableLabel: "Vehicle age",
				usageDescription: "Does the age of the client's vehicle <em>direct</em> influence any of these?",
				description: "The age of the client's vehicle." ),

			new Variable(
				label: "ThisCarDam",
				readableLabel: "Damage to client's car",
				usageDescription: "If a client's car is damaged, does it <em>directly</em> influence any of these?",
				description: "The damage acquired by the car owned by the client, in the event of an accident (none, mild, moderate or severe)." ),

			new Variable(
				label: "RuggedAuto",
				readableLabel: "Car strength",
				usageDescription: "Does the strength of the client's car <em>directly</em> influence any of these?",
				description: "Whether the client's car would break like an eggshell or hold together like a tank in a crash." ),

			new Variable(
				label: "Accident",
				readableLabel: "Accident",
				usageDescription: "If the client becomes involved in an accident, will it <em>directly</em> influence any of these?",
				description:  "Whether or not the client will be involved in an accident." ),

			new Variable(
				label: "MakeModel",
				readableLabel: "Car type",
				usageDescription: "Does the type of the client's car <em>directly</em> influence any of these?",
				description: "General category of the car, such as sports car, economy car, family sedan or a luxury car. This is defined by the body of the car (e.g. hatchback, station wagon) and the features it has." ),

			new Variable(
				label: "DrivQuality",
				readableLabel: "Quality of client's driving",
				usageDescription: "Does the [This] <em>directly</em> influence any of these?",
				description:  "Whether the client is a safe driver, or prone to accidents." ),

			new Variable(
				label: "Mileage",
				readableLabel: "Mileage",
				usageDescription: "Does the [This] of the client's car <em>directly</em> influence any of these?",
				description: "The distance the client's car driven since the engine was built." ),

			new Variable(
				label: "Antilock",
				readableLabel: "Car has antilock brakes",
				usageDescription: "Do any of the following variables <em>direct</em> influence the chance of a client's Car having antilock brakes?",
				description: "Whether the client's car has antilock breaks installed or not. These help prevent uncontrollable skidding." ),

			new Variable(
				label: "DrivingSkill",
				readableLabel: "Driving skill",
				usageDescription: "Does the client's [This] <em>directly</em> influence any of these?",
				description: "The client's ability to drive a vehicle in varying conditions, such as rain or traffic congestion." ),

			new Variable(
				label: "SeniorTrain",
				readableLabel: "Advanced driver training",
				usageDescription: "If the client has undertaken [This], does it <em>directly</em> influence any of these?",
				description: "Whether the client has undergone additional training after obtaining their license. Some companies may refer to this as \"Skilled Driving\" or \"Defensive Driving\" courses." ),

			new Variable(
				label: "CarValue",
				readableLabel: "(Monetary) value of client's car",
				usageDescription: "Does the [This] <em>directly</em> influence any of these?",
				description: "The amount of money that the client could sell their car for."),

			new Variable(
				label: "ThisCarCost",
				readableLabel: "Cost to insurer to fix client's car",
				usageDescription: "Does the [This] <em>directly</em> influence any of these?",
				description: "The damage cost for the client's car, in the event of an accident."),

			new Variable(
				label: "HomeBase",
				readableLabel: "Primary parking location",
				usageDescription: "Does the [This] of the client's car <em>directly</em> influence any of these?",
				description: "The location the client's car is usually parked. For example, in secure parking/garage, or on a city, suburban or rural street." ),

			new Variable(
				label: "AntiTheft",
				usageDescription: "Does the installation of an Anti Theft device in the client's car <em>directly</em> influence any of these?",
				readableLabel: "Anti theft device installed" ),

			new Variable(
				label: "Theft",
				readableLabel: "Car stolen",
				usageDescription: "If the client's car was stolen, would it <em>directly</em> influence any of these?",
				description: "Whether or not the client's car will get stolen." ),

			new Variable(
				label: "Other Car Cost",
				readableLabel: "Cost to insurer for other cars",
				usageDescription: "Does the [This] <em>directly</em> influence any of these?",
				description: "In the event of an accident involving other cars, this is the amount the client's insurer must pay for damage to them." ),

			new Variable(
				label: "OtherCar",
				readableLabel: "Other cars involvement",
				usageDescription: "In the event of an accident, if other cars were involved, would it <em>directly</em> influence any of these?",
				description: "Whether other cars are involved in an accident with the client's car." ),

			/*
			Removed for same reason as the MedicalCost: Government deals with medical insurance in Victoria, Australia.
			In the paper by Binder et al, this pretty much just influences medical cost, and is influenced by two other
			latent variables. As such, I think that it wont effect the overall structure much to remove it.
			new Variable(
				label: "Cushioning",
				readableLabel: "Cushioning",
				description: "The level of cushioning the client's car provides for the people inside in the event of an accident." ),
			*/

			new Variable(
				label: "Airbag",
				readableLabel: "Airbags",
				usageDescription: "If the clients car has Airbags, would it <em>directly</em> influence any of these?",
				description: "Whether or not there are any airbags installed in the client's car." ),

			new Variable(
				label: "DrivHist",
				readableLabel: "Driver history",
				usageDescription: "Does the client's driving history <em>directly</em> influence any of these?",
				description: "If the client has a history of insurance claims" ),
		]

	}

	protected List<Variable> getProblemVariables() {

		[

			/*
			Not included for Victoria (Australia) study, because  medical bills are dealt with by
			the government collateArcs TAC, and not the client's insurance company.

			 new Variable(
				 label: "MedCost",
				 readableLabel: "Expected Medical Costs",
				 description:
				 	"The amount of money the insurance company would be expected to pay to this client for medical costs arising from an accident." ),
			 */

			new Variable(
					label: "IliCost",
					readableLabel: "Cost to insurer for liability/property",
					usageDescription: "Does the [This] <em>directly</em> influence any of these?",
					description: "The total cost to the insurer for 3rd party property damage, due to an accident caused by the client." ),

			new Variable(
				label: "PropCost",
				readableLabel: "Total cost to insurer for all cars",
				usageDescription: "Does the [This] <em>directly</em> influence any of these?",
				description: "The total cost to the insurer for fixing all cars involved in an accident caused by the client." ),



		]

	}

	protected List<Variable> getMediatingVariables() { [] }

	protected List<Variable> getSymptomVariables()   { [] }

	protected AppProperties getProperties( ServletContext servletContext )
	{
		String explanatoryStatement = """
<style>
	ul.list {
        list-style: disc inside;
        padding-left: 1em;
	}

	ol.list li {
        list-style: decimal outside;
	}

    ul.list, ol.list li {
        margin-bottom: 0.5em;
    }
</style>

<h2>Car Insurance Risk Analysis</h2>
<p>
    My name is Peter Serwylo and this study is part of my PhD research project at the Caulfield School of Information
    Technology, Monash University.
    Dr Grace Rumantir and Professor Frada Burstein are my research supervisors.
    You are invited to take part in this study.
</p>

<h2><a name="aim">The aim/purpose of the research</a></h2>
<p>
The research has two aims:
</p>

<ol class="list">
	<li>
		To gathering knowledge from motorists on what factors likely to increase the chance of an accident and the total
		payout claim by a car insurance holder. This knowledge will be used to build a model for assessing risk of a
		potential client when selling car insurance.
	</li>
	<li>
		To test a new online knowledge elicitation tool that I have built for the purpose of building risk assessment
		models in an automated manner. Traditionally, gathering knowledge for such a model would require you and I to
		meet for a face-to-face interview. I would then manually transcribe the interview and then analyse the
		transcripts to build the model.
	</li>
</ol>


<h2><a name="benefits">Possible benefits</a></h2>
<p>
	There are a few benefits in conducting knowledge-elicitation process online vis-à-vis through face-to-face interviews.
</p>
<p>
	It requires less time:
</p>
<ul class="list">
	<li>
		For <em>you</em> to contribute to the research by imparting your knowledge.
	</li>
	<li>
		For <em>me</em> to analyse the knowledge and create the risk assessment model generated from the study.
	</li>
</ul>
<p>
	It also requires less organisation from all involved as the process can be conducted online at each participant’s
	convenience.
</p>
<p>
	All these benefits will enable more participants to take part which results in better more informed model.
</p>
<p>
	This part of the PhD research project uses the car insurance domain to build a risk assessment model as the domain
	already has a model which we can can compare with.
	We’d like to test if the model resulting from this online survey is similar to the existing model.
	We can then draw a conclusion whether or not this proposed way of knowledge elicitation is useful for building risk
	assessment models in other domains.
</p>

<h2><a name="involves">What does the research involve?</a></h2>
<p>
	The study involves a short online survey. It consists of questions about what you think causes car accidents, and
	how this should affect decisions to sell car insurance to clients. The research will be conducted online, so that
	you can <a href="#time">complete it at your leisure</a>.
</p>
<p>
	After you complete the primary survey (questions about car insurance), you will also be asked a few short questions
	about how you felt the process went (questions about the survey process itself).
</p>

<h2><a name="time">How much time will the research take?</a></h2>
<p>
	The survey should take around 20-30 minutes to complete. You don’t have to complete the whole survey all at once, you
	can take breaks and the system will save your results from last time and continue from where you left off.
</p>
<p>
	To be eligible for the <a href="#payment">prize draw</a>, however, you will need to complete the survey
	<em>before the 25th of October 2013.</em>
</p>

<h2><a name="discomfort">Inconvenience/discomfort</a></h2>
<p>
	There is no anticipated risk of inconvenience or discomfort while completing this survey.
</p>

<h2><a name="payment">Prizes / Payment</a></h2>
<p>
	Upon completion of the survey, you will be eligible to enter a draw for a \$100 prize. More information is provided
	on the following page.
</p>

<h2><a name="withdraw">Withdrawing from the research</a></h2>
<p>
	This study is voluntary and you are under no obligation to participate. If you wish to withdraw, please contact
	Peter Serwylo (peter.serwylo@monash.edu.au or 03 9903 2556).
</p>

<h2><a name="confidentiality">Confidentiality</a></h2>
<p>
    During the online survey, you will be given your own username and password to access your results. This is to prevent
	other participants or people who stumble upon the website from looking at your results.
</p>
<p>
	Once you have completed the survey and feedback questions, the data will be de-identified. At no point during any
	publication of results will your name be linked with any data you submitted. If any quotes from you are published,
	they will be attributed to a pseudonym of the form "Participant 1", and there will be no way to identify who this
	pseudonym belongs to.
</p>

<h2><a name="data-storage">Storage of data</a></h2>
<p>
    Data collected will be stored in accordance with Monash University regulations, on a secure computer within the
	Monash network for a period of five years. A report of the study may be submitted for publications, but individual
	<a href="#confidentiality">participants will not be identifiable</a> in such a report.
</p>

<h2><a name="results">Results</a></h2>
<p>
    If you would like to be informed of the research findings at the end of the study, they will be available
    <a href="http://survey.infotech.monash.edu/cars/results">online here</a>.
	The findings will be accessible for at that web address for at least six months after the completion of the study.
</p>

<p>
	Alternatively, you can contact:
	<div class="contact-details">
		<div class="name">Peter Serwylo</div>
		<div class="school">Caulfield School of IT, Monash University</div>
		<div class="phone">03 9903 2556</div>
		<div class="email"><a href="mailto:peter.serwylo@monash.edu">peter.serwylo@monash.edu</a></div>
	</div>
</p>

<h2><a name="contact">Contacting the researchers</a></h2>
<p>
    If you would like, you can contact any of the researchers about any aspect of the study:
</p>
<div class="contact-details">
	<div class="name">Dr Grace Rumantir</div>
	<div class="school">Caulfield School of IT, Monash University</div>
	<div class="phone">03 9903 1965</div>
	<a href="mailto:grace.rumantir@monash.edu">grace.rumantir@monash.edu</a>
</div>

<div class="contact-details">
    <div class="name">Prof Frada Burstein</div>
	<div class="school">Caulfield School of IT, Monash University</div>
    <div class="phone">03 9903 2011</div>
	<div class="email"><a href="mailto:frada.burstein@monash.edu">frada.burstein@monash.edu</a></div>
</div>

<div class="contact-details">
    <div class="name">Peter Serwylo</div>
	<div class="school">Caulfield School of IT, Monash University</div>
    <div class="phone">03 9903 2556</div>
	<div class="email"><a href="mailto:peter.serwylo@monash.edu">peter.serwylo@monash.edu</a></div>
</div>

<h2><a name="complaints">Complaints</a></h2>
<p>
    If you have a complaint concerning the manner in which this research <em>CF12/1826 - 2012001011</em> is being
	conducted, please contact:
</p>

<div class="contact-details">
	<div class="name">Executive Officer</div>
	<div class="school">Monash University Human Research Ethics Committee (MUHREC)</div>
	<div class="address">Building 3e  Room 111</div>
	<div class="address">Research Office</div>
	<div class="address">Monash University VIC 3800</div>
	<div class="phone">Ph: +61 3 9905 2052</div>
	<div class="fax">Fax: +61 3 9905 3831</div>
	<div class="email"><a href="mailto:muhrec@monash.edu">muhrec@monash.edu</a></div>
</div>

<br />
<br />
<br />

<p>Thank you,</p>

<p>Peter Serwylo</p>

<img src="http://www.monash.edu.au/assets/images/template/monash-logo.png" alt="Monash university logo"/>
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
