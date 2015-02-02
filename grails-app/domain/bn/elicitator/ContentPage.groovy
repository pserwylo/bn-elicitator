package bn.elicitator

class ContentPage {

	public static final String PRIVACY_POLICY   = "privacy-policy"
	public static final String HELP             = "help"
	public static final String HOME             = "home"
	public static final String EMPTY_LAST_ROUND = "empty-last-round"
	public static final String PRIZE            = "prize"
	public static final String CANT_REGISTER_THIS_ROUND = "cant-register"

	static mapping = {
		content type: "text", blank: true, nullable: true
	}

	String content = ""
	String label
	String alias
	boolean canDelete = true
    
    public String getContent() {
        content ?: ""
    }

}
