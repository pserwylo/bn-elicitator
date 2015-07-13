package bn.elicitator.collate

import bn.elicitator.Relationship
import bn.elicitator.Variable
import bn.elicitator.VisitedVariable
import bn.elicitator.analysis.CandidateArc
import bn.elicitator.analysis.CandidateNetwork
import bn.elicitator.auth.User

abstract class CollationAlgorithm {

    private Collection<Relationship> toCollate

    CollationAlgorithm( Collection<Relationship> toCollate ) {
        this.toCollate = toCollate
    }

    final CandidateNetwork run() {
        collateArcs()
        new CandidateNetwork( arcs : resultingArcs ).save( flush : true, failOnError : true )
    }
    
    final Collection<Relationship> getToCollate() { this.toCollate }

    abstract Collection<CandidateArc> getResultingArcs()
    abstract protected void collateArcs()

    protected List<User> getExperts() {
        User.findAllByHasConsented( true ).findAll {
            VisitedVariable.findAllByVisitedByAndDelphiPhase( it, 1 ).size() > 0
        }
    }

    Map<User, Double> getExpertWeights() { getExperts().collectEntries { new MapEntry( it, 1 ) } }

    final Map<User, Double> calcAccuracy( CandidateNetwork goldStandard ) {

        getExperts().collectEntries { User expert ->

            List<Relationship> arcs = toCollate.findAll { it.createdBy.id == expert.id && it.isExistsInitialized }

            double accuracy = -1
            
            if (arcs.size() > 0) {

                int correct = 0
                arcs.each {
                    boolean inGold = goldStandard.contains(it)
                    boolean inThis = it.exists

                    if (inGold && inThis || !inGold && !inThis) {
                        correct++
                    }
                }

                accuracy = correct / arcs.size()

            }
            
            return new MapEntry( expert, accuracy )
            
        }
        
    }

}
