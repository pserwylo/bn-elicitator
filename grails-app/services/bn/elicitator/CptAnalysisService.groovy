package bn.elicitator

import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.analysis.cpt.CptAnalysisRun
import bn.elicitator.analysis.cpt.CptAnalysisSuite
import bn.elicitator.analysis.cpt.DSCptAnalysisRun
import bn.elicitator.analysis.cpt.MeanCptAnalysisRun
import bn.elicitator.auth.User
import bn.elicitator.collate.cpt.CptCollationAlgorithm
import bn.elicitator.collate.cpt.DawidSkeneCpt
import bn.elicitator.collate.cpt.MeanCpt
import bn.elicitator.das2004.CompletedDasVariable

class CptAnalysisService {

    Das2004Service das2004Service
    AnalysisService analysisService
    UserService userService

    def analyse( CptAnalysisSuite analysisSuite ) {

        analysisSuite.unprocessedCpts = loadCptsForAnalysis()
        Map<Variable, List<Cpt>> cptsForAnalysis = analysisSuite.unprocessedCpts.groupBy { it?.variable }
        
        analyseMean( analysisSuite, cptsForAnalysis )
        analyseDawidSkene( analysisSuite, cptsForAnalysis )
        
        print "Analysis complete, saving... "

        analysisSuite.save( failOnError : true, flush: true )
        
        println "Done!"
        
        return analysisSuite
        
    }

    def analyseMean( CptAnalysisSuite analysisSuite, Map<Variable, List<Cpt>> cptsForAnalysis ) {
        CptAnalysisRun run = analysisRun(
            new MeanCpt( cptsForAnalysis, das2004Service, analysisService.goldStandardNetwork ),
            new MeanCptAnalysisRun()
        )

        analysisSuite.analysisRuns.add( run )
    }
    
    def analyseDawidSkene( CptAnalysisSuite analysisSuite, Map<Variable, List<Cpt>> cptsForAnalysis ) {

        analysisSuite.analysisRuns.add(
            analysisRun(
                new DawidSkeneCpt( cptsForAnalysis, das2004Service, analysisService.goldStandardNetwork ),
                new DSCptAnalysisRun()
            )
        )
                
        analysisSuite.save( flush : true, failOnError : true )

    }

    private List<Cpt> loadCptsForAnalysis() {
        analysisService.goldStandardNetwork.variables.collect { Variable variable ->
            processVariable( variable )
        }.flatten()
    }

    /**
     * Find all people allocated this variable, then for each of those people, produce a CPT.
     * Then collate those CPTs together.
     */
    private List<Cpt> processVariable( Variable variable ) {

        println "Processing variable $variable.label..."

        Collection<Variable> parents = analysisService.goldStandardNetwork.getParentsOf variable
        List<CompletedDasVariable> completedVars = CompletedDasVariable.findAllByVariable( variable )

        List<Cpt> cpts = completedVars.collect { CompletedDasVariable completed ->
            try {
                return processUsersVariable( completed.completedBy, variable, parents )
            } catch ( Exception e ) {
                // TODO: Actually bail when this happens, it really shouldn't happen...
                println """
******************************************************************************
  Error occured while processing variable $variable.label

  Exception: ${e.getMessage()}

  Cause: ${e.cause?.getMessage()}
******************************************************************************
"""
                return null
            } finally {
                println "OK."
            }
        }.findAll { it != null }

        return cpts ?: [ new Cpt( probabilities : [] ) ]
    }

    private Cpt processUsersVariable( User user, Variable child, Collection<Variable> parents ) {

        println "Processing variable $child.label (for user $user.id)..."

        List<Probability> probs
        if ( parents.size() == 0 ) {
            probs = das2004Service.calcMarginalProbability( child, user )
        } else if ( parents.size() == 1 ) {
            probs = das2004Service.singleParentConditional( child, user )
        } else {
            probs = das2004Service.calcConditionalProbability( child, parents, user )
        }
        new Cpt( probabilities : probs, createdBy : user )
    }

    private CptAnalysisRun analysisRun( CptCollationAlgorithm collationAlgorithm, CptAnalysisRun analysis ) {
        println "Getting results for $analysis... "
        analysis.cpts = collationAlgorithm.run()
        analysis.cpts.each { it.createdBy = userService.current }
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