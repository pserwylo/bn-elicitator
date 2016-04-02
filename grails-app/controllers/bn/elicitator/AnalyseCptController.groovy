package bn.elicitator

import bn.elicitator.analysis.cpt.CptAnalysisRun
import bn.elicitator.analysis.cpt.CptAnalysisSuite
import bn.elicitator.auth.User


class AnalyseCptController implements AnalysisController {

    CptAnalysisService cptAnalysisService
    VerifyDasService verifyDasService
    VerifyCoherencyService verifyCoherencyService
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

    def verifyConsistency() {
        Map<User, List<Double>> results = verifyCoherencyService.verify()

        header('Content-Type', 'text/csv')
        header('Content-Disposition', 'attachment; filename="verifyConsistency.csv"')

        render([
            "user",
            "totalProb",
        ].join(",") + "\n")

        results.each { User user, List<Double> usersValues ->
            usersValues.each { Double totalProb ->
                render([
                    user.id,
                    totalProb
                ].join(",") + "\n")
            }
        }
    }

    def viewRun() {
        CptAnalysisRun run = CptAnalysisRun.get( params.remove( 'id' ) as Integer )
        return [ run : run ]
    }
    
    def verifyDas() {
        CptAnalysisSuite suite = CptAnalysisSuite.get( params.remove( 'id' ) as Integer )
        List<EstimatedAndElicitedProbabilities> results = verifyDasService.verify(suite)

        header("Content-Type", "text/csv")
        
        render([
              "probabilityOf",
              "user",
              "estimatedProb",
              "elicitedProb",
        ].join("\t") + "\n")

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
    }

    def normalize() {
        CptAnalysisRun run = CptAnalysisRun.get( params.remove( 'runId' ) as Integer )
        run.cpts*.normalize()
        run.save( flush : true, failOnError : true )
        redirect( action : 'viewRun', params : [ id : run.id ] )
    }

}
