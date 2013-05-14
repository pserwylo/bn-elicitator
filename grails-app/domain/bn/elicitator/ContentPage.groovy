package bn.elicitator

class ContentPage {

	public static final String PRIVACY_POLICY = "privacy-policy"
	public static final String HELP           = "help"

	static mapping = {
		content type: "text"
	}

	String content
	String label
	String alias

}
