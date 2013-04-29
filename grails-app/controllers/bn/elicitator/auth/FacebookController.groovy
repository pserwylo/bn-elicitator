package bn.elicitator.auth

class FacebookController {

	def success() {
		render "Facebook login succeeded"
	}

	def failure() {
		render "Facebook login failed"
	}

}
