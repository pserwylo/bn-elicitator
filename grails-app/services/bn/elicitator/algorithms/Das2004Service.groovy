package bn.elicitator.algorithms

import Jama.Matrix
import bn.elicitator.BnService
import bn.elicitator.CptAllocation
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

	public void populateSingleParentConfigurations( Variable parent ) {
		parent.states.each { State parentState ->
			saveCompatibleParentConfiguration( parentState, [] )
		}
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

	public void saveCompatibleParentConfiguration( State parentState, List<State> otherParentStates ) {

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

		List<CptAllocation> allocations = CptAllocation.list()

		allocations.each { CptAllocation allocation ->

			allocation.variables.each { Variable variable ->

				def completed = CompletedDasVariable.countByVariableAndCompletedBy( variable, allocation.user ) > 0
				if ( !completed ) {
					return
				}

				calculateUsersCpt( variable, allocation.user )
			}

		}

	}

	private void calculateUsersCpt( Variable child, User user ) {

		List<Variable> parents = bnService.getArcsByChild( child )*.parent*.variable
		Map<Variable, BigDecimal> weights = calculateWeights( child, parents, user )
		weightedSum( child, parents, user, weights )

	}

	static class AlgorithmException extends Exception {

		public AlgorithmException( String message ) {
			super( "Error while performing Das (2004) calculation: $message" )
		}

	}

	private void weightedSum( Variable child, List<Variable> parents, User user, Map<Variable, BigDecimal> weights ) {

		List<State> parentConfigs = parents*.states.combinations()
		parentConfigs.each { List<State> parentConfiguration ->

			def compatibleConfigs = CompatibleParentConfiguration.findAllByCreatedByAndParentStateInList( user, parentConfiguration )
			def estimations       = ProbabilityEstimation.findAllByCreatedByAndParentConfigurationInList( user, compatibleConfigs )

			Map<State, Double> childStateProbabilities = [:]

			child.states.each { State childState ->

				double probChildState = 0
				compatibleConfigs.each { CompatibleParentConfiguration config ->

					def probOfConfig = estimations.find {
						it.parentConfiguration.id == config.id &&
						it.childState.id == childState.id
					}

					if ( probOfConfig == null ) {
						throw new AlgorithmException( "Couldn't find probability estimation for parent configuration $config.id" )
					}

					if ( !weights.containsKey( config.parentState.variable ) ) {
						throw new AlgorithmException( "Mismatch between compatible parent configuration $config.id and the pairwise comparisons. Could not find variable $config.parentState.variable in comparisons." )
					}

					def parentWeight = weights[ config.parentState.variable ]
					probChildState  += parentWeight * probOfConfig.probability

				}

				childStateProbabilities[ childState ] = probChildState
			}

			double sum = (double)childStateProbabilities.values().sum()
			if ( sum <= 0 ) {
				throw new AlgorithmException( "Probabilities summed to $sum. Should be > 0." )
			}

			double scale = 1 / sum
			childStateProbabilities.each { it.value *= scale }
		}
	}

	private Map<Variable, BigDecimal> calculateWeights( Variable child, List<Variable> parents, User user ) {

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

		public WeightMatrix( List<Variable> variables ) {
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

			double[][] values = new double[ matrix.size() ][ matrix.size() ]

			indexes.eachWithIndex { Variable one, int i ->
				indexes.eachWithIndex { Variable two, int j ->
					values[ i ][ j ] = matrix[ one ][ two ]
				}
			}

			Matrix eigenVectors = new Matrix( values ).eig().v

			Map<Variable, BigDecimal> weights = [:]
			indexes.eachWithIndex { Variable variable, int i ->
				weights[ variable ] = eigenVectors.get( i, 0 )
			}

			// TODO: Normalise vector...
			double sum = weights.values().sum() as Double
			if ( sum == 0 ) {
				throw new Exception( "Error calculating weights, they summed to 0." )
			}

			double scale = 1 / sum;
			weights.each {
				it.value *= scale
			}

			return weights

		}

	}

}
