package bn.elicitator

import bn.elicitator.analysis.AnalysisRun
import bn.elicitator.analysis.AnalysisSuite
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.analysis.CandidateNetwork
import bn.elicitator.analysis.DSAnalysisRun
import bn.elicitator.analysis.MajAnalysisRun
import bn.elicitator.anomalies.cycles.CycleRemover
import bn.elicitator.collate.arcs.CollationAlgorithm
import bn.elicitator.collate.arcs.DawidSkene
import bn.elicitator.collate.arcs.MajorityVote

class ArcAnalysisService {

    UserService userService
    
    def analyse( AnalysisSuite analysisSuite ) {
        
        Collection<Relationship> toAnalyse = relationshipsToAnalyse
        CandidateNetwork fullNetwork = createCandidateNetwork( toAnalyse )

        analyseMajority( analysisSuite, toAnalyse, fullNetwork )
        analyseDawidSkene( analysisSuite, toAnalyse, fullNetwork )
        
    }

    def analyseMajority( AnalysisSuite analysisSuite, Collection<Relationship> toAnalyse, CandidateNetwork fullNetwork ) {
        
        // TODO: Be less specific about these numbers. I've chosen 6 as it is the maximum amount of allocations for any
        // given question during my evaluation. And I've chosen 3 because the number of cycles in networks with higher
        // thresholds than 3 cause memory errors in the JVM.
        for ( def i in 6..3 ) {

            analysisSuite.analysisRuns.add(
                analysisRun(
                    new MajorityVote( i, toAnalyse ),
                    new MajAnalysisRun(
                        startingNetwork : fullNetwork,
                        threshold       : i
                    )
                )
            )
            
            analysisSuite.save( flush : true, failOnError : true )
        }
        
    }
    
    def analyseDawidSkene( AnalysisSuite analysisSuite, Collection<Relationship> toAnalyse, CandidateNetwork fullNetwork ) {

        for ( def i in [
                0.0000001,
                0.000001, 
                0.00001, 
                0.0001, 
                0.001, 
                0.01, 0.02, 0.03, 0.04, 0.05,
                0.10, 0.15, 0.20
            ] ) {

            analysisSuite.analysisRuns.add(
                analysisRun(
                    new DawidSkene( i, toAnalyse ),
                    new DSAnalysisRun(
                        startingNetwork : fullNetwork,
                        prior           : i
                    )
                )
            )
                    
            analysisSuite.save( flush : true, failOnError : true )

        }

    }

    private AnalysisRun analysisRun( CollationAlgorithm collationAlgorithm, AnalysisRun analysis ) {

        print "Getting results for $analysis..."
        
        analysis.collatedNetwork = collationAlgorithm.run()
        
        println "Done (${analysis.collatedNetwork.arcs.size()} arcs)."

        print collationAlgorithm.expertWeights.collect { "  $it.key.id: $it.value" }.join( "\n" )
        
        print "Removing cycles... "
        
        CycleRemover cycleRemover = new CycleRemover( analysis.collatedNetwork ).removeCycles()
        analysis.acyclicNetwork   = cycleRemover.dag
        def summary               = cycleRemover.summary
        
        println "Done (${analysis.acyclicNetwork.arcs.size()} arcs left)."
        println "\n" + summary.join( "\n" ) + "\n\n"
        
        analysis.save( flush : true, failOnError : true )
    }

    /**
     * Given a set of relationships, create a network structure which we can persist to the database.
     * It doesn't have to be a valid DAG or BN structure, because it will be the basis for which we
     * perform modifications _in order to get a valid DAG_. We need to persist it to the database so
     * that the results of our analysis (i.e. removing arcs to eliminate cycles) can be browsed via
     * the web interface.
     */
    private CandidateNetwork createCandidateNetwork( Collection<Relationship> relationships ) {
        new CandidateNetwork(
            arcs : relationships.findAll { it.exists }.collect {
                CandidateArc.getOrCreate( it.parent, it.child )
            }.unique()
        ).save( flush : true )
    }

    /**
     * Only include relationships from the first delphi phase and which have actually been saved by
     * a user. Those which were created because the question was allocated to a user, but then never
     * answered, are excluded.
     *
     * In the future, this should be changed to allow for delphi phases other than 1.
     */
    private Collection<Relationship> getRelationshipsToAnalyse() {
        Relationship.findAllByIsExistsInitializedAndDelphiPhase( true, 1 )
    }

    AnalysisSuite beginAnalysis() {

        def analysis = new AnalysisSuite(
                createdBy    : userService.current,
                createdDate  : new Date(),
                analysisRuns : []
        ).save( flush : true, failOnError : true )

        AnalyseJob.run( analysis )
        
        return analysis
        
    }

    CandidateNetwork getGoldStandardNetwork() {

        /*
         * Source for the Insurance network structure is from: http://www.bnlearn.com/documentation/man/insurance.html
         */
        def goldStandard = "[Age][Mileage][SocioEcon|Age][GoodStudent|Age:SocioEcon]" +
                "[RiskAversion|Age:SocioEcon][OtherCar|SocioEcon][VehicleYear|SocioEcon:RiskAversion]" +
                "[MakeModel|SocioEcon:RiskAversion][SeniorTrain|Age:RiskAversion]" +
                "[HomeBase|SocioEcon:RiskAversion][AntiTheft|SocioEcon:RiskAversion]" +
                "[RuggedAuto|VehicleYear:MakeModel][Antilock|VehicleYear:MakeModel]" +
                "[DrivingSkill|Age:SeniorTrain][CarValue|VehicleYear:MakeModel:Mileage]" +
                "[Airbag|VehicleYear:MakeModel][DrivQuality|RiskAversion:DrivingSkill]" +
                "[Theft|CarValue:HomeBase:AntiTheft][Cushioning|RuggedAuto:Airbag]" +
                "[DrivHist|RiskAversion:DrivingSkill][Accident|DrivQuality:Mileage:Antilock]" +
                "[ThisCarDam|RuggedAuto:Accident][OtherCarCost|RuggedAuto:Accident]" +
                "[MedCost|Age:Accident:Cushioning][ILiCost|Accident]" +
                "[ThisCarCost|ThisCarDam:Theft:CarValue][PropCost|ThisCarCost:OtherCarCost]"

        CandidateNetwork network = new CandidateNetwork( arcs : [] )

        goldStandard[ 1 .. -2 ].split( '\\]\\[' ).each { String fragment ->

            String[] parts = fragment.split( '\\|' )
            String child   = parts[ 0 ]

            if ( parts.length > 1 ) {

                String[] parents = parts[ 1 ].split( ':' )

                parents.each { String parent ->

                    def from = Variable.findByLabel( parent )
                    def to   = Variable.findByLabel( child )

                    if ( from != null && to != null ) {
                        network.arcs.add(
                            new CandidateArc(
                                    from: from,
                                    to: to,
                            )
                        )
                    }

                }
            }
        }

        return network

    }

}