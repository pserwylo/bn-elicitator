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

class State {

	static belongsTo = [ variable : Variable ]

	String label         = ""
	String readableLabel = ""
	String description   = ""

	public State( String label, String description = "" ) {
		this.label         = label
		this.readableLabel = label
		this.description   = description
	}

	void setLabel( String value ) {
		if ( readableLabel == "" || readableLabel == null ) {
			readableLabel = value
		}
		this.label = value
	}

	String toString() {
		readableLabel
	}

	String getDescription() {
		if ( this.description?.size() > 0 ) {
			return this.description
		} else {
			return "$variable = $label"
		}
	}

}