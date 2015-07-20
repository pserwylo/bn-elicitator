package bn.elicitator.analysis

import bn.elicitator.auth.User

class AnalysisSuite {

    static hasMany = [ analysisRuns: AnalysisRun ]
    
    List<AnalysisRun> analysisRuns
    Date createdDate
    User createdBy

}
