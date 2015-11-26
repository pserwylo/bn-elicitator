package bn.elicitator

import bn.elicitator.das2004.ProbabilityEstimation

/**
 * Elicited probabilities are those where a specific question was asked of a user about the probability of a certain
 * situation, that they answered. Estimated probabilities are those which were calculated using the weighted sum
 * algorithm. Note that the terribly named ProbabilityEstimation class doesn't help here.
 */
class EstimatedAndElicitedProbabilities implements Iterable<EstimatedAndElicitedProbabilityPair> {

    public final List<Probability> estimated
    public final List<ProbabilityEstimation> elicited
    
    public EstimatedAndElicitedProbabilities(List<Probability> estimated, List<ProbabilityEstimation> elicited) {
        this.estimated = estimated
        this.elicited = elicited
        assert estimated.size() == elicited.size() && estimated.size() > 0
        
        normalizeEstimated()
        normalizeElicited()
    }

    private normalizeElicited() {
        double sum = elicited*.probability.sum() as Double
        double normalizingFactor = 1 / sum
        elicited.each {
            it.probability *= normalizingFactor
        }
    }

    private normalizeEstimated() {
        double sum = estimated*.probability.sum() as Double
        double normalizingFactor = 1 / sum
        estimated.each {
            it.probability *= normalizingFactor
        }
    }

    @Override
    Iterator<EstimatedAndElicitedProbabilityPair> iterator() {
        return new Iterator<EstimatedAndElicitedProbabilityPair>() {

            private int index = 0;

            @Override
            boolean hasNext() {
                return index < estimated.size()
            }

            @Override
            EstimatedAndElicitedProbabilityPair next() {
                EstimatedAndElicitedProbabilityPair pair = new EstimatedAndElicitedProbabilityPair(estimated[index], elicited[index])
                index ++
                return pair
            }

            @Override
            void remove() {}
        }
    }
}

class EstimatedAndElicitedProbabilityPair {

    public final Probability estimated
    public final ProbabilityEstimation elicited

    public EstimatedAndElicitedProbabilityPair(Probability estimated, ProbabilityEstimation elicited) {
        this.estimated = estimated
        this.elicited = elicited
    }

}