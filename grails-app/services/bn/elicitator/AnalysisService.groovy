package bn.elicitator

import bn.elicitator.analysis.AnalysisSuite
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.analysis.CandidateNetwork
import bn.elicitator.analysis.DSAnalysisRun
import bn.elicitator.anomalies.cycles.CycleRemover
import bn.elicitator.collate.DawidSkene
import bn.elicitator.network.Graph
import bn.elicitator.troia.DawidSkeneRelationship

class AnalysisService {

    UserService userService

    /*

Only looking at the insurance network, I could estimate a prior probability fo an arc at 0.07
(52 out of 729 possible arcs)

> bn <- read.net( 'data/insurance.net' )
> length( arcs( bn ) ) / 2
[1] 52
> length( nodes( bn ) )
[1] 27
> length( nodes( bn ) ) ^ 2
[1] 729
> ( length( arcs( bn ) ) / 2 ) / ( length( nodes( bn ) ) ^ 2 )
[1] 0.07133059

     */
    
    def analyseDawidSkene( AnalysisSuite analysisSuite ) {

        Collection<Relationship> toAnalyse = relationshipsToAnalyse
        CandidateNetwork fullNetwork = createCandidateNetwork( toAnalyse )
        
        for ( def i in [
                0.0000001,
                0.000001, 
                0.00001, 
                0.0001, 
                0.001, 
                0.01, 0.02, 0.03, 0.04, 0.05,
                0.10, 0.15, 0.20
            ] ) {

            analyseWithPrior(
                analysisSuite,
                toAnalyse,
                new DSAnalysisRun(
                    startingNetwork : fullNetwork,
                    prior           : i
                )
            )
            
        }

    }

    private void analyseWithPrior( AnalysisSuite analysisSuite, Collection<Relationship> toAnalyse, DSAnalysisRun analysis ) {

        print "Getting D&S results (prior $analysis.prior})... "
        
        analysis.collatedNetwork = runDawidSkene( toAnalyse, analysis.prior )
        
        println "Done (${analysis.collatedNetwork.arcs.size()} arcs)."
        
        print "Removing cycles... "
        
        CycleRemover cycleRemover = new CycleRemover( analysis.collatedNetwork ).removeCycles()
        analysis.acyclicNetwork   = cycleRemover.dag
        def summary      = cycleRemover.summary
        
        println "Done (${analysis.acyclicNetwork.arcs.size()} arcs left)."

        println "\n" + summary.join( "\n" ) + "\n\n"
        
        analysis.save( flush : true, failOnError : true )
        
        analysisSuite.analysisRuns.add( analysis )
        analysisSuite.save( flush : true, failOnError : true )
    }

    /**
     * Connect to the Troia server which does the Dawid & Skene calculations for us.
     * This will decide on a set of arcs to include, based on how popular they were.
     */
    private CandidateNetwork runDawidSkene( Collection<Relationship> toAnalyse, Double prior ) {
        DawidSkene dawidSkene = new DawidSkene( prior, toAnalyse )
        def arcs = dawidSkene.resultingArcs
        return new CandidateNetwork( arcs : arcs ).save( failOnError : true )
    }

    private void writeOutput( prior, predictions ) {

        def file = new File( "/tmp/ds-structure.prior-$prior.tsv" )

        file.withWriter { Writer out ->

            out.println([
                    "Prior",
                    "Parent",
                    "Child",
            ].join("\t"))

        }

        file.withWriterAppend { Writer out ->
            predictions.each { DawidSkeneRelationship rel ->
                out.println([
                        prior,
                        rel.relationship.parent.label,
                        rel.relationship.child.label,
                ].join("\t"))
            }
        }
        
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