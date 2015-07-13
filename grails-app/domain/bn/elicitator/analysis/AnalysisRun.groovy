package bn.elicitator.analysis

abstract class AnalysisRun {

    CandidateNetwork startingNetwork
    
    CandidateNetwork collatedNetwork
    
    CandidateNetwork acyclicNetwork

    Double threshold

    /** Estimated accuracy */
    Map expertWeights
    
    /** Actual accuracy - compared to the gold standard network */
    Map expertAccuracy

    public String toShortString() { "${typeName}_$threshold" }

    abstract public String getTypeName()

}
