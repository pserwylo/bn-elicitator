package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.feedback.Answer
import bn.elicitator.feedback.Option
import bn.elicitator.feedback.Question

class FeedbackController {

	UserService userService

	def index() {

		[ questions : Question.list() ]

	}

	def save() {

		User user            = userService.current
		List<Answer> answers = Answer.findAllByAnsweredBy( user )

		Question.list().each { Question question ->

			Option answerOption = null
			String answerText   = null

			String inputName = "question-$question.id"

			if ( params.containsKey( inputName ) ) {

				String value = params[ inputName ]
				if ( question.options?.size() > 0 ) {
					answerOption = question.options.find { it.id == ( value as Integer ) }
				} else {
					answerText = value
				}

			}

			Answer answer = answers.find { it.question.id == question.id }
			if ( !answer ) {
				answer = new Answer( answeredBy : user, question : question )
			}
			answer.answerText   = answerText
			answer.answerOption = answerOption
			answer.save()

		}

		redirect( controller : 'das2004' )

	}

}
