/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2014 Peter Serwylo (peter.serwylo@monash.edu)
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

import bn.elicitator.network.BnArc
import bn.elicitator.network.BnNode

class AnomaliesTagLib {

	static namespace = "bnAnomalies"

	BnService bnService

	def list = {

		List<BnNode> nodes = BnNode.list()

		BnNode skill        = nodes.find { it.variable.readableLabel == "Driving skill" }
		BnNode age          = nodes.find { it.variable.readableLabel == "Age of the client" }
		BnNode vehicleAge   = nodes.find { it.variable.readableLabel == "Vehicle age" }
		BnNode otherCar     = nodes.find { it.variable.readableLabel == "Other cars involvement" }
		BnNode history      = nodes.find { it.variable.readableLabel == "Driver history" }
		BnNode quality      = nodes.find { it.variable.readableLabel == "Quality of client's driving" }
		BnNode training     = nodes.find { it.variable.readableLabel == "Advanced driver training" }
		BnNode riskAversion = nodes.find { it.variable.readableLabel == "Risk aversion" }
		BnNode carType      = nodes.find { it.variable.readableLabel == "Car type" }
		BnNode carStolen    = nodes.find { it.variable.readableLabel == "Car stolen" }
		BnNode antiTheft    = nodes.find { it.variable.readableLabel == "Anti theft device installed" }
		BnNode airbags      = nodes.find { it.variable.readableLabel == "Airbags" }
		BnNode accident     = nodes.find { it.variable.readableLabel == "Accident" }
		BnNode parking      = nodes.find { it.variable.readableLabel == "Primary parking location" }
		BnNode fix          = nodes.find { it.variable.readableLabel == "Cost to insurer to fix client's car" }

		List<BnArc> arcs = [
			new BnArc( parent : history, child : skill ),

			/*
			new BnArc( parent : otherCar, child : skill ),
			new BnArc( parent : skill, child : age ),
			new BnArc( parent : history, child : quality ),
			new BnArc( parent : training, child : riskAversion ),
			new BnArc( parent : airbags, child : carType ),
			new BnArc( parent : fix, child : carType ),
			new BnArc( parent : fix, child : age ),
			new BnArc( parent : history, child : skill ),
			new BnArc( parent : history, child : training ),
			new BnArc( parent : skill, child : quality ),
			new BnArc( parent : accident, child : training ),
			new BnArc( parent : airbags, child : vehicleAge ),
			new BnArc( parent : carStolen, child : antiTheft ),
			new BnArc( parent : carStolen, child : parking ),
			*/
		]

		List<BnService.CyclicalRelationship> relationships = bnService.getCyclicalRelationships()

		def arcCount = [:]
		relationships.each {
			it.arcs.each { BnArc arc ->
				if (!arcCount.containsKey(arc)) {
					arcCount.put(arc, 1)
				} else {
					arcCount.put(arc, arcCount.get( arc ) + 1);
				}
			}
		}

		arcCount.sort { it1, it2 ->
			it1.value <=> it2.value
		}

		out << "<h2>Most frequent arcs in cycles</h2>"
		arcCount.each {
			out << "<p>$it.key.parent -> $it.key.child: $it.value times</p>"
		}

		relationships.sort { BnService.CyclicalRelationship it1, BnService.CyclicalRelationship it2 ->
			it1.chain.size() <=> it2.chain.size()
		}

		out << "<h2>${relationships.size()} cycles identified...</h2>"

		relationships.each {
			out << "<p>$it</p>"
		}

	}

}
