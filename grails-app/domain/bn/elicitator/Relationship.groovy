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

import bn.elicitator.auth.User

/**
 * Specifies the nature of the causal relationship from {@link Relationship#parent} to {@link Relationship#child).
 * If the relationship {@link Relationship#exists}, then there is a causal relationship.
 * You can have a relationship which doesn't {@link Relationship#exists}, but still has comments explaining why they
 * think there is *not* any relationship.
 */
class Relationship {

    static constraints = {
		comment nullable: true
    }

	static mapping = {
		exists column: 'doesRelationshipExist' // Exists is a reserved word in MySQL.
	}

	Variable parent

	Variable child

	/**
	 * Used so that in the first round, we are able to know whether to put a relationship in the "Yes", "No" or
	 * "Pending" list.
	 */
	Boolean isExistsInitialized = false

	/**
	 * If this is false, it is the same as there being no relationship object between {@link Relationship#parent} and
	 * {@link Relationship#child}.
	 * The value defaults to true.
	 */
	Boolean exists = true;

	/**
	 * This will always reference the person who thinks that there is/isn't a relationship.
	 * It will not be created by, e.g. an admin user on behalf of the user.
	 */
	User createdBy

	/**
	 * Allow participants the opportunity to comment on why they chose that there is/isn't a relationship.
	 * Other participants comments will be displayed (anonymously) to the logged in user when viewing a relationship,
	 * so that they can weigh up each persons opinions.
	 */
	Comment comment = null

	/**
	 * Because the survey will be run multiple times, we need to keep track of which phase we are currently in.
	 */
	Integer delphiPhase

	String toString()
	{
		String string = exists ? "Relationship" : "No relationship";
		string += " from " + parent + " to " + child;
		return string

	}

	Boolean isCurrent()
	{
		return delphiPhase == AppProperties.properties.delphiPhase
	}

	Comment getMostRecentComment( Integer phaseToSearchFrom = AppProperties.properties.delphiPhase )
	{
		Comment c     = this.comment
		Integer phase = phaseToSearchFrom

		while ( !c?.comment?.length() && phase > 0 )
		{
			phase --
			Relationship relationship = Relationship.findByChildAndParentAndCreatedByAndDelphiPhase( child, parent, createdBy, phase )
			c = relationship?.comment
		}

		return c
	}

}
