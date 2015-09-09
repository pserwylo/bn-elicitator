package bn.elicitator.analysis

class DegradedDSAnalysisRun extends DSAnalysisRun {

    int numExpertsRemoved

    public String toString() { "Dawid & Skene - Degraded (prior $prior, removed $numExpertsRemoved experts)" }

    public String getTypeName() { "DSDegraded" }
    
}
