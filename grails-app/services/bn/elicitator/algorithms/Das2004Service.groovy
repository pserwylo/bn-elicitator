package bn.elicitator.algorithms

import Jama.EigenvalueDecomposition
import Jama.Matrix
import bn.elicitator.BnService
import bn.elicitator.CptAllocation
import bn.elicitator.Probability
import bn.elicitator.State
import bn.elicitator.UserService
import bn.elicitator.Variable
import bn.elicitator.auth.User
import bn.elicitator.das2004.CompatibleParentConfiguration
import bn.elicitator.das2004.CompletedDasAllocation
import bn.elicitator.das2004.CompletedDasVariable
import bn.elicitator.das2004.PairwiseComparison
import bn.elicitator.das2004.ProbabilityEstimation
import org.apache.commons.collections.CollectionUtils

class Das2004Service {

	UserService userService
	BnService   bnService

	public void saveComparison( long childId, long parentOneId, long parentTwoId, long mostImportantParentId, int weight )
		throws IllegalArgumentException {

		Variable child = Variable.get( childId )
		if ( child == null ) {
			throw new IllegalArgumentException( "Could not find child variable $childId" )
		}

		Variable parentOne = Variable.get( parentOneId )
		if ( parentOne == null ) {
			throw new IllegalArgumentException( "Could not find parent variable $parentOneId" )
		}

		Variable parentTwo = Variable.get( parentTwoId )
		if ( parentTwo == null ) {
			throw new IllegalArgumentException( "Could not find parent variable $parentTwoId" )
		}

		Variable mostImportantParent = null
		if ( mostImportantParentId == 0 || mostImportantParentId == parentOne.id || mostImportantParentId == parentTwo.id ) {
			if ( mostImportantParentId != 0 ) {
				mostImportantParent = mostImportantParentId == parentOne.id ? parentOne : parentTwo
			}
		} else {
			throw new IllegalArgumentException( "Most important variable '$mostImportantParentId' must be one of 0, $parentOne.id or $parentTwo.id")
		}

		def comparison = PairwiseComparison.findByCreatedByAndChildAndParentOneAndParentTwo( userService.current, child, parentOne, parentTwo )

		if ( comparison != null ) {
			if ( comparison.weight != weight || comparison.mostImportantParent != mostImportantParent ) {
				comparison.mostImportantParent = mostImportantParent
				comparison.weight = weight
				comparison.save()
			}
		} else {
			new PairwiseComparison(
				createdBy           : userService.current,
				child               : child,
				parentOne           : parentOne,
				parentTwo           : parentTwo,
				mostImportantParent : mostImportantParent,
				weight              : weight,
			).save()
		}
	}

	public void saveProbabilityEstimation( long childStateId, long parentConfigurationId, double probability )
		throws IllegalArgumentException {

		State childState = State.get( childStateId )
		if ( childState == null ) {
			throw new IllegalArgumentException( "Could not find state $childStateId" )
		}

		CompatibleParentConfiguration parentConfig = null
		if ( parentConfigurationId > 0 ) {
			parentConfig = CompatibleParentConfiguration.get( parentConfigurationId )
			if ( parentConfig == null ) {
				throw new IllegalArgumentException( "Could not find parent configuration $parentConfigurationId" )
			}
		}

		updateProbabilityEstimation( childState, parentConfig, probability )

		// Look for compatible configurations which are the same, but for which we don't ask the user
		// questions, so that we can populate it too. This will make calculations easier later on if
		// we have all of the data available.
		if ( parentConfig != null ) {
			def otherConfigs = CompatibleParentConfiguration.findAllByCreatedByAndIdNotEqual( userService.current, parentConfig.id )
			def equivalentConfigs = otherConfigs.findAll { it.equivalentTo( parentConfig ) }
			equivalentConfigs.each {
				updateProbabilityEstimation( childState, it, probability )
			}
		}
	}

	private void updateProbabilityEstimation( State childState, CompatibleParentConfiguration parentConfig, double probability ) {

		def estimation = getProbabilityEstimation( childState, parentConfig )

		if ( estimation != null ) {
			if ( estimation.probability != probability ) {
				estimation.probability = probability
				estimation.save()
			}
		} else {
			new ProbabilityEstimation(
				createdBy           : userService.current,
				childState          : childState,
				parentConfiguration : parentConfig,
				probability         : probability,
			).save()
		}
	}

	public List<CompatibleParentConfiguration> populateSingleParentConfigurations( Variable parent ) {
		List<CompatibleParentConfiguration> configs = []
		parent.states.each { State parentState ->
			configs.add( saveCompatibleParentConfiguration( parentState, [] ) )
		}
		return configs
	}

    public void saveCompatibleParentConfiguration( long parentStateId, List<Long> otherParentStateIds )
		throws IllegalArgumentException {

		State parentState = State.get( parentStateId )
		if ( parentState == null ) {
			throw new IllegalArgumentException( "Could not find state $parentStateId" )
		}

		List<State> otherParentStates = State.findAllByIdInList( otherParentStateIds )
		if ( !CollectionUtils.isEqualCollection( otherParentStates*.id, otherParentStateIds ) ) {
			throw new IllegalArgumentException( "Tried to find states $otherParentStateIds but found ${otherParentStates*.id}" )
		}

		saveCompatibleParentConfiguration( parentState, otherParentStates )
	}

	public CompatibleParentConfiguration saveCompatibleParentConfiguration( State parentState, List<State> otherParentStates ) {

		def existing = CompatibleParentConfiguration.findByCreatedByAndParentState( userService.current, parentState )

		// Let's see if we have a previous one for this parentState, and if so,
		// update that instead of creating a new state.
		def toSave
		if ( existing ) {
			toSave = existing
			toSave.otherParentStates = otherParentStates
		} else {
			toSave = new CompatibleParentConfiguration(
				parentState : parentState,
				otherParentStates : otherParentStates,
				createdBy : userService.current,
			)
		}
		toSave.save( flush : true, failOnError : true )

		return toSave

    }

	public List<CompatibleParentConfiguration> getParentConfigurationsForChild( Variable variable, List<Variable> parents = null ) {
		if ( parents == null ) {
			parents = bnService.getArcsByChild( variable )*.parent*.variable
		}

		def configs = CompatibleParentConfiguration.findAllByCreatedBy( userService.current )

		configs.findAll {
			CollectionUtils.isEqualCollection( it.allParentStates()*.variable*.id, parents*.id )
		}
	}

	public CompatibleParentConfiguration getParentConfig( State state ) {
		CompatibleParentConfiguration.findByCreatedByAndParentState( userService.current, state )
	}

	public ProbabilityEstimation getProbabilityEstimation( State childState, CompatibleParentConfiguration parentConfig ) {
		if ( parentConfig != null ) {
			ProbabilityEstimation.findByCreatedByAndChildStateAndParentConfiguration( userService.current, childState, parentConfig )
		} else {
			ProbabilityEstimation.findByCreatedByAndChildState( userService.current, childState )
		}
	}

	public PairwiseComparison getPairwiseComparison( Variable child, Variable parentOne, Variable parentTwo ) {
		PairwiseComparison.findByCreatedByAndChildAndParentOneAndParentTwo( userService.current, child, parentOne, parentTwo )
	}

	public void complete( Variable variable ) {

		CompletedDasVariable completed = CompletedDasVariable.findByCompletedByAndVariable( userService.current, variable )

		if ( !completed ) {
			new CompletedDasVariable( completedBy : userService.current, variable : variable ).save( flush : true )
		}

		if ( !hasCompletedAllocation() ) {
			CptAllocation allocation = CptAllocation.findByUser( userService.current )
			int completedCount       = CompletedDasVariable.countByCompletedBy( userService.current )
			if ( completedCount == allocation?.variables?.size() ) {
				new CompletedDasAllocation( completedBy : userService.current ).save( flush : true )
			}
		}

	}

	public boolean hasCompletedAllocation() {
		CompletedDasAllocation.countByCompletedBy( userService.current ) > 0
	}

	public List<Variable> getCompleted() {
		CompletedDasVariable.findAllByCompletedBy( userService.current )*.variable
	}

	/**
	 * After everybody has submitted their answers to each of the questions we required, actually
	 * perform the calculations required.
	 */
	public void performCalculations() {

		timestamp( true );

		List<CptAllocation> allocations = CptAllocation.list()

		allocations.each { CptAllocation allocation ->

			allocation.variables.each { Variable variable ->

				def completed = CompletedDasVariable.countByVariableAndCompletedBy( variable, allocation.user ) > 0
				if ( !completed ) {
					return
				}

				calculateUsersCptForVariable( variable, allocation.user )
			}

		}

	}

	private void calculateUsersCptForVariable( Variable child, User user ) {

		Collection<Variable> parents = bnService.getArcsByChild( child )*.parent*.variable
		if ( parents.size() > 1 ) {
			timestamp()
			print "Calculating conditional probabilities for $child (${parents.size()} parents)"
			print "First - get weights for $parents -> $child, then calculate weighted sum."
            calcConditionalProbability( child, parents, user )*.save()
			print "Finished calculating weighted sum for $parents -> $child"
			timestamp()
		} else if ( parents.size() == 1 ) {
			print "Calculating CPT for $child with single parent"
			timestamp()
			singleParentConditional( child, user )*.save()
			timestamp()
		} else {
			print "Calculating marginal probability for $child (no parents)"
			timestamp()
			calcMarginalProbability( child, user )*.save()
			timestamp()
		}

	}
    
    public List<Probability> calcConditionalProbability( Variable child, Collection<Variable> parents, User user ) {

        Map<Variable, BigDecimal> weights = calculateWeights( child, parents, user )
        weightedSum( child, parents, user, weights )
        
    }

	public List<Probability> calcMarginalProbability( Variable child, User user ) {
		def estimations = ProbabilityEstimation.findAllByCreatedByAndChildStateInList( user, child.states )
		estimations.collect { ProbabilityEstimation estimation ->
			new Probability(
				childState   : estimation.childState,
				probability  : estimation.probability,
				createdBy    : user,
				// Intentionally leave "parentStates" empty.
			)
		}
	}

	// TODO: Not sure if this is the right calculation. It looks very similar to calcMarginalProbability, but includes parentStates.
	public List<Probability> singleParentConditional( Variable child, User user ) {
		def estimations = ProbabilityEstimation.findAllByCreatedByAndChildStateInList( user, child.states )
		estimations.collect { ProbabilityEstimation estimation ->
			new Probability(
				childState   : estimation.childState,
				probability  : estimation.probability,
				createdBy    : user,
				parentStates : estimation.parentConfiguration.allParentStates(),
			)
		}
	}

	static class AlgorithmException extends RuntimeException {

		public AlgorithmException( String message ) {
			super( "Error while performing Das (2004) calculation: $message" )
		}

	}

	private long lastTimestamp = 0

	private void timestamp( boolean clear = false ) {
		long t = System.currentTimeMillis()
		String output = "# " + t
		if ( clear ) {
			lastTimestamp = 0
		} else {
			output += " (" + ( t - lastTimestamp ) + "ms)"
		}
		// print output
		lastTimestamp = t;
	}

	private List<CompatibleParentConfiguration> allCompatibleConfigs = null

	private List<CompatibleParentConfiguration> findCompatibleConfigs( User createdBy, List<State> parentConfiguration ) {
		if ( allCompatibleConfigs == null ) {
			allCompatibleConfigs = CompatibleParentConfiguration.list()
		}

		def parentIds = parentConfiguration*.id

		allCompatibleConfigs.findAll {
			it.createdBy.id == createdBy.id &&
			parentConfigIds.contains( it.parentState.id )
		}
	}

	private List<ProbabilityEstimation> allEstimations = null

    /**
     * For a given user, load all of their estimations which belong to the CPCs specified by compatibleConfigs.
     */
	private List<ProbabilityEstimation> findEstimations( User createdBy, List<CompatibleParentConfiguration> compatibleConfigs ) {
        // Cache allEstimations because findEstimations will be called many times during our calculations,
        // and we don't want to hit the database each time.
		if ( allEstimations == null ) {
            List<CompletedDasVariable> completed = CompletedDasVariable.list()
			allEstimations = ProbabilityEstimation.list().findAll { ProbabilityEstimation estimation ->
                // Only include estimations which belong to completed variables.
                completed.find { CompletedDasVariable var ->
                    var.completedById == estimation.createdById && var.variableId == estimation.childState.variableId
                }
            }
            println "Caching ${allEstimations.size()} estimations from DB..."
		}

		def compatibleConfigIds = compatibleConfigs*.id

		allEstimations.findAll {
			it.createdBy.id == createdBy.id && (
                // Things with no parents will not have a parent configuration.
                !it.parentConfiguration || compatibleConfigIds.contains( it.parentConfigurationId )
            )
		}
	}

	private List<Probability> weightedSum( Variable child, Collection<Variable> parents, User user, Map<Variable, BigDecimal> weights ) {

        List<Probability> probabilities = []
        
		boolean completed = CompletedDasVariable.countByCompletedByAndVariable( user, child ) > 0
		if ( !completed ) {
			print "User $user.id didn't complete the variable '$child', so skipping."
			return []
		}

		List<State> parentConfigs = parents*.states.combinations()
		print "Processing ${parentConfigs.size()} possible combinations of parents..."
		parentConfigs.each { List<State> parentConfiguration ->

			print "Checking parent configurations for ${parentConfiguration}..."
			
            timestamp()
            
			// def compatibleConfigs = CompatibleParentConfiguration.findAllByCreatedByAndParentStateInList( user, parentConfiguration )
			def compatibleConfigs = findCompatibleConfigs( user, parentConfiguration )
            
            if ( compatibleConfigs.size() == parentConfiguration.size() ) {
                println " Found ${compatibleConfigs.size()} matching CPCs."
            } else {
                throw new AlgorithmException( """

Expected ${parentConfiguration.size()} CPCs, but got ${compatibleConfigs.size()}.

Expected:
  - ${parentConfiguration.join("\n  - ")}

Found:
  - ${compatibleConfigs.join("\n  - ")}

""" )
            }
            
			// def estimations = ProbabilityEstimation.findAllByCreatedByAndParentConfigurationInList( user, compatibleConfigs )
			def estimations = findEstimations( user, compatibleConfigs )
            
            print "  Estimations:\n  ${estimations.join( "\n  " ) }"

			timestamp()

			Map<State, Double> childStateProbabilities = [:]

			child.states.each { State childState ->

				double probChildState = 0
				compatibleConfigs.each { CompatibleParentConfiguration config ->

					def probOfConfig = estimations.find {
                        ( !it.parentConfiguration || it.parentConfigurationId == config.id ) && // Deal with variables without parents...
						it.childState.id == childState.id
					}

					if ( probOfConfig == null ) {
						throw new AlgorithmException( """
Couldn't find probability estimation for parent configuration.
  CPC [id: $config.id, createdBy: $config.createdById] $config
""" )
					} else {

						if ( !weights.containsKey( config.parentState.variable ) ) {
							throw new AlgorithmException( "Mismatch between compatible parent configuration $config.id and the pairwise comparisons. Could not find variable $config.parentState.variable in comparisons." )
						}

						def parentWeight = weights[ config.parentState.variable ]
						probChildState  += parentWeight * probOfConfig.probability

					}

				}

				childStateProbabilities[ childState ] = probChildState
			}

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

	private Map<Variable, BigDecimal> calculateWeights( Variable child, Collection<Variable> parents, User user ) {

		WeightMatrix matrix = new WeightMatrix( parents )

		def comparisons = PairwiseComparison.findAllByCreatedByAndChild( user, child )
		comparisons.each { PairwiseComparison comparison ->
			BigDecimal weight = 1
			if ( comparison.mostImportantParent?.id == comparison.parentOne.id ) {
				weight = comparison.weight
			} else if ( comparison.mostImportantParent?.id == comparison.parentTwo.id ) {
				weight = 1 / comparison.weight
			}
			matrix.set( comparison.parentOne, comparison.parentTwo, weight )
		}

		return matrix.calcWeights()

	}

	/**
	 *
	 *       How much
	 *   <-    more    ->
	 *      do these...
	 *
	 *     W   X   Y   Z
	 *   +---+---+---+---+      ^
	 * W | 1 |   |   |   |      |
	 *   +---+---+---+---+
	 * X |   | 1 | 3 |   |  influence
	 *   +---+---+---+---+
	 * Y |   |1/3| 1 |   |    these?
	 *   +---+---+---+---+
	 * Z |   |   |   | 1 |      |
	 *   +---+---+---+---+      v
	 *
	 *   e.g. Y is three times more important than X, while X is 1/3rd the importance of Y.
	 *
	 */
	static class WeightMatrix {

		private Map<Variable,Map<Variable,BigDecimal>> matrix = [:]

		public WeightMatrix( Collection<Variable> variables ) {
			variables.each { Variable variable ->
				matrix[ variable ] = [:]
				variables.each { Variable other ->
					matrix[ variable ][ other ] = variable.id == other.id ? 1 : null
				}
			}
		}

		public void set( Variable one, Variable two, BigDecimal weight ) {
			matrix[ one ][ two ] = weight
			matrix[ two ][ one ] = 1 / weight
		}

		public Map<Variable, BigDecimal> calcWeights() {

			List<Variable> indexes = matrix.keySet().toList()

			double[][] values = new double[ matrix.size() ][]

			try {
				indexes.eachWithIndex { Variable one, int i ->
					values[ i ] = new double[ matrix.size() ]
					// TODO: Don't iterate over this, instead iterate over matrix properly...
					indexes.eachWithIndex { Variable two, int j ->
						int value
						if ( matrix[ one ][ two ] == null ) {
							value = 0;
							throw new AlgorithmException( "Uh Oh, we found a null value here in matrix[ $one.label ][ $two.label ]" )
						} else {
							value = matrix[ one ][ two ]
						}
						values[ i ][ j ] = value
					}
				}
			} catch ( Exception e ) {
				throw new AlgorithmException( "OOPS: $e.message" )
			}

			// print "  Calculating eigen vectors..."

			EigenvalueDecomposition eigen = new Matrix( values ).eig()

			if ( eigen == null ) {
				throw new AlgorithmException( "Uh oh, couldn't calculate eigen vectors for $values" )
			}

			Matrix eigenVectors = eigen.v

            // print "  Getting weights from eigen vectors..."

			Map<Variable, BigDecimal> weights = [:]
			indexes.eachWithIndex { Variable variable, int i ->
				weights[ variable ] = eigenVectors.get( i, 0 )
			}

            // print "  Normalising weights..."

			double sum = weights.values().sum() as Double
			if ( sum == 0 ) {
				indexes.eachWithIndex { Variable variable, int i ->
					weights[ variable ] = 1 / indexes.size()
				}
			} else {
				double scale = 1 / sum;
				weights.each {
					it.value *= scale
				}
			}

			print "  Weights: " + weights

			return weights

		}

	}

}
