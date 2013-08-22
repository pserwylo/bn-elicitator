package bn.elicitator.algorithms

import bn.elicitator.BnService
import bn.elicitator.State
import bn.elicitator.UserService
import bn.elicitator.Variable
import bn.elicitator.das2004.CompatibleParentConfiguration
import bn.elicitator.das2004.ProbabilityEstimation
import org.apache.commons.collections.CollectionUtils

class Das2004Service {

	UserService userService
	BnService   bnService

	public void saveProbabilityEstimation( long childStateId, long parentConfigurationId, double probability )
		throws IllegalArgumentException {

		State childState = State.get( childStateId )
		if ( childState == null ) {
			throw new IllegalArgumentException( "Could not find state $childStateId" )
		}

		CompatibleParentConfiguration parentConfig = CompatibleParentConfiguration.get( parentConfigurationId )
		if ( parentConfig == null ) {
			throw new IllegalArgumentException( "Could not find parent configuration $parentConfigurationId" )
		}

		def estimation = ProbabilityEstimation.findByCreatedByAndChildStateAndParentConfiguration( userService.current, childState, parentConfig )

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

	public CompatibleParentConfiguration getParentConfig( State state ) {
		CompatibleParentConfiguration.findByCreatedByAndParentState( userService.current, state )
	}

	public List<CompatibleParentConfiguration> getParentConfigurationsForChild( Variable variable, List<Variable> parents = null ) {
		if ( parents == null ) {
			parents = bnService.getArcsByChild( variable )*.parent*.variable
		}

		def configs = CompatibleParentConfiguration.findAllByCreatedBy( userService.current )

		configs.findAll {
			CollectionUtils.isEqualCollection( it.allParentStates()*.variable, parents )
		}
	}
}
