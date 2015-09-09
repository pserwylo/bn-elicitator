package bn.elicitator.collate

import bn.elicitator.Relationship

/**
 * Removes a random collection of responses before collating. 
 */
class DegradedMajorityVote extends MajorityVote implements DegradedCollation {

    private int numExpertsRemoved
    
    public DegradedMajorityVote( int expertsToRemove, int threshold, Collection<Relationship> toCollate) {
        super( threshold, DegradedCollation.Utils.removeResponses( expertsToRemove, toCollate ) )
        this.numExpertsRemoved = expertsToRemove
    }

    int getNumExpertsRemoved() { this.numExpertsRemoved }

}
