package bn.elicitator.init

import bn.elicitator.feedback.Option
import bn.elicitator.feedback.Question

class FeedbackQuestionLoader {

	public void initQuestions() {
		gender()
		age()
		internetExperience()
		language()
		successful()
		bestAndWorstThing()
		jargon()
		enoughInformation()
		read()
		lost()
		frustrated()
	}

	private void frustrated() {
		Question q = new Question( "Were you at any stage frustrated completing the survey?" )
		Option yes = q.addToOptions( "Yes" )
		q.addToOptions( "No" )
		q.save( flush : true, failOnError : true )

		new Question( "Describe what caused the frustration." ).save( flush : true, failOnError : true )
	}

	private void lost() {
		Question lost = new Question( "Did you get lost at any stage during the survey?" )
		Option yes = lost.addToOptions( "Yes" )
		lost.addToOptions( "No" )
		lost.save( flush : true, failOnError : true )

		new Question( "Please describe how you got lost.", yes ).save( flush : true, failOnError : true )
	}

	private void read() {
		Question read = new Question( "How much information on the site did you actually read?" )
		read.addToOptions( [ "All of it", "Most of it", "About half", "Skim read only", "None" ] )
		read.save( flush : true, failOnError : true )
	}

	private void gender() {
		def gender = new Question( "Gender" )
		gender.addToOptions( [ "Female", "Male" ] )
		gender.save( flush : true, failOnError : true )
	}

	private void age() {
		Question age = new Question( "Age" )
		age.addToOptions( [ "18-30", "31-40", "41-50", "51-60", "60+" ] )
		age.save( flush : true, failOnError : true )
	}

	private void internetExperience() {
		Question experience = new Question( "How experienced are you in using the internet?" )
		experience.addToOptions( [ "Very experienced", "Some experience", "Limited experience", "No experience" ] )
		experience.save( flush : true, failOnError : true )
	}

	private void language() {
		Question language = new Question( label : "Is English your primary language?" )
		language.addToOptions( "Yes" )
		Option notEnglish = language.addToOptions( "No" )
		language.save( flush : true, failOnError : true )

		Question otherLanguage = new Question( "Which language is your primary language?", notEnglish )
		otherLanguage.save( flush : true, failOnError : true )
	}

	private void successful() {
		Question successful = new Question( label : "Were you able to successfully complete the task set for this website?" )
		successful.addToOptions( "Yes" )
		Option notSuccessful = language.addToOptions( "No" )
		successful.save( flush : true, failOnError : true )

		Question whyNotSuccessful = new Question( "Can you please describe why not?", notSuccessful )
		whyNotSuccessful.save( flush : true, failOnError : true )
	}

	private void bestAndWorstThing() {
		new Question( "What was the best thing about the survey?" ).save( flush : true, failOnError : true )
		new Question( "What was the worst thing about the survey?" ).save( flush : true, failOnError : true )
	}

	private void jargon() {
		Question jargon = new Question( label : "Were there any words or jargon used that you did not understand?" )
		Option yesJargon = jargon.addToOptions( "Yes" )
		jargon.addToOptions( "No" )
		jargon.save( flush : true, failOnError : true )

		new Question( "Describe any problems you experienced with jargon", yesJargon ).save( flush : true, failOnError : true )
	}

	private void enoughInformation() {
		Question provideAllInformation = new Question( "Did the survey provide all the information you required to complete the set task?" )
		provideAllInformation.addToOptions( [ "Yes", "No" ] )
		provideAllInformation.save( flush : true, failOnError : true )

		Question wantedMoreInformation = new Question( "Was there anything else you wanted to know but could not find out from the site?" )
		Option yesWantMoreInfo = wantedMoreInformation.addToOptions( "Yes" )
		wantedMoreInformation.addToOptions( "No" )
		wantedMoreInformation.save( flush : true, failOnError : true )

		new Question( "What else did you want to know?", yesWantMoreInfo ).save( flush : true, failOnError : true )
	}

}
