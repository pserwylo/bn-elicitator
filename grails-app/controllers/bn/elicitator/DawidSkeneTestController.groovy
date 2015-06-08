package bn.elicitator

import bn.elicitator.analysis.AnalysisRun
import bn.elicitator.analysis.AnalysisSuite

class DawidSkeneTestController {

    ArcAnalysisService arcAnalysisService
    UserService userService

    def index() {
        []
    }
    
    def startAnalysis() {
        redirect( action : 'showAnalysis', params : [ 'id' : arcAnalysisService.beginAnalysis().id ] )
    }

    def showAnalysis() {
        
        def goldStandard = arcAnalysisService.goldStandardNetwork
        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        return [ analysis : analysis, goldStandard : goldStandard ]
        
    }
    
    def downloadDataFrame() {
        
        def goldStandard = arcAnalysisService.goldStandardNetwork
        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        
        render "<pre>"
        render "# Downloaded from ${AppProperties.properties.url}${g.createLink( action : 'downloadDataFrame' )}\n"
        render "analysis.data <- data.frame( label = NULL, original.arcs = NULL, collated.arcs = NULL, acyclic.arcs = NULL, shd.added = NULL, shd.removed = NULL, shd.reversed = NULL, shd.reversed = NULL, shd.total = NULL )\n"
        analysis.analysisRuns.each { AnalysisRun run ->
            def shd = run.acyclicNetwork.calcShd( goldStandard )
            render "analysis.data <- rbind( analysis.data, data.frame( " +
                "label = \"${run.toShortString()}\", " +
                "original.arcs = ${run.startingNetwork.arcs.size()}, " + 
                "acyclic.arcs = ${run.acyclicNetwork.arcs.size()}, " +
                "collated.arcs = ${run.collatedNetwork.arcs.size()}, " +
                "shd.added = ${shd.added.size()}, " +
                "shd.removed = ${shd.added.size()}, " +
                "shd.reversed = ${shd.added.size()}, " +
                "shd.total = $shd.shd, " +
                "roc.tp = $shd.truePositives, " +
                "roc.fp = $shd.falsePositives," +
                "roc.tpr = $shd.truePositiveRate, " +
                "roc.fpr = $shd.falsePositiveRate" +
            ") )\n"
        }
        render "</pre>"

    }

}
