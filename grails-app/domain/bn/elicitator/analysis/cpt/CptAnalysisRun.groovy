package bn.elicitator.analysis.cpt

abstract class CptAnalysisRun {

    static hasMany = [ cpts : Cpt ]
    static mapping = {
        cpts cascade: 'all'
    }
    
    public abstract String toShortString()

}
