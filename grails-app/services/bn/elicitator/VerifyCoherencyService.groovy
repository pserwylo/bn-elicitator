package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.das2004.CompatibleParentConfiguration as CPC
import bn.elicitator.das2004.ProbabilityEstimation as Prob
import grails.transaction.Transactional

/**
 * Looks at the CPTs elicited by experts and sees how close to normal they are.
 * If they sum to more or less than one, then they need to be normalized. The further
 * from 1 they are, the less coherent they are.
 * 
 * Note that this only occurs for nodes with zero or one parents. The reason is that
 * those with more than one did not have their entire CPT explicitly elicited. As such,
 * it seems unreasonable to worry about normalizing values which were not directly from
 * the expert.
 */
@Transactional
class VerifyCoherencyService {

    UserService userService

    Map<User, List<Double>> verify() {
        final List<Prob> allProbs = Prob.list()
        Map<User, List<Double>> results = userService.expertList.collectEntries { User expert ->
            new MapEntry(expert, verifyExpert(expert, allProbs))
        }
        return results
    }

    List<Double> verifyExpert(User expert, List<Prob> allProbs) {
        List<Prob> probs = allProbs.findAll {
            it.createdById == expert.id /*&& (it.parentConfiguration == null || it.parentConfiguration.allParentStates().size() <= 1)*/
        }

        Map<Variable, List<Prob>> probsByVariable = probs.groupBy { it.childState.variable }
        List<Double> results = []
        probsByVariable.each { Variable variable, List<Prob> probsForVariable ->
            Map<CPC, Double> probsForVar = verifyVariable(variable, probsForVariable)
            results.addAll(probsForVar.values())
        }
        return results
    }

    Map<CPC, Double> verifyVariable(Variable variable, List<Prob> probs) {
        // Lets assume Pr(A|...) with a single parent: Pr(A|B) where B = {b1, b2, b3}
        // This means:
        //
        //  * "variable" will be analogous to "A"
        //
        //  * "probs" will be:
        //      Pr( A = a1 | B = b1 ) = y1
        //      Pr( A = a2 | B = b1 ) = y2
        //      Pr( A = a1 | B = b2 ) = y3
        //      Pr( A = a2 | B = b2 ) = y4
        //      Pr( A = a1 | B = b3 ) = y5
        //      Pr( A = a2 | B = b3 ) = y6
        //
        //  * And in a perfect world:
        //      y1 + y2 = 1 (conditioned on B = b1)
        //      y3 + y4 = 1 (conditioned on B = b2)
        //      y5 + y6 = 1 (conditioned on B = b3)

        // Firstly, group elicitation's based on the unique conditions (e.g. b1, b2, and b3)
        def probsGroupedByParentStates = probs.groupBy { it.parentConfiguration }
        
        // Then for each of these, we should sum all of the available probabilities and hope they are one (though
        // we don't really expect that to exactly work out most the time - the UI doesn't ask for all probabilities
        // at one time and so it is not enforced).
        probsGroupedByParentStates.collectEntries { parentStates, List<Prob> probsForParentStates ->
             new MapEntry(
                 parentStates,
                     verifyParentStates(variable, parentStates, probsForParentStates)
             )
        }.findAll { it.value >= 0 } // Exclude those which verifyParentStates couldn't calculate.
    }

    /**
     * Sums the probability of each state of child, conditioned on the compatible parent configuration.
     * In a perfect world, they would always sum to 1, but people are eliciting probabilities at different times,
     * and so they are not forced to sum to 1 by the user interface.
     */
    double verifyParentStates(Variable child, CPC parentConfig, List<Prob> probs) {
        if (probs.size() != child.states.size()) {
            println "Stuffed up, expected ${child.states.size()} but got ${probs.size()} probabilities for $parentConfig"
            return -1
        } else {
            double total = probs*.probability.sum() as Double
            return total
        }
    }

}
