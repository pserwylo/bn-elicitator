package bn.elicitator

import bn.elicitator.analysis.AnalysisSuite

class DawidSkeneTestController {

    AnalysisService analysisService
    UserService userService

    def index() {
        []
    }
    
    def startAnalysis() {
        redirect( action : 'showAnalysis', params : [ 'id' : analysisService.beginAnalysis().id ] )
    }
    
    def showAnalysis() {
        
        def goldStandard = analysisService.goldStandardNetwork
        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        return [ analysis : analysis, goldStandard : goldStandard ]
        
    }

}
