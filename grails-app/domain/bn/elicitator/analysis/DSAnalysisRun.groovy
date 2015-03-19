package bn.elicitator.analysis

/**
 *  
 */
class DSAnalysisRun extends AnalysisRun {

    public Double getPrior() { threshold }
    
    public setPrior( Double prior ) { threshold = prior }

    public String toString() { "Dawid & Skene analysis (prior $prior)" }
    
    public String toShortString() { "DS_$prior" }
    
}
