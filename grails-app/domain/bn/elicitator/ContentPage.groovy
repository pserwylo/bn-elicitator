package bn.elicitator

class ContentPage {

	public static final String PRIVACY_POLICY = "privacy-policy"

	static mapping = {
		content type: "text"
	}

	String content
	String label
	String alias

}
