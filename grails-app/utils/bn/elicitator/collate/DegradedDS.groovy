package bn.elicitator.collate

import bn.elicitator.Relationship

/**
 * Removes a random collection of responses before collating. 
 */
class DegradedDS extends DawidSkene implements DegradedCollation {

    private int numExpertsRemoved
    
    public DegradedDS( int expertsToRemove, double threshold, Collection<Relationship> toCollate) {
        super( threshold, DegradedCollation.Utils.removeResponses( expertsToRemove, toCollate ) )
        this.numExpertsRemoved = expertsToRemove;
    }

    int getNumExpertsRemoved() { this.numExpertsRemoved }

}
