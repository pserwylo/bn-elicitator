package bn.elicitator.collate.cpt

import bn.elicitator.Probability
import bn.elicitator.Variable
import bn.elicitator.analysis.cpt.Cpt
import bn.elicitator.algorithms.Das2004Service
import bn.elicitator.analysis.CandidateNetwork
import com.datascience.core.base.AssignedLabel
import com.datascience.core.base.ContValue
import com.datascience.core.base.IData
import com.datascience.core.base.LObject
import com.datascience.core.base.Worker
import com.datascience.core.results.DatumContResults
import com.datascience.core.results.ResultsFactory
import com.datascience.core.results.WorkerContResults
import com.datascience.datastoring.datamodels.memory.InMemoryResults
import com.datascience.galc.ContinuousIpeirotis
import com.datascience.galc.ContinuousProject
import com.datascience.galc.EmpiricalData

class DawidSkeneCpt extends CptCollationAlgorithm {

    DawidSkeneCpt(Map<Variable, List<Cpt>> cptsForAnalysis, Das2004Service service, CandidateNetwork goldStandardStructure) {
        super(cptsForAnalysis, service, goldStandardStructure)
    }

    protected List<Cpt> combineCpts( Map<Variable, List<Cpt>> cpts ) {
        if ( cpts ) {
            collateFromTroia( cpts.values().flatten() )
        } else {
            new Cpt( probabilities: [] )
        }
    }

    private List<Cpt> collateFromTroia( List<Cpt> cpts ) {

        // This will help us retrieve the probabilities from the results.
        // Troia only lets strings be used as labels (as it is designed for usage over HTTP) and so
        // it is difficult to map objects to strings. This pRevents having to deserialize the strings and
        // figure out what the child and parent states of a particular conditional probability were.
        Map<String, Probability> probabilityLabelMap = cpts*.probabilities.flatten().collectEntries { Probability prob ->
            new MapEntry( prob.toStringWithoutValue(), prob )
        }

        def results = runTroia( cpts )
        
        results.entrySet().collect { LObject<ContValue> object, DatumContResults value ->
            
            if ( !probabilityLabelMap.containsKey( object.name ) ) {
                throw new Exception( "Couldn't find probability $object.name while collating Troia results." )
            }
            
            Probability oldProb = probabilityLabelMap[ object.name ]
            
            new Probability( 
                    probability : value.est_value,
                    childState: oldProb.childState,
                    parentStates: oldProb.parentStates
            )
            
        }.groupBy { Probability prob -> 
            prob.childState.variable
        }.collect { MapEntry entry ->
            new Cpt( probabilities : entry.value as List<Probability> )
        }
    }

    private Map<LObject<ContValue>, DatumContResults> runTroia( List<Cpt> cpts ) {

        ContinuousProject project = createTroia(cpts)
        project.algorithm.compute()

        project.results.getDatumResults(project.data.objects)
    }

    private ContinuousProject createTroia( List<Cpt> cpts ) {
        new ContinuousProject(
                new ContinuousIpeirotis(),
                setupData( cpts ),
                new InMemoryResults<ContValue, DatumContResults, WorkerContResults>(
                        new ResultsFactory.DatumResultFactory(),
                        new ResultsFactory.WorkerContResultFactory()
                )
        )
    }

    private IData<ContValue> setupData( List<Cpt> cpts ) {

        EmpiricalData data = new EmpiricalData()

        cpts.each { Cpt cpt ->
            Worker worker = data.getOrCreateWorker( cpt.createdBy?.id?.toString() )
            data.addWorker( worker )
            cpt.probabilities.each { Probability prob ->
                LObject<ContValue> object = data.getOrCreateObject( prob.toStringWithoutValue() )
                data.addObject( object )
                data.addAssign( new AssignedLabel<ContValue>( worker, object, new ContValue( prob.probability ) ) )
            }
        }
        
        return data

    }

}
