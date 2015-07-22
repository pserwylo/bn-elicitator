package bn.elicitator.algorithms

import bn.elicitator.BnService
import bn.elicitator.CptAllocation
import bn.elicitator.Probability
import bn.elicitator.State
import bn.elicitator.UserService
import bn.elicitator.Variable
import bn.elicitator.algorithm.BadCptException
import bn.elicitator.algorithm.DasWeightedSum
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
        try {
            if ( parents.size() > 1 ) {
                print "Calculating conditional probabilities for $child (${parents.size()} parents)"
                print "First - get weights for $parents -> $child, then calculate weighted sum."
                calcConditionalProbability( child, parents, user )*.save()
                print "Finished calculating weighted sum for $parents -> $child"
            } else if ( parents.size() == 1 ) {
                print "Calculating CPT for $child with single parent"
                singleParentConditional( child, user )*.save()
            } else {
                print "Calculating marginal probability for $child (no parents)"
                calcMarginalProbability( child, user )*.save()
            }
        } catch ( BadCptException e ) {
            // TODO: Actually bail when this happens, it really shouldn't happen...
            println "******************************************************************************"
            println e.getMessage()
            println "******************************************************************************"
        }

	}
    
    public List<Probability> calcConditionalProbability( Variable child, Collection<Variable> parents, User user ) {
        new DasWeightedSum( child, parents, user ).run()
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

}
