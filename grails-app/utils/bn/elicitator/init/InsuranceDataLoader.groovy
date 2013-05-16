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
import javax.servlet.ServletContext

class InsuranceDataLoader extends DataLoader {

	protected void initOther() {
		updateHomePage();
	}

	protected void updateHomePage() {
		String homeText = """
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

			<div style='text-align: center; margin-top: 2em;'>
				<p>
					Does this sound like something you can help with?
					<br />
					<button class='big' onclick='document.location = "[serverURL]/elicit"'>Participate</button>
				</p>
			</div>
		"""

		ContentPage page = ContentPage.findByAlias( ContentPage.HOME )
		page.content = replaceContentPlaceholders( homeText )
		page.save( failtOnError : true, flush : true )

	}

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
<h2>Car Insurance Risk Analysis</h2>
<p>
    My name is Peter Serwylo and I am conducting a research project with Dr Grace Rumantir and Professor Frada Burstein
    from the Caulfield School of Information Technology, Monash University.
    It is being conducted as part of my PhD research.
    You are invited to take part in this study.
</p>

<h2><a name="aim">The aim/purpose of the research</a></h2>
<p>
	There are two aims for this research.
	From the perspective of assessing risk when selling car insurance, it is to build a model to help
	predict which clients are likely to cost the insurer more.
	It will do this by gathering knowledge from people such as yourself about what factors are likely
	to increase the chance of an accident and the total payout required for a client.
</p>
<p>
	The other goal is to investigate different methods to use for building this model. Traditionally, gathering
	knowledge for such a model would require you and I to meet for a face-to-face interview. I would then manually
	transcribe the interview and then analyse the transcripts to build the model. This research project aims to elicit
	the same knowledge from you, but in a way which doesn’t require individual meetings for each participant. To do
	this, I am researching the practicality of eliciting knowledge from people like you from an online system.
</p>

<h2><a name="benefits">Possible benefits</a></h2>
<p>
	Conducting the knowledge-elicitation process online rather than in a one-to-one interview means that it takes
	less time for you to contribute to the research by imparting your knowledge. It also means less time for me to
	analyse the knowledge you have contributed and use it to build the model for assessing risk. It requires
	less organisation from you and I, due to the fact that you can attend to the online system at your leisure (you
	don’t have to complete the entire process all at once). Finally, all of these benefits culminate in allowing more
	participants to contribute their knowledge to the project. The more participants, the better, because the model will
	include a broader range of knowledge.
</p>
<p>
	Although a model for assessing risk when providing insurance already exists, this survey is being conducted to
	see if the result from the online survey will align with this pre-existing model. If so, then that will
	provide evidence that this is a valuable technique for producing these type of models into the future.
</p>

<h2><a name="involves">What does the research involve?</a></h2>
<p>
	The study involves a short series of online surveys based on the Delphi method. This is a method whereby after
	each participants has completed the survey once, the results are summarised. These summarised results are presented
	to you as you complete the survey a second time, giving you the option to revise your answers based on the
	collective answers given by all the other participants.
</p>
<p>
	The research will be conducted online, so that you can <a href="#time">complete it at your leisure</a>.
</p>
<p>
	After you complete the primary survey (questions about car insurance), you will also be asked a
	few short questions about how you felt the process went (questions about the survey process itself) so that I can
	evaluate whether it was worthwhile eliciting knowledge from you using this online system in preference to
	person-to-person interviews.
</p>

<h2><a name="time">How much time will the research take?</a></h2>
<p>
	You will be asked to complete the survey three, and each time should take around 10-15 minutes to complete.
	Each round of the survey will last for one week. If you have not completed the survey after five days, the
	researchers will send you an email to remind you (if you don't want to receive this email, please contact
	peter.serwylo@monash.edu and ask to be removed from the email list).
	Remember that you don’t have to complete the whole survey all at once, the system will save your results 
	from last time and continue from where you left off.
</p>

<h2><a name="discomfort">Inconvenience/discomfort</a></h2>
<p>
	There is no anticipated risk of inconvenience or discomfort while completing this survey.
</p>

<h2><a name="payment">Payment</a></h2>
<p>
    If you continue, you will be completing this survey on a voluntary basis, with no monetary reward. I thank you for
	contributing your time and expertise towards this study.
</p>

<h2><a name="withdraw">Withdrawing from the research</a></h2>
<p>
	As this study is voluntary and you are under no obligation to participate. If you wish to withdraw, please contact
	Peter Serwylo (peter.serwylo@monash.edu.au or 03 9903 2556).
</p>

<h2><a name="confidentiality">Confidentiality</a></h2>
<p>
    During the online survey, you will have your own username and password to access your results. This is to prevent
	other participants or people who stumble upon the website from looking at your results.
</p>
<p>
	Once you have completed the survey, including questions at the end regarding your experience completing the survey,
	the data will be deidentified. At no point during any publication of results will your name be linked with any data
	you submitted. If any quotes from you are published, they will be attributed to a pseudonym of the form
	“Participant 1”, and there will be no way to identify who this pseudonym belongs to.
</p>

<h2><a name="data-storage">Storage of data</a></h2>
<p>
    Data collected will be stored in accordance with Monash University regulations, on a secure computer within the
	Monash network for a period of five years. A report of the study may be submitted for publication, but individual
	<a href="#confidentiality">participants will not be identifiable</a> in such a report.
</p>

<h2><a name="results">Results</a></h2>
<p>
    If you would like to be informed of the research findings at the end of the study, they will be available
    <a href="http://firstaid.infotech.monash.edu/survey/run/results">online here</a>.
	The findings will be accessible for at that web address for at least six months after the completion of the study.
</p>

<p>
	Alternatively, you can contact:
	<div class="contact-details">
		<div class="name">Peter Serwylo</div>
		<div class="school">Caulfield School of IT, Monash University</div>
		<div class="phone">0431 608 014 or 03 9903 2556</div>
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
    <div class="phone">0431 608 014 or 03 9903 2556</div>
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
