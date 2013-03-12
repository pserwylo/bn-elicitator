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

class Variable implements Comparable<Variable> {
	
	static constraints = {
	}

	static hasMany = [ synonyms: String ]

	static mapping = {
		synonyms( lazy: false )
		description( type: "text" )
	}

	User createdBy
	Date createdDate
	User lastModifiedBy
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
	 *  i18n message: elicit.parents.desc=Which of the following variables have an influence on [This]?
	 *
	 * Example:
	 * 	"Which of the following variables would increase or decrease the chance of your next casualty being somebody
	 * 	 with a Cardiac Arrest?"
	 */
	String usageDescription = ""

	String getDescriptionWithSynonyms()
	{
		String desc = this.description
		if ( synonyms?.size() > 0 ) {
			if ( this.description ) {
				desc += "\n\n"
			}
			desc += "Synonyms: " + synonyms.join( ", " );
		}
		return desc
	}

	/**
	 * Removes multiple spaces, then replaces spaces with underscores, then removes all non-alphanumeric characters.
	 * @param value
	 */
	void setLabel( String value   ) { this.label = value.trim().replace( '  ', ' ' ).replace( ' ', '_' ).replaceAll( '[^A-Za-z0-9]', '' ); }

	int compareTo( Variable value ) { label.compareTo( value.label ) }

	String getUsageDescription()    { usageDescription.replace( "[This]", "<span class='variable'>${readableLabel}</span>" ) }

	String toString()               { readableLabel }

}