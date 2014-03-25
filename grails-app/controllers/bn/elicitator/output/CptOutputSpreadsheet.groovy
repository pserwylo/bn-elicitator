package bn.elicitator.output

import bn.elicitator.Probability
import bn.elicitator.State

class CptOutputSpreadsheet {

	@Override
	String toString() {

		StringBuilder sb = new StringBuilder( headerRow() )
		for ( Probability p : Probability.list() ) {
			sb.append( toSpreadsheetRow( p ) )
		}
		sb.toString()

	}

	private String headerRow() {

		listToSpreadsheetRow([

			"Created By (username)",
			"Child variable (label)",
			"Child state (label)",
			"Parent variables (label)",
			"Parent states (label)",

			"Probability",

			"Created By (id)",
			"Child variable (id)",
			"Child state (id)",
			"Parent variables (id)",
			"Parent states (id)",

			"Probability",

			"Created By (id)",
			"ChildStateId|ParentStateIds",

			"Probability",
		])

	}

	private String toSpreadsheetRow( Probability probability ) {

		List<State> parentStates = probability.parentStates.sort { a, b -> a.id <=> b.id }

		listToSpreadsheetRow([

			probability.createdBy.username,
			probability.childState.variable.label,
			probability.childState.label,
			parentStates*.variable*.label.join( ',' ),
			parentStates*.label.join( ',' ),

			probability.probability,

			probability.createdBy.id,
			probability.childState.variable.id,
			probability.childState.id,
			parentStates*.variable*.id.join( ',' ),
			parentStates*.id.join( ',' ),

			probability.probability,

			probability.createdBy.id,
			probability.childState.id + "|" + parentStates*.id.join( ',' ),
			probability.probability,

		])

	}

	private String listToSpreadsheetRow( List list ) {
		list.join( '\t' ) + '\n'
	}

}