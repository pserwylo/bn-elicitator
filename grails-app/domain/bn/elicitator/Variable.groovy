package bn.elicitator

class Variable implements Comparable<Variable> {
	
	static constraints = {
	}

	static hasMany = [ synonyms: String ]

	static mapping = {
		synonyms( lazy: false )
		description( type: "text" )
	}

	ShiroUser createdBy
	Date createdDate
	ShiroUser lastModifiedBy
	Date lastModifiedDate

	/**
	 * A human readable label for this variable.
	 * e.g. "Abdominal Cramps" instead of "AbdominalCramps".
	 */
	String readableLabel = ""
	
	/**
	 * An identifier for this variable. Doesn't necessarily need to be human readable.
	 * e.g. "AbdominalCramps" vs "Abdominal Cramps".
	 */
	String label = ""
	
	/**
	 * Human readable description of this variable. 
	 * Ideally, it would include citations as to where the information came from. 
	 */
	String description = ""

	/**
	 * One of either:
	 *  {@link VariableClass#getProblem()}
	 *  {@link VariableClass#getSymptom()}
	 *  {@link VariableClass#getBackground()}
	 *  {@link VariableClass#getMediating()}
	 */
	VariableClass variableClass = null

	/**
	 * When presenting a list of potential parents/children, we want to provide a usable description of how to think
	 * about relationships to this variable. While we can provide a meaningful default, it might do well to customise
	 * specific examples for each variable.
	 *
	 * Default:
	 *  "Which of the following variables have an influence on [This]?"
	 *
	 * Example:
	 * 	"Which of the following variables would increase or decrease the chance of your next casualty being somebody
	 * 	 with a Cardiac Arrest?"
	 */
	String usageDescription =
		"Which of the following variables have an influence on [This]?"

	String toString()
	{
		return readableLabel
	}

	String getUsageDescription()
	{
		return usageDescription.replace( "[This]", "<span class='variable'>${readableLabel}</span>" )
	}

	/**
	 * Removes multiple spaces, then replaces spaces with underscores, then removes all non-alphanumeric characters.
	 * @param value
	 */
	void setLabel( String value )
	{
		this.label = value.trim().replace( '  ', ' ' ).replace( ' ', '_' ).replaceAll( '[^A-Za-z0-9]', '' );
	}
	
	int compareTo( Variable value )
	{
		return label.compareTo( value.label )
	}
	
}