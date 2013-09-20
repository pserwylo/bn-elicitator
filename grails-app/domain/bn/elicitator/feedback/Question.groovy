/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2013 Peter Serwylo (peter.serwylo@monash.edu)
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

package bn.elicitator.feedback

class Question {

	static constraints = {
		dependsOn nullable: true
	}

	static hasMany = [ options : Option ]

	public Question() {}

	public Question( String label ) {
		this.label = label
	}

	public Question( String label, Option dependsOn ) {
		this.label     = label
		this.dependsOn = dependsOn
	}

	public Option addToOptions( String label ) {
		Option option = new Option( label : label )
		addToOptions( option )
		return option
	}

	public List<Option> addToOptions( List<String> labels ) {
		List<Option> options = new ArrayList<Option>( labels.size() )
		labels.each { String label ->
			Option option = new Option( label : label )
			addToOptions( option )
			options.add( option )
		}
		return options
	}

	String label

	List<Option> options = []

	Option dependsOn = null

	public String toString() { label }

}