package bn.elicitator.troia

import bn.elicitator.Relationship
import bn.elicitator.Variable

class StructureJob extends DiscreteJob<DawidSkeneRelationship> {

	StructureJob( String troiaServerAddress, String jobId = null ) {
		super( troiaServerAddress, jobId )
	}

	@Override
	protected List<Assign> getAssigns() {

		def all = Relationship.findAllByIsExistsInitializedAndDelphiPhase( true, 1 )
		def relationships = all

		print "Loading ${relationships.size()} of ${all.size()} relationships into Troia..."

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
		Relationship relationship = objectToRelationship( data.objectName, data.categoryName as String )
		new DawidSkeneRelationship( relationship : relationship )
	}

	@Override
	protected Collection getCategories() {
		return [ "Yes", "No" ]
	}
}
