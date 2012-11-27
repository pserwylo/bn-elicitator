package bn.elicitator

class AdminService {

	def userService

	/**
	 * Increments the delphiPhase and saves the properties.
	 */
	def advanceDelphiPhase() {

		AppProperties.properties.delphiPhase ++
		AppProperties.properties.save()
	}

}
