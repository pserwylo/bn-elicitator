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

class UtilTagLib {

	static namespace = "bn"

	def backToTop = { attrs, body ->
		out << "<span class='back-to-top'><a href='#top'>(back to top)</a></span>"
	}

	def top = { attrs, body ->
		out << "<a name='top'></a>	"
	}

	/**
	 * @attr date REQUIRED
	 */
	def date = { attrs ->
		Date date = attrs.date
		out << "<span class='date'>${date.format( 'dd/MM/yyyy hh:mm' )}</span>"
	}

	/**
	 * @attr atTop REQUIRED Specifies whether to style the buttons as if they are at the top of the form or the bottom.
	 * @attr includeDelete
	 * @attr closeLabel
	 */
	def saveButtons = { attrs ->
		Boolean atTop         = attrs.atTop
		Boolean includeDelete = false
		String  closeLabel    = "Close"

		if ( attrs.containsKey( 'includeDelete' ) ) {
			includeDelete = attrs.remove( 'includeDelete' )
		}

		if ( attrs.containsKey( 'closeLabel' ) ) {
			closeLabel = attrs.remove( 'closeLabel' )
		}

		out << """
			<span class='save-wrapper ${atTop ? "top" : "bottom"}'>
				<button class='close' type='button'>$closeLabel</button>
				"""

		if ( includeDelete ) {
			out << "<button class='delete' type='button'>Delete</button>"
		}

		out << """
				<button class='save' type='submit'>Save</button>
			</span>
			"""
	}

}
