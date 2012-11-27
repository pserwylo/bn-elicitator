package bn.elicitator

/**
 * We need to make sure that the expert has at least looked at every variable,
 * so that we can tell at the end if there are any which were NOT visited and
 * need their attention.
 * @author Peter Serwylo (peter.serwylo@monash.edu)
 *
 */
class VisitedVariable {

	ShiroUser visitedBy

	Variable variable

	Integer delphiPhase

}
