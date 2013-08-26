package bn.elicitator.algorithms

import bn.elicitator.BnService
import bn.elicitator.State
import bn.elicitator.UserService
import bn.elicitator.Variable
import bn.elicitator.das2004.CompatibleParentConfiguration
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
		def valid = [ 0, parentOne.id, parentTwo.id ]
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
}
