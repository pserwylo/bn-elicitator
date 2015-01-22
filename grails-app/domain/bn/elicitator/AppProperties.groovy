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

	Integer elicitationPhase = ELICIT_2_RELATIONSHIPS

	String title = "Online Knowledge Elicitation"

	String explanatoryStatement = ""

	String url = ""

	Integer targetParticipantsPerQuestion = 4

	/**
	 * Lazily load, and keep a reference to the {@link AppProperties} for this web app.
	 * @return
	 */
	public static AppProperties getProperties()
	{
		if ( AppProperties.instance == null )
		{
			List<AppProperties> list = AppProperties.list()
			if ( list.size() == 0 ) {
				AppProperties.instance = new AppProperties()
				AppProperties.instance.id = 1
				AppProperties.instance.save()
			} else {
				AppProperties.instance = list.get( 0 )
			}
		}
		return AppProperties.instance;
	}
}
