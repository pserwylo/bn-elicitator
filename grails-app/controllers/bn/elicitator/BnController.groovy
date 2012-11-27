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
