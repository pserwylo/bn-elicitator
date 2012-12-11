/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
