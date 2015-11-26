package bn.elicitator

import bn.elicitator.analysis.cpt.CptAnalysisRun
import bn.elicitator.analysis.cpt.CptAnalysisSuite


class AnalyseCptController implements AnalysisController {

    CptAnalysisService cptAnalysisService
    VerifyDasService verifyDasService
    UserService userService

    def index() {
        [ analysisSuites: CptAnalysisSuite.list() ]
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
    
    def verifyDas() {
        CptAnalysisSuite suite = CptAnalysisSuite.get( params.remove( 'id' ) as Integer )
        List<EstimatedAndElicitedProbabilities> results = verifyDasService.verify(suite)

        println "Printing header of .tsv"

        header("Content-Type", "text/csv")
        
        render([
              "probabilityOf",
              "user",
              "estimatedProb",
              "elicitedProb",
        ].join("\t") + "\n")
        
        println "Printing body of .tsv"
        
        results.each { outerIt ->
            outerIt.each { innerIt ->
                render([
                    innerIt.estimated.toShortStringWithoutValue(),
                    innerIt.estimated.createdById,
                    innerIt.estimated.probability,
                    innerIt.elicited.probability
                ].join("\t") + "\n")
                
            }
        }

        println "Done!"

        // return [ results : results ]
    }

    def normalize() {
        CptAnalysisRun run = CptAnalysisRun.get( params.remove( 'runId' ) as Integer )
        run.cpts*.normalize()
        run.save( flush : true, failOnError : true )
        redirect( action : 'viewRun', params : [ id : run.id ] )
    }

}
