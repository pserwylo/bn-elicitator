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

class InvestigateBnController {

	def index() {

	}

	def arcs() {

		Variable parent = null
		Variable child  = null
		User user       = null
		List<Variable> children = []

		if ( params.containsKey( 'parentId' ) ) {
			parent   = getVariableFromParams( 'parentId' )
			children = Relationship.findAllByParent( parent ).unique { it.child.id }*.child.sort { it1, it2 -> it1.readableLabel <=> it2.readableLabel }
		}

		if (params.containsKey( 'childId' ) ) {
			child = getVariableFromParams( 'childId' )
		}

		if (params.containsKey( 'userId' ) ) {
			user = getUserFromParams( 'userId' )
		}

		List<User> users = User.list()

		return [
		    parent   : parent,
			child    : child,
			user     : user,
			users    : users,
			children : children,
		]

	}

	private Variable getVariableFromParams( String variableIdKey ) {
		Variable variable = null
		if ( params.containsKey( variableIdKey ) ) {
			try {
				variable = Variable.get( params[ variableIdKey ] as Long )
			} catch ( NumberFormatException ignored ) {}
		}
		return variable
	}

	private User getUserFromParams( String userIdKey ) {
		User user = null
		if ( params.containsKey( userIdKey ) ) {
			try {
				user = User.get( params[ userIdKey ] as Long )
			} catch ( NumberFormatException ignored ) {}
		}
		return user
	}

}
