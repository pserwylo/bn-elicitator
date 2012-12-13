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
 * Used to allow participants to specify why they chose what they chose.
 * Includes flags which let administrators moderate discussion.
 */
class Comment {

	static constraints = {
		hiddenBy nullable: true
	}

	static mapping = {
		comment type: "text"
	}

	String comment

	ShiroUser createdBy

	Date createdDate

	ShiroUser lastModifiedBy

	Date lastModifiedDate

	/**
	 * Allows moderators to hide comments, either because they are innappropriate, or because they are repeating an
	 * argument that others made.
	 */
	boolean isHidden = false

	String reasonForHiding = ""

	ShiroUser hiddenBy = null


}
