package bn.elicitator

import bn.elicitator.analysis.cpt.CptAnalysisRun
import bn.elicitator.analysis.cpt.CptAnalysisSuite


class AnalyseCptController implements AnalysisController {

    CptAnalysisService cptAnalysisService
    UserService userService

    def index() {
        []
    }
    
    def startAnalysis() {
        CptAnalysisSuite analysis = params.containsKey( "now" ) ? cptAnalysisService.runAnalysis() : cptAnalysisService.scheduleAnalysis()
        redirect( action : 'showAnalysis', params : [ 'id' : analysis.id ] )
    }

    def showAnalysis() {
        def analysis = CptAnalysisSuite.get( params.remove( 'id' ) as Integer )
        return [ analysis : analysis ]
    }

    def viewRun() {
        CptAnalysisRun run = CptAnalysisRun.get( params.remove( 'id' ) as Integer )
        return [ run : run ]
        
    }

    def normalize() {
        CptAnalysisRun run = CptAnalysisRun.get( params.remove( 'runId' ) as Integer )
        run.cpts*.normalize()
        run.save( flush : true, failOnError : true )
        redirect( action : 'viewRun', params : [ id : run.id ] )
        
    }

}
