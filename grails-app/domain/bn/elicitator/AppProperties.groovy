package bn.elicitator

class AppProperties {

	static constraints = {
		explanatoryStatement( maxSize: 10000 )
		adminEmail( nullable: true )
	}

	static mapping = {
		explanatoryStatement( type: "text" )
	}

	private static AppProperties instance = null;

	static final ELICIT_1_VARIABLES = 1;
	static final ELICIT_2_RELATIONSHIPS = 2;
	static final ELICIT_3_PROBABILITIES = 3;

	String adminEmail = null

	Integer delphiPhase = 1

	Integer elicitationPhase = ELICIT_1_VARIABLES

	String title = "Online Knowledge Elicitation"

	String explanatoryStatement = ""

	String url = ""

	/**
	 * Lazily load, and keep a reference to the {@link AppProperties} for this web app.
	 * @return
	 */
	public static AppProperties getProperties()
	{
		if ( AppProperties.instance == null )
		{
			AppProperties properties = AppProperties.findById( 1 );
			if ( properties == null )
			{
				properties = new AppProperties();
				properties.id = 1;
				properties.save();
			}
			AppProperties.instance = properties;
		}
		return AppProperties.instance;
	}
}
