package bn.elicitator

import bn.elicitator.analysis.AnalysisSuite

class AnalyseJob {
    
    ArcAnalysisService arcAnalysisService
    
    static triggers = {}
    
    def execute( context ) {

        Object obj = null
        obj.toString()
        AnalysisSuite analysisSuite = context.mergedJobDataMap.get( 'analysisSuite' )
        
        arcAnalysisService.analyse( analysisSuite )
        
    }
    
    public static def run( AnalysisSuite analysisSuite ) {
        triggerNow( [ analysisSuite : analysisSuite ] )
    }
    
}
