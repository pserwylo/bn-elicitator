package bn.elicitator.troia

import bn.elicitator.Relationship
import bn.elicitator.Variable

/**
 * This class is designed to investigate what happens as the number of responses from
 * participants is randomly reduced. The aim is to see how quickly the results degrade
 * with less information. Hopefully this will provide feedback about how the process
 * would improve had more data been collected.
 */
class DegradedStructureJob extends StructureJob {

	private double removalPercent = 0;

	DegradedStructureJob(String troiaServerAddress, String jobId) {
		super(troiaServerAddress, jobId)
	}

	DegradedStructureJob(String troiaServerAddress) {
		super(troiaServerAddress)
	}

	public void setRemovalPercent(double percent) {
		this.removalPercent = percent;
	}

	protected Collection<Relationship> getRelationshipsToAnalyse() {
		def relationships = Relationship.findAllByIsExistsInitializedAndDelphiPhase( true, 1 )
		def targetSize = relationships.size() - ( relationships.size() * ( removalPercent / 100 ) )
		while ( relationships.size() > targetSize ) {
			relationships.remove( (int)( Math.random() * ( relationships.size() - 1 ) ) )
		}
		return relationships
	}

}

class StructureJob extends DiscreteJob<DawidSkeneRelationship> {

    /**
     * The prior probability that any particular arc should be included in the BN.
     */
    private double arcPrior = -1

	StructureJob( String troiaServerAddress, String jobId = null ) {
		super( troiaServerAddress, jobId )
	}

    public void setArcPrior(double value) {
        if ( value < 0 || value > 1 ) {
            throw new Exception( "Arc prior must be between 0 and 1" )
        }
        this.arcPrior = value
    }

	protected Collection<Relationship> getRelationshipsToAnalyse() {
		Relationship.findAllByIsExistsInitializedAndDelphiPhase( true, 1 )
	}

    public double getArcPrior() {
        this.arcPrior
    }

    protected Map<String, Double> getCategoryPriors() {
         arcPrior >= 0 ? [ "Yes" : arcPrior, "No"  : 1 - arcPrior ] : super.categoryPriors
    }

	@Override
	protected List<Assign> getAssigns() {
		def relationships = relationshipsToAnalyse
		relationships.collect { relationship ->
			new Assign(
				worker : relationship.createdBy.id,
				object : relationshipToObject( relationship ),
				label  : relationship.exists ? "Yes" : "No"
			)
		}

	}

	private String relationshipToObject( Relationship relationship ) {
		"$relationship.parent.label->$relationship.child.label"
	}

	private Relationship objectToRelationship( String relationship, String categoryName ) {

		Relationship arc = new Relationship()

		String[] parts = relationship.split( /->/ )
		String parentLabel = parts[ 0 ]
		String childLabel  = parts[ 1 ]

		arc.parent = Variable.findByLabel( parentLabel )
		arc.child  = Variable.findByLabel( childLabel  )
		arc.exists = categoryName == "Yes"

		return arc

	}

	// objectName, categoryName
	@Override
	protected DawidSkeneRelationship predictionFromData(Object data) {
		Relationship relationship = objectToRelationship( data.objectName as String, data.categoryName as String )
		new DawidSkeneRelationship( relationship : relationship )
	}

	@Override
	protected Collection getCategories() {
		return [ "Yes", "No" ]
	}
}
