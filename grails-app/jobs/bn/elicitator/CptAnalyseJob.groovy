package bn.elicitator

import bn.elicitator.analysis.cpt.CptAnalysisSuite

class CptAnalyseJob {
    
    CptAnalysisService analysisService
    
    static triggers = {}
    
    def execute( context ) {

        Object obj = null
        obj.toString()
        CptAnalysisSuite analysisSuite = context.mergedJobDataMap.get( 'analysisSuite' )
        
        analysisService.analyse( analysisSuite )
        
    }
    
    public static def run( CptAnalysisSuite analysisSuite ) {
        triggerNow( [ analysisSuite : analysisSuite ] )
    }
    
}
