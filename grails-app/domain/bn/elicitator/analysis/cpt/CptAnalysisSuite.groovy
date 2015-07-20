package bn.elicitator.analysis.cpt

import bn.elicitator.auth.User

class CptAnalysisSuite {

    static hasMany = [ analysisRuns: CptAnalysisRun ]
    static mapping = {
        analysisRuns cascade: 'all'
    }

    List<CptAnalysisRun> analysisRuns
    Date createdDate
    User createdBy

}
