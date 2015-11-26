package bn.elicitator

import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.analysis.cpt.CptAnalysisSuite
import bn.elicitator.auth.User
import bn.elicitator.das2004.CompatibleParentConfiguration
import bn.elicitator.das2004.ProbabilityEstimation
import grails.transaction.Transactional

@Transactional
class VerifyDasService {

    UserService userService

    List<EstimatedAndElicitedProbabilities> verify(CptAnalysisSuite analysisSuite) {

        final List<EstimatedAndElicitedProbabilities> results      = [];
        final List<CompatibleParentConfiguration> allCpcs          = CompatibleParentConfiguration.list()
        final List<ProbabilityEstimation> allElicitedProbabilities = ProbabilityEstimation.list()
        
        userService.expertList.each { User expert ->
            allCpcs.findAll { it.createdById == expert.id }.each { CompatibleParentConfiguration cpc ->
                List<ProbabilityEstimation> elicitedProbability = loadElicitedProbabilitiesForCpc(cpc, allElicitedProbabilities).sort(true)
                List<Probability> estimatedProbability          = loadEstimatedProbabilitiesForCpc(cpc, analysisSuite).sort(true)

                if (elicitedProbability.size() == estimatedProbability.size()) {
                    results.add(new EstimatedAndElicitedProbabilities(estimatedProbability, elicitedProbability))
                } else {
                    println "Skipping:\n  Estimated: ${estimatedProbability}\n  Elicited:  ${elicitedProbability}"
                }
            }
        }
        
        return results
    }
    
    private doesProbabilityMatchCpc(Probability probability, CompatibleParentConfiguration cpc) {
        Collection<Long> cpcParentStates  = cpc.allParentStates()*.id
        Collection<Long> probParentStates = probability.parentStates*.id

        cpcParentStates.containsAll(probParentStates) && probParentStates.containsAll(cpcParentStates)
        
    }
    
    private List<Probability> loadEstimatedProbabilitiesForCpc(CompatibleParentConfiguration cpc, CptAnalysisSuite analysisSuite) {
        for (Cpt cpt : analysisSuite.unprocessedCpts) {
            if (!cpt.createdBy?.id == cpc.createdBy?.id || !cpt.probabilities?.size()) {
                break
            }

            final List<Probability> matchingDasProbabilities = cpt.probabilities.findAll { doesProbabilityMatchCpc(it, cpc) }.toList()
                
            if (matchingDasProbabilities.size() > 0) {
                return matchingDasProbabilities
            }
        }
        return null
    }
    
    private List<ProbabilityEstimation> loadElicitedProbabilitiesForCpc(CompatibleParentConfiguration cpc, List<ProbabilityEstimation> allEstimations) {
        final List<ProbabilityEstimation> elicited = allEstimations.findAll { it.parentConfigurationId == cpc.id }
        return elicited
        
    }
    
}
