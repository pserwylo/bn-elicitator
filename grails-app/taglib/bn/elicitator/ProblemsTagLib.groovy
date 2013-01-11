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

class ProblemsTagLib {

	static namespace = "bn"

	/**
	 * @attr cyclicalRelationship REQUIRED
	 */
	def removeCycleCheckboxes = { attrs ->
		BnService.CyclicalRelationship rel = attrs.cyclicalRelationship
		out << "<ul class='answers'>"
		rel.relationships.each{ out << bn.removeCycleCheckboxItem( [ relationship: it ]) }
		out << "</ul>"
	}

	/**
	 * @attr relationship REQUIRED
	 */
	def removeCycleCheckboxItem = { attrs ->
		Relationship relationship = attrs.relationship
		out << """
			<li>
				<label>
					<input type="checkbox" name="remove-${relationship.parent.label}-${relationship.child.label}" value="1" />
					${bn.variable( [ var: relationship.parent ] )} ${bn.rArrow( [ comment: relationship.comment?.comment ] )} ${bn.variable( [ var: relationship.child ] )}
				</label>
			</li>
		"""
	}

}
