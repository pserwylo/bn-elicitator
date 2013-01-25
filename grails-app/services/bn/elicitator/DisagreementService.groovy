package bn.elicitator

class DisagreementService {

	def delphiService
	def variableService

	def recalculateDisagreement( Variable child ) {

		Disagreement disagreement = null
		if ( delphiService.hasPreviousPhase )
		{
			disagreement = getDisagreement( child )
			if ( disagreement == null )
			{
				disagreement = new Disagreement(
					createdBy  : ShiroUser.current,
					child      : child,
					delphiPhase: delphiService.phase,
				)
			}

			List<Variable> potentialParents = variableService.getPotentialParents( child )
			List<Agreement> agreements = []
			for ( Variable parent in potentialParents )
			{
				Agreement agreement = this.delphiService.calcAgreement( parent, child )
				agreements.add( agreement )
			}

			disagreement.disagreeCount = agreements.count{ it -> !it.agree }
			disagreement.totalCount    = potentialParents.size()
			disagreement.save( flush: true )

			if ( disagreement.disagreeCount == 0 ) {

				// No longer requires review...
				variableService.visitVariable( child )

			}

		}
		return disagreement
	}

	def getDisagreement( Variable child ) {
		return Disagreement.findByDelphiPhaseAndCreatedByAndChild( delphiService.phase, ShiroUser.current, child )
	}

	def getDisagreements( List<Variable> children ) {
		return Disagreement.findAllByDelphiPhaseAndCreatedByAndChildInList( delphiService.phase, ShiroUser.current, children )
	}

}
