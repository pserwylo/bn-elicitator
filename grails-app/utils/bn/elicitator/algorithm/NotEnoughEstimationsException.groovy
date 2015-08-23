package bn.elicitator.algorithm

import bn.elicitator.Variable
import bn.elicitator.das2004.ProbabilityEstimation

class NotEnoughEstimationsException extends BadCptException {
    NotEnoughEstimationsException(int numExpected, Variable child, List<ProbabilityEstimation> estimations) {
        super("Expected $numExpected estimations for the $child.label variable, but received ${estimations.size()}")
    }
}
