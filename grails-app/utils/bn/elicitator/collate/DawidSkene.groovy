package bn.elicitator.collate

import bn.elicitator.Relationship
import bn.elicitator.Variable
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.auth.User
import com.datascience.core.base.AssignedLabel
import com.datascience.core.base.LObject
import com.datascience.core.base.Worker
import com.datascience.core.nominal.CategoryValue
import com.datascience.core.nominal.INominalData
import com.datascience.core.nominal.NominalProject
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators
import com.datascience.core.nominal.decision.WorkerEstimator
import com.datascience.core.nominal.decision.WorkerQualityCalculator
import com.datascience.core.results.DatumResult
import com.datascience.core.results.ResultsFactory
import com.datascience.core.results.WorkerResult
import com.datascience.datastoring.datamodels.memory.InMemoryNominalData
import com.datascience.datastoring.datamodels.memory.InMemoryResults
import com.datascience.gal.BatchDawidSkene
import com.datascience.utils.CostMatrix

class DawidSkene extends CollationAlgorithm {

    protected double prior
    protected NominalProject project

    public DawidSkene( double prior, Collection<Relationship> toCollate ) {
        super( toCollate )
        this.prior = prior
    }

    public def getLabelResults() {
        project.results.getDatumResults( project.data.objects )
    }

    public def getWorkerResults() {
        project.results.getWorkerResults( project.data.workers )
    }

    @Override
    Map<User, Double> getExpertWeights() {
        WorkerQualityCalculator wqc = new WorkerEstimator(LabelProbabilityDistributionCostCalculators.get( "ExpectedCost" ) );

        workerResults.collectEntries {
            Worker worker = it.key
            new MapEntry(
                User.findById(worker.name as Integer),
                wqc.getQuality( project, worker )
            )

        }
    }

    protected boolean includeArc( DatumResult result ) { result.categoryProbabilites[ "yes" ] > 0.5 }
    
    public Collection<CandidateArc> getResultingArcs() {
        labelResults.findAll { includeArc( it.value ) }.collect {
            objectToArc( it.key )
        }
    }

    protected void collateArcs() {
        def workerResultsFactory = new ResultsFactory.WorkerResultNominalFactory()
        workerResultsFactory.categories = [ "yes", "no" ]

        project = new NominalProject(
            new BatchDawidSkene(),
            setupData(),
            new InMemoryResults<String, DatumResult, WorkerResult>(
                new ResultsFactory.DatumResultFactory(),
                workerResultsFactory
            )
        )

        project.algorithm.compute()
    }

    protected INominalData setupData() {

        INominalData data = new InMemoryNominalData()

        data.initialize(
            [ "yes", "no", ],
            [ new CategoryValue( "yes", prior ), new CategoryValue( "no", 1 - prior ) ],
            new CostMatrix<String>()
        )

        toCollate.collect { relationshipToObject( it ) }.unique().each {
            data.addObject( new LObject( it ) )
        }

        toCollate.collect { it.createdBy.id }.unique().each {
            data.addWorker( new Worker( String.valueOf( it ) ) )
        }

        toCollate.each { Relationship relationship ->

            LObject object = data.getObject( relationshipToObject( relationship ) )
            Worker  worker = data.getWorker( String.valueOf( relationship.createdBy.id ) )

            data.addAssign(
                new AssignedLabel<String>(
                    worker,
                    object,
                    relationship.exists ? "yes" : "no"
                )
            )
        }

        return data

    }

    protected CandidateArc objectToArc( LObject<String> object ) {
        String[] parts = object.getName().split( '-' )
        CandidateArc.getOrCreate(
            Variable.get( parts[ 0 ] as Long ),
            Variable.get( parts[ 1 ] as Long )
        )
    }

    protected String relationshipToObject( Relationship relationship ) {
        "$relationship.parent.id-$relationship.child.id"
    }

}
