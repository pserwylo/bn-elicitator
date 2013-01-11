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

class ParentClassTagLib {

	static namespace = "bn"

	/**
	 * @attr variables REQUIRED
	 * @attr matrix REQUIRED
	 * @attr x REQUIRED
	 * @attr y REQUIRED
	 */
	def hasParentClass = { attrs, body ->

		def variables = attrs.variables
		def matrix = attrs.matrix
		Integer x = attrs.x
		Integer y = attrs.y

		String parent = "";
		if ( x > 0 )
		{
			parent = "has-parent";
			for ( int i = 0; i < x; i ++ )
			{
				parent += "-" + variables[ i ].label + "-" + matrix[ y ][ i ]
			}
		}

		out << parent

	}

}
