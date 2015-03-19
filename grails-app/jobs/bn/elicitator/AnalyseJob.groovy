package bn.elicitator

import bn.elicitator.analysis.AnalysisSuite

class AnalyseJob {
    
    AnalysisService analysisService
    
    static triggers = {}
    
    def execute( context ) {

        Object obj = null
        obj.toString()
        AnalysisSuite analysisSuite = context.mergedJobDataMap.get( 'analysisSuite' )
        
        analysisService.analyse( analysisSuite )
        
    }
    
    public static def run( AnalysisSuite analysisSuite ) {
        triggerNow( [ analysisSuite : analysisSuite ] )
    }
    
}
