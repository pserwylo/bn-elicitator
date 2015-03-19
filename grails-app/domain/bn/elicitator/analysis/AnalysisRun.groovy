package bn.elicitator.analysis

abstract class AnalysisRun {
    
    CandidateNetwork startingNetwork
    
    CandidateNetwork collatedNetwork
    
    CandidateNetwork acyclicNetwork

    Double threshold
    
    abstract public String toShortString();

}
