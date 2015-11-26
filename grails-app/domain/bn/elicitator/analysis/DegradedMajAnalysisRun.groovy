package bn.elicitator.analysis

class DegradedMajAnalysisRun extends MajAnalysisRun {

    int numExpertsRemoved

    public String toString() { "Majority Vote - Degraded (threshold $threshold, removed $numExpertsRemoved experts)" }

    public String getTypeName() { "MajDegraded" }
    
}
