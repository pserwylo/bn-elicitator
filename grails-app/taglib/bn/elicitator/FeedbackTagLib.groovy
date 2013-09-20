package bn.elicitator

import bn.elicitator.feedback.Answer
import bn.elicitator.feedback.Option
import bn.elicitator.feedback.Question

class FeedbackTagLib {

	static namespace = "bnFeedback"

	UserService userService

	/**
	 * @attr questions REQUIRED
	 */
	def survey = { attrs ->

		if ( !attrs.containsKey( 'questions' ) || !attrs.questions ) {
			throwTagError( "Tag [question] missing required [questions] attribute." )
		}

		List<Question> questions = attrs.remove( 'questions' )

		questions.each { Question question ->
			out << bnFeedback.question( [ question : question ] )
		}

	}

	/**
	 * @attr question REQUIRED
	 */
	def question = { attrs ->

		if ( !attrs.containsKey( 'question' ) || !attrs.question ) {
			throwTagError( "Tag [answer] missing required [question] attribute." )
		}

		Question question = attrs.remove( 'question' )
		String style      = ""
		if ( question.dependsOn ) {
			Answer dependsOnAnswer = Answer.findByAnsweredByAndQuestionAndAnswerOption( userService.current, question, question.dependsOn )
			if ( !dependsOnAnswer ) {
				style = "display: none;"
			}
		}

		out << """
			<li style='$style' id='question-$question.id' class='question'>
				<span class='label'>${question.label}</span>
				${answer( [ question : question ] )}
			</li>
		"""
	}

	def answer = { attrs ->

		if ( !attrs.containsKey( 'question' ) || !attrs.question ) {
			throwTagError( "Tag [answer] missing required [question] attribute." )
		}

		Question question = attrs.remove( 'question' )
		Answer answer     = Answer.findByAnsweredByAndQuestion( userService.current, question )

		String name = "question-$question.id"
		if ( question.options?.size() > 0 ) {

			out << "<div class='response options'>"
			question.options.each { Option option ->

				String id = "option-$option.id"
				String checked =  ( answer?.answerOption?.id == option.id ) ? "checked='checked'" : ""

				out << """
						<input type='radio' $checked name='$name' value='$option.id' id='$id' />
						<label for='$id'>
							$option
						</label>
				"""
			}
			out << "</div>"

		} else {

			String text =  answer?.answerText ?: ""
			out << """
				<div class='response text'>
					<textarea name='$name'>$text</textarea>
				</div>
			"""

		}

	}

}
