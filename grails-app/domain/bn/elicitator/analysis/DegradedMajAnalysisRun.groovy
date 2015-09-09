package bn.elicitator.analysis

class DegradedMajAnalysisRun extends AnalysisRun {

    int numExpertsRemoved

    public String toString() { "Majority Vote - Degraded (threshold $threshold, removed $numExpertsRemoved experts)" }

    public String getTypeName() { "MajDegraded" }
    
}
