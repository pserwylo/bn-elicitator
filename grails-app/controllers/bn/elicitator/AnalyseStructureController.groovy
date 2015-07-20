package bn.elicitator

import bn.elicitator.analysis.AnalysisRun
import bn.elicitator.analysis.AnalysisSuite
import bn.elicitator.analysis.DSAnalysisRun
import bn.elicitator.network.Arc


class AnalyseStructureController implements AnalysisController {

    AnalysisService analysisService
    UserService userService

    def index() {
        []
    }
    
    def startAnalysis() {
        AnalysisSuite analysis = params.containsKey( "now" ) ? analysisService.runAnalysis() : analysisService.scheduleAnalysis()
        redirect( action : 'showAnalysis', params : [ 'id' : analysis.id ] )
    }

    def showAnalysis() {
        
        def goldStandard = analysisService.goldStandardNetwork
        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        return [ analysis : analysis, goldStandard : goldStandard ]
        
    }
    
    def downloadNetworkStructures() {

        render( [ "Type", "Prior", "From", "To" ].join( "\t" ) + "\n" )
        
        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        analysis.analysisRuns.each { AnalysisRun run ->
            run.acyclicNetwork.arcs.each { Arc arc ->

                render( [
                    run instanceof DSAnalysisRun ? "DS" : "Maj",
                    run.threshold,
                    arc.from.label,
                    arc.to.label
                ].join( "\t" ) + "\n" )
            }
            
        }

    }

    def downloadExpertWeights() {

        render( [ "Type", "Prior", "Expert", "Estimated_Accuracy", "Actual_Accuracy" ].join( "\t" ) + "\n" )

        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        analysis.analysisRuns.each { AnalysisRun run ->
            run.expertAccuracy.each { accuracy ->
                render( [
                    /* Type               */ run.toShortString(),
                    /* Prior              */ run.threshold,
                    /* Expert             */ accuracy.key,
                    /* Estimated_Accuracy */ accuracy.value,
                    /* Actual_Accuracy    */ run.expertWeights.find { weight -> weight.key == accuracy.key }.value
                ].join( "\t" ) + "\n" )
            }

        }

    }

    def downloadDataFrame() {
        
        def goldStandard = analysisService.goldStandardNetwork
        def analysis = AnalysisSuite.get( params.remove( 'id' ) as Integer )
        
        render "<pre>"
        render "# Downloaded from ${AppProperties.properties.url}${g.createLink( action : 'downloadDataFrame' )}\n"
        render "analysis.data <- data.frame( label = NULL, original.arcs = NULL, collated.arcs = NULL, acyclic.arcs = NULL, shd.added = NULL, shd.removed = NULL, shd.reversed = NULL, shd.reversed = NULL, shd.total = NULL )\n"
        analysis.analysisRuns.each { AnalysisRun run ->
            def shdAcyclic = run.acyclicNetwork.calcShd( goldStandard )
            def shdCollated = run.collatedNetwork.calcShd( goldStandard )
            render "analysis.data <- rbind( analysis.data, data.frame( " +
                "label = \"${run.toShortString()}\", " +
                "original.arcs = ${run.startingNetwork.arcs.size()}, " +
                "acyclic.arcs = ${run.acyclicNetwork.arcs.size()}, " +
                "collated.arcs = ${run.collatedNetwork.arcs.size()}, " +
                "shd.acyclic.added = ${shdAcyclic.added.size()}, " +
                "shd.acyclic.removed = ${shdAcyclic.added.size()}, " +
                "shd.acyclic.reversed = ${shdAcyclic.added.size()}, " +
                "shd.acyclic.total = $shdAcyclic.shd, " +
                "roc.acyclic.tp = $shdAcyclic.truePositives, " +
                "roc.acyclic.fp = $shdAcyclic.falsePositives," +
                "roc.acyclic.tpr = $shdAcyclic.truePositiveRate, " +
                "roc.acyclic.fpr = $shdAcyclic.falsePositiveRate" +
                "shd.collated.added = ${shdCollated.added.size()}, " +
                "shd.collated.removed = ${shdCollated.added.size()}, " +
                "shd.collated.reversed = ${shdCollated.added.size()}, " +
                "shd.collated.total = $shdCollated.shd, " +
                "roc.collated.tp = $shdCollated.truePositives, " +
                "roc.collated.fp = $shdCollated.falsePositives," +
                "roc.collated.tpr = $shdCollated.truePositiveRate, " +
                "roc.collated.fpr = $shdCollated.falsePositiveRate" +
            ") )\n"
        }
        render "</pre>"

    }

}
