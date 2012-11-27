package bn.elicitator

/**
 * Essentially we want to know if there is a difference between the current user and other users opinions.
 * The easiest way to do this is to see if:
 *  - We specified a relationship, and they didn't, or...
 *  - They specified a relationship, and we didn't.
 * To figure this out, you can check if myRelationship == null or othersRelationships.size() > 0.
 * Alternatively, the 'by' property will be a bitmask consisting of BY_ME and/or BY_OTHERS (or BY_NEITHER).
 */
class Agreement {

	static constraints = {
		myRelationship( nullable:  true )
	}

	static final int BY_ME = 1
	static final int BY_OTHERS = 2

	static final int BY_NEITHER = 0
	static final int BY_BOTH = BY_ME | BY_OTHERS

	Variable parent
	Variable child

	boolean agree = true

	/**
	 * If this is true, then {@link Agreement#myRelationship} is from this round. Most the time, we just care about the
	 * most recent version of my opinion vs the last version of others opinion, but when rendering info to the screen,
	 * sometimes we want to be able to throw up my last rounds opinion only.
	 */
	boolean current = false

	Relationship myRelationship = null

	List<Relationship> othersRelationships = []

	private List<Relationship> othersRelationshipsWhichExist = []

	int myConfidence = 0

	/**
	 * The average of all other peoples confidence values (if they had any).
	 */
	int othersConfidence = 0

	/**
	 * The number of other people who believes there is a relationship between {@link Agreement#parent}
	 * and {@link Agreement#child}
	 */
	int othersCount = 0

	public int getSpecifiedBy()
	{
		int by = 0;
		if ( myRelationship?.exists )
		{
			by |= BY_ME
		}
		if ( othersRelationshipsWhichExist?.size() > 0 )
		{
			by |= BY_OTHERS
		}
		return by
	}

	public void setOthersRelationships( List<Relationship> value )
	{
		othersRelationships = value
		othersRelationshipsWhichExist = []

		if ( othersRelationships )
		{
			othersRelationshipsWhichExist = othersRelationships.findAll{ it ->
				it.exists
			}
		}
	}

	public List<Relationship> getOthersRelationshipsWhichExist()
	{
		return othersRelationshipsWhichExist
	}

}
