package bn.elicitator.collate

import bn.elicitator.Relationship
import bn.elicitator.auth.User

interface DegradedCollation {

    abstract int getNumExpertsRemoved()
    
    static class Utils {
        
        static Collection<Relationship> removeResponses(int numExpertsToRemove, Collection<Relationship> responses) {
            List<User> experts = responses*.createdBy.unique()
            List<User> removedExperts = []
            while (numExpertsToRemove > removedExperts.size() && experts.size() > 0) {
                int index = (int) (Math.random() * (experts.size() - 1))
                removedExperts.add(experts.remove(index))
            }

            responses.findAll { !removedExperts.contains(it.createdBy) }
        }
        
    }

}
