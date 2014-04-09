package bn.elicitator.output
import bn.elicitator.State

import java.text.DecimalFormat

public class HuginNode extends SerializedBnNode {

	def generateProbs() {

		if ( !cpt ) {
			return ""
		}

		String formatString = "#.######"
		DecimalFormat format = new DecimalFormat( formatString )

		List<String> probs = []

		// If I don't reverse the parents, then I end up with it doing a depth first combination.
		// By doing this, I am forcing it to end up as a breadth first search.
		def parentConfigs = family.parents.reverse()*.states.combinations().collect { it.reverse() }

		if ( parentConfigs.size() > 0 ) {
			parentConfigs.eachWithIndex { List<State> parentConfig, int i ->
				family.variable.states.eachWithIndex { State variableState, int j ->
					double probability = cpt.getProbabilityFor(variableState, parentConfig)
					probs.add( format.format( probability ) )
				}
			}
		} else {
			family.variable.states.eachWithIndex { State variableState, int j ->
				double probability = cpt.getProbabilityFor(variableState, [])
				probs.add( format.format( probability ) )
			}
		}

		probs.join( ' ' )

	}

}
