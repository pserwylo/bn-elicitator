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

class ArrowTagLib {

	static namespace = "bn"

	/**
	 * @attr comment
	 * @attr onclick
	 * @attr forceCommentIcon
	 */
	def rArrow = { attrs ->
		String comment = null
		String onclick = null
		Boolean forceCommentIcon = false

		if ( attrs.containsKey( "onclick" ) ) {
			onclick = attrs.remove( "onclick" )
		}

		if ( attrs.containsKey( "comment" ) && attrs.comment?.trim()?.length() > 0) {
			comment = attrs.remove( 'comment' ).trim()
		}

		if ( attrs.containsKey( "forceCommentIcon" ) ) {
			forceCommentIcon = attrs.remove( "forceCommentIcon" )
		}

		Boolean isDeletable = onclick != null

		if ( onclick != null ) {
			out << "<a href=\"javascript:$onclick\">"
		}

		if ( comment == null ) {
			out << generateArrow( "right", forceCommentIcon, isDeletable )
		} else {
			out << VariableTagLib.generateTooltip( comment, null, null, generateArrow( "right", true, isDeletable ), !isDeletable )
		}

		if ( onclick != null ) {
			out << "</a>"
		}
	}

	static String generateArrow( String direction, boolean hasComment = false, boolean isDeletable = false ) {
		assert [ "left", "right", "up", "down" ].contains( direction )
		String commentClass = hasComment ? "comment" : ""
		String deletableClass = isDeletable ? "deletable" : ""
		return "<span class='arrow ${direction} ${commentClass} ${deletableClass}'></span>"
	}

}
