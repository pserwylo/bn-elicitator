package bn.elicitator

/**
 * Helper class which helps clamp a value to a meaningful (human readable) value for the participants.
 */
class ConfidenceValue {

	String label

	Integer realValue

	Integer roundedValue

	private final static ConfidenceValue VALUE_NONE   = new ConfidenceValue( label: "Not confident"     , realValue: 0  , roundedValue: 0   );
	private final static ConfidenceValue VALUE_LOW    = new ConfidenceValue( label: "Somewhat confident", realValue: 25 , roundedValue: 25  );
	private final static ConfidenceValue VALUE_MEDIUM = new ConfidenceValue( label: "50-50"             , realValue: 50 , roundedValue: 50  );
	private final static ConfidenceValue VALUE_HIGH   = new ConfidenceValue( label: "Very confident"    , realValue: 75 , roundedValue: 75  );
	private final static ConfidenceValue VALUE_FULL   = new ConfidenceValue( label: "Certain"           , realValue: 100, roundedValue: 100 );

	public final static List<ConfidenceValue> VALUES = [ VALUE_NONE, VALUE_LOW, VALUE_MEDIUM, VALUE_HIGH, VALUE_FULL ]

	/**
	 * Converts a number (e.g. 20[%]) to a ConfidenceValue with a human readable label (e.g. 'Somewhat confident').
	 * It iterates through the {@link ConfidenceValue#VALUES} to find the closest one.
	 * @param confidencePercent
	 * @return
	 */
	static ConfidenceValue create( int confidencePercent ) {

		double minDistanceSoFar = Double.MAX_VALUE
		ConfidenceValue bestSoFar = null;

		for( ConfidenceValue value : VALUES )
		{

			double distance = Math.abs( (Double)( confidencePercent - value.roundedValue ) )
			if ( distance < minDistanceSoFar )
			{
				minDistanceSoFar = distance
				bestSoFar = value
			}

		}

		return new ConfidenceValue( label: bestSoFar.label, realValue: confidencePercent, roundedValue: bestSoFar.roundedValue )
	}

	String toString() { return label }

}
