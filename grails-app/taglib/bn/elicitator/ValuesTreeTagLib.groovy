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

class ValuesTreeTagLib {

	static namespace = "bn"

	/**
	 * @attr values REQUIRED
	 */
	def valuesTree = { attrs, body ->

		def values = attrs.values
		out << "<ul>"
		renderBranch( values, out )
		out << "<ul/>"

	}

	private renderBranch( List items, out ) {

		for ( def item in items )
		{
			out << "<li>"
			out << item.label

			if ( item.children?.size() )
			{
				out << "<ul>"

				for ( def child in item.children )
				{
					renderBranch( child, out )
				}

				out << "</ul>"
			}
			out << "</li>"
		}
	}
}
