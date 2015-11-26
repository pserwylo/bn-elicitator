package bn.elicitator.analysis.cpt

import bn.elicitator.auth.User

class CptAnalysisSuite {

    static hasMany = [
        analysisRuns: CptAnalysisRun, 
        unprocessedCpts: Cpt
    ]
    
    static mapping = {
        analysisRuns cascade: 'all'
        unprocessedCpts cascade: 'all'
    }

    List<CptAnalysisRun> analysisRuns
    List<Cpt> unprocessedCpts
    Date createdDate
    User createdBy

}
