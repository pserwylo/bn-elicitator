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

import norsys.netica.*

class BnController {

	BnService bnService
	DelphiService delphiService

    def index() 
	{
		
	}

	def redundant = {

		def varExpectedInsurancePayout = Variable.findByLabel( "PropCost" )
		def varCarCost = Variable.findByLabel( "ThisCarCost" )
		def varCarYear = Variable.findByLabel( "VehicleYear" )
		def varDriverSkill = Variable.findByLabel( "DrivingSkill" )
		def varIsAccident = Variable.findByLabel( "Accident" )

		def relations = [
				[ varCarCost, varExpectedInsurancePayout ],
				[ varCarYear, varExpectedInsurancePayout ],
				[ varDriverSkill, varExpectedInsurancePayout ],
				[ varIsAccident, varExpectedInsurancePayout ],

				[ varCarYear, varCarCost ],
				[ varDriverSkill, varIsAccident ]
		]

		List<Relationship> tempRelationships = []

		for ( def pair in relations )
		{
			tempRelationships.add(
				new Relationship(
					parent: pair[ 0 ],
					child: pair[ 1 ],
					createdBy: ShiroUser.current,
					delphiPhase: delphiService.phase
				).save( flush: true )
			)
		}

		List<BnService.RedundantRelationship> redundantRelationships = bnService.getRedundantRelationships()
		render redundantRelationships

		tempRelationships.each { it.delete( flush: true ) }

	}
	
	def download = {
		
		Net net = this.bnService.createBn()
		
		response.setContentType( "application/octet-stream" )
		response.setHeader( "Content-disposition", "attachment;filename=BayesianNetwork.dne" )
		
		render this.bnService.serialize( net )
		
	}
}
