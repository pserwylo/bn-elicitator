package bn.elicitator.algorithm

import bn.elicitator.Probability
import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.auth.User
import bn.elicitator.das2004.CompatibleParentConfiguration
import bn.elicitator.das2004.CompletedDasVariable
import bn.elicitator.das2004.ProbabilityEstimation
import org.apache.commons.collections.CollectionUtils

/**
 * Implements the Weighted Sum algorithm (Das, 2004) to fill in a full CPT from sparse elicitation's of "Compatible
 * Parent Configurations":
 * 
 *  Pr( A = a | X = x, Y = y, Z = z ) ~
 *      Alpha . (  
 *          Weight( X ) . Pr( A = a | CPC( X ) ) +
 *          Weight( Y ) . Pr( A = a | CPC( Y ) ) +
 *          Weight( Z ) . Pr( A = a | CPC( Z ) )
 *      )
 *
 *  Where Alpha is a normalizing constant.
 */
class DasWeightedSum {

    private final User user
    private final Variable child
    private final List<Variable> parents
    private final Map<Variable,Map<Variable,BigDecimal>> matrix = [ : ]

    private static final Cache CACHE = new Cache()
    
    public DasWeightedSum( Variable child, Collection<Variable> parents, User user ) {

        this.child = child
        this.user = user
        this.parents = parents
        
    }

    List<Probability> getCalculatedProbabilities( Map<State, Double> stateProbabilities, List<State> parentStates, User user ) {

        stateProbabilities.collect {

            State childState   = it.key
            Double probability = it.value

            List<Probability> probs         = Probability.findAllByCreatedByAndChildState( user, childState )
            Probability existingProbability = probs.find { CollectionUtils.isEqualCollection( probs.parentStates*.id, parentStates*.id ) }
            if ( !existingProbability ) {
                existingProbability = new Probability( createdBy : user, childState : childState, parentStates : parentStates )
            }

            existingProbability.probability = probability

            return existingProbability

        }

    }

    private List<CompatibleParentConfiguration> findCompatibleConfigs( List<State> parentConfiguration ) {
        def found = CACHE.findCompatibleConfigs( user ).findAll { CompatibleParentConfiguration elicitedConfig ->
            parentConfiguration*.id.contains( elicitedConfig.parentStateId )
        }
        
        println "Found ${found.size()} matching CPCs:\n  - ${found.join( '\n  - ' )}"

        if ( found.size() != parentConfiguration.size() ) {
            throw new AlgorithmException( """
Expected ${parentConfiguration.size()} CPCs, but got ${found.size()}.
Expected one for each state: ${parentConfiguration*.label}
""" )
        }
        
        return found
    }

    private List<ProbabilityEstimation> findEstimations( List<CompatibleParentConfiguration> compatibleConfigs ) {
        def compatibleConfigIds = compatibleConfigs*.id
        def found = CACHE.findEstimations( user ).findAll {
            // Things with no parents will not have a parent configuration, so make sure to exclude them from this search.
            it.parentConfiguration && compatibleConfigIds.contains( it.parentConfigurationId ) && it.childState.variableId == child.id
        }

        print "Estimations:\n  ${found.join( "\n  " ) }"

        int expectedEstimations = parents.size() ? ( compatibleConfigs.size() * child.states.size() ) : child.states.size()
        if ( found.size() != expectedEstimations ) {
            throw new AlgorithmException( "Expected $expectedEstimations estimations for the $child.label variable, but received ${found.size()}" )
        }

        return found
    }

    private List<List<State>> getAllPossibleParentStates() { parents*.states.combinations() }

    public List<Probability> run() {

        boolean completed = CompletedDasVariable.countByCompletedByAndVariable( user, child ) > 0
        if ( !completed ) {
            print "User $user.id didn't complete the variable '$child', so skipping."
            return []
        }

        Map<Variable, BigDecimal> weights         = new AHP( child, parents, user ).run()
        List<Probability> probabilities           = []
        List<List<State>> allPossibleParentStates = getAllPossibleParentStates()
        
        print "Processing ${allPossibleParentStates.size()} possible combinations of parents:\n  - ${allPossibleParentStates.join( '\n  - ' )}"
        
        allPossibleParentStates.each { List<State> parentConfiguration ->

            print "Checking parent configurations for ${parentConfiguration}..."

            Map<State, Double> childStateProbabilities = child.states.collectEntries { new MapEntry( it, calcProb( parentConfiguration, it, weights ) ) }

            double sum = (double)childStateProbabilities.values().sum()
            if ( sum <= 0 ) {
                def msg = "Probabilities summed to $sum. Should be > 0."
                print "OOPS: $msg"
                // throw new AlgorithmException( msg )
            } else {

                double scale = 1 / sum
                childStateProbabilities.each { it.value *= scale }

                probabilities.addAll getCalculatedProbabilities( childStateProbabilities, parentConfiguration, user )
            }
        }

        return probabilities
    }

    private double calcProb( List<State> parentConfiguration, State childState, Map<Variable, BigDecimal> weights ) {
        
        def compatibleConfigs = findCompatibleConfigs( parentConfiguration )
        def estimations       = findEstimations( compatibleConfigs )
        double probChildState = 0
        
        compatibleConfigs.each { CompatibleParentConfiguration config ->

            def probOfConfig = estimations.find {
                ( !it.parentConfiguration || it.parentConfigurationId == config.id ) && // Deal with variables without parents...
                        it.childState.id == childState.id
            }

            if (probOfConfig == null) {
                throw new AlgorithmException("""
Couldn't find probability estimation for parent configuration.
  CPC [id: $config.id, createdBy: $config.createdById] $config
""")
            } else {

                if (!weights.containsKey(config.parentState.variable)) {
                    throw new AlgorithmException("Mismatch between compatible parent configuration $config.id and the pairwise comparisons. Could not find variable $config.parentState.variable in comparisons.")
                }

                def parentWeight = weights[config.parentState.variable]
                probChildState += parentWeight * probOfConfig.probability

            }

        }
        probChildState
    }

    /**
     * Static cache which loads all of the CompatibleParentConfiguration and ProbabilityEstimation's from the database,
     * so that they can be iterated over and the ones for each user selected many times over during the running of the
     * algorithm.
     */
    private static class Cache {

        private List<CompatibleParentConfiguration> allCompatibleConfigs = null
        private List<ProbabilityEstimation>         allEstimations       = null

        private List<CompatibleParentConfiguration> getAllCompatibleConfigs() {
            if ( this.allCompatibleConfigs == null ) {
                this.allCompatibleConfigs = CompatibleParentConfiguration.list()
                println "Cached ${this.allCompatibleConfigs.size()} CPCs from DB..."
            }
            return this.allCompatibleConfigs
        }

        private List<ProbabilityEstimation> getAllEstimations() {
            if ( this.allEstimations == null ) {
                List<CompletedDasVariable> completed = CompletedDasVariable.list()
                this.allEstimations = ProbabilityEstimation.list().findAll { ProbabilityEstimation estimation ->
                    // Only include estimations which belong to completed variables.
                    completed.find { CompletedDasVariable var ->
                        var.completedById == estimation.createdById && var.variableId == estimation.childState.variableId
                    }
                }
                println "Caching ${this.allEstimations.size()} probability estimations from DB..."
            }
            return this.allEstimations
        }

        private List<CompatibleParentConfiguration> findCompatibleConfigs( User createdBy ) {
            return getAllCompatibleConfigs().findAll { it.createdBy.id == createdBy.id }
        }

        private List<ProbabilityEstimation> findEstimations( User createdBy ) {
            return getAllEstimations().findAll { it.createdBy.id == createdBy.id }
        }

    }

}
