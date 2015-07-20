package bn.elicitator

import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.analysis.cpt.CptAnalysisRun
import bn.elicitator.analysis.cpt.CptAnalysisSuite
import bn.elicitator.analysis.cpt.DSCptAnalysisRun
import bn.elicitator.analysis.cpt.MeanCptAnalysisRun
import bn.elicitator.collate.cpt.CptCollationAlgorithm
import bn.elicitator.collate.cpt.DawidSkeneCpt
import bn.elicitator.collate.cpt.MeanCpt

class CptAnalysisService {

    Das2004Service das2004Service
    AnalysisService analysisService
    UserService userService

    def analyse( CptAnalysisSuite analysisSuite ) {

        analyseMean( analysisSuite )
        // analyseDawidSkene( analysisSuite )
        
        println "Analysis complete"

        analysisSuite.save( failOnError : true, flush : true )
        
        return analysisSuite
        
    }

    def analyseMean( CptAnalysisSuite analysisSuite ) {
        
        CptAnalysisRun run = analysisRun(
            new MeanCpt( das2004Service, analysisService.goldStandardNetwork ),
            new MeanCptAnalysisRun()
        )

        analysisSuite.analysisRuns.add( run )
        analysisSuite.save()
        
    }
    
    def analyseDawidSkene( CptAnalysisSuite analysisSuite ) {

        for ( def i in [
                /*0.0001,
                0.001,
                0.01, 0.02, 0.03, 0.04, */0.05,
                /*0.10, 0.15, 0.20, 0.25, 0.30*//*, 0.35*/
            ] ) {

            analysisSuite.analysisRuns.add(
                analysisRun(
                    new DawidSkeneCpt( das2004Service, analysisService.goldStandardNetwork ),
                    new DSCptAnalysisRun()
                )
            )
                    
            analysisSuite.save( flush : true, failOnError : true )

        }

    }

    private CptAnalysisRun analysisRun( CptCollationAlgorithm collationAlgorithm, CptAnalysisRun analysis ) {

        println "Getting results for $analysis... "
        List<Cpt> cpts = collationAlgorithm.run()
        cpts*.save()
        println "CPTs saved."
        
        print "Saving analysis... "
        analysis.save()
        println "Done."
        
        return analysis
    }

    CptAnalysisSuite createAnalysisSuite() {
        new CptAnalysisSuite(
                createdBy    : userService.current,
                createdDate  : new Date(),
                analysisRuns : []
        ).save( flush : true, failOnError : true )
    }
    
    CptAnalysisSuite runAnalysis() {

        def analysis = createAnalysisSuite()
        analyse( analysis )
        return analysis
        
    }

    CptAnalysisSuite scheduleAnalysis() {

        def analysis = createAnalysisSuite()
        CptAnalyseJob.run( analysis )
        return analysis
        
    }

}