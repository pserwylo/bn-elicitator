package bn.elicitator.algorithm

import Jama.EigenvalueDecomposition
import Jama.Matrix
import bn.elicitator.Variable
import bn.elicitator.auth.User
import bn.elicitator.das2004.CompatibleParentConfiguration
import bn.elicitator.das2004.PairwiseComparison
import bn.elicitator.das2004.ProbabilityEstimation
import com.sun.mail.iap.BadCommandException

/**
 *
 * Implements the Analytic Heirachy Process (Saaty, 1988), whereby pairwise comaparisons are used to establish a weighting
 * for various items to see which is more important relative to the others.*
 * 
 *  
 *       How much
 *   <-    more    ->
 *      do these...
 *
 *     W   X   Y   Z
 *   +---+---+---+---+      ^
 * W | 1 |   |   |   |      |
 *   +---+---+---+---+
 * X |   | 1 | 3 |   |  influence
 *   +---+---+---+---+
 * Y |   |1/3| 1 |   |    these?
 *   +---+---+---+---+
 * Z |   |   |   | 1 |      |
 *   +---+---+---+---+      v
 *
 *   e.g. Y is three times more important than X, while X is 1/3rd the importance of Y.
 *
 */
class AHP {

    private final User user
    private final Variable child
    private final List<Variable> parents
    private final Map<Variable,Map<Variable,BigDecimal>> matrix = [ : ]
    
    private static final Cache CACHE = new Cache()

    public AHP( Variable child, Collection<Variable> parents, User user ) {

        this.child = child
        this.user = user
        this.parents = parents
        parents.each { Variable variable ->
            matrix[ variable ] = [ : ]
            parents.each { Variable other ->
                matrix[ variable ][ other ] = variable.id == other.id ? 1 : null
            }
        }
        
    }
    
    private void loadPairwiseComparisons() {
        def comparisons = findPairwiseComparisons()
        comparisons.each { PairwiseComparison comparison ->
            BigDecimal weight = 1
            if ( comparison.mostImportantParent?.id == comparison.parentOne.id ) {
                weight = comparison.weight
            } else if ( comparison.mostImportantParent?.id == comparison.parentTwo.id ) {
                weight = 1 / comparison.weight
            }

            matrix[ comparison.parentOne ][ comparison.parentTwo ] = weight
            matrix[ comparison.parentTwo ][ comparison.parentOne ] = 1 / weight
        }
    }
    
    private Matrix calcEigenVectors() {

        double[][] values = new double[ matrix.size() ][]

        try {
            parents.eachWithIndex { Variable one, int i ->
                values[ i ] = new double[ matrix.size() ]
                // TODO: Don't iterate over this, instead iterate over matrix properly...
                parents.eachWithIndex { Variable two, int j ->
                    double value
                    if ( matrix[ one ][ two ] == null ) {
                        value = 0;
                        throw new AlgorithmException( "Uh Oh, we found a null value here in matrix[ $one.label ][ $two.label ]" )
                    } else {
                        value = matrix[ one ][ two ]
                    }
                    values[ i ][ j ] = value
                }
            }
        } catch ( Exception e ) {
            throw new AlgorithmException( "OOPS: $e.message" )
        }

        EigenvalueDecomposition eigen = new Matrix( values ).eig()
        if ( eigen == null ) {
            throw new AlgorithmException( "Couldn't calculate eigen vectors for $values" )
        }
        return eigen.v
    }
    
    private Map<Variable, BigDecimal> calcWeights() {
        Matrix eigenVectors = calcEigenVectors()
        Map<Variable, BigDecimal> weights = [:]
        parents.eachWithIndex { Variable variable, int i ->
            weights[ variable ] = eigenVectors.get( i, 0 )
        }
        return weights
    }
    
    private void normalizeWeights( Map<Variable, BigDecimal> weights ) {
        
        weights.each {
            if ( it.value < 0 ) {
                throw new WeightLessThanZeroException( it.key )
            }
        }
        
        double sum = weights.values().sum() as Double
        if ( sum == 0 ) {
            throw new WeightSumToLessThanZeroException( weights )
            /*parents.each { Variable variable ->
                weights[ variable ] = 1 / parents.size()
            }*/
        } else {
            double scale = 1 / sum;
            weights.each { Map.Entry<Variable, BigDecimal> entry ->
                entry.value *= scale
            }
        }
    }
    
    public Map<Variable, BigDecimal> run() {

        loadPairwiseComparisons()
        Map<Variable, BigDecimal> weights = calcWeights()
        normalizeWeights( weights )

        print "  Weights: " + weights

        return weights

    }

    private List<PairwiseComparison> findPairwiseComparisons() {
        def found = CACHE.findPairwiseComparisons( user ).findAll { it.childId == child.id }

        print "Pairwise Comparisons:\n  ${found.join( "\n  " ) }"

        int expectedComparisons = ( parents.size() * parents.size() - parents.size() ) / 2
        if ( found.size() != expectedComparisons ) {
            throw new AlgorithmException( "Expected $expectedComparisons pairwise comparisons for the parents of $child.label (${parents*.label}), but received ${found.size()}" )
        }

        return found
    }

    public class WeightLessThanZeroException extends BadCptException {
        WeightLessThanZeroException(Variable weightedParent) {
            super( "Weight for $weightedParent.label (user $user.id, child variable $child.label) is < 0" )
        }
    }

    public class WeightSumToLessThanZeroException extends BadCptException {
        WeightSumToLessThanZeroException(Map<Variable, BigDecimal> weights) {
            super( "Weights ${weights.collect { "Weight($it.key) = $it.value" } } (user $user.id, child variable $child.label) sum to zero." )
        }
    }
    
    private static class Cache {

        private List<PairwiseComparison> allPairwiseComparisons = null
        
        public List<PairwiseComparison> getAllPairwiseComparisons() {
            if ( this.allPairwiseComparisons == null ) {
                this.allPairwiseComparisons = PairwiseComparison.list()
                println "Cached ${this.allPairwiseComparisons.size()} pairwise comparisons from DB."
            }
            return this.allPairwiseComparisons
        }

        private List<PairwiseComparison> findPairwiseComparisons( User createdBy ) {
            getAllPairwiseComparisons().findAll { it.createdBy.id == createdBy.id }
        }
        
    }

}
