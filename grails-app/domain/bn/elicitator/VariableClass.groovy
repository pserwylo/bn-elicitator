package bn.elicitator

/**
 * Variable classes have been defined by Kjærulff, U. B. & Madsen, A. L. (Chapter 6, page 150).
 */
class VariableClass {

	static hasMany = [
		potentialParents: VariableClass
	]


	String name;

	List<VariableClass> potentialParents = []

	String toString()
	{
		return name
	}

	/**
	 * Nicely capitalized version of the name.
	 * http://stackoverflow.com/questions/681807/groovy-gdk-equivalent-of-apache-commons-stringutils-capitalizestr-or-perls-uc
	 * @return First character of name is upper case, rest is as is.
	 */
	String getNiceName()
	{
		return name[0].toUpperCase() + name[1..-1];
	}

	static final BACKGROUND = "background";
	static final MEDIATING = "mediating";
	static final PROBLEM = "problem";
	static final SYMPTOM = "symptom";

	static VariableClass getBackground() { return VariableClass.findByName( BACKGROUND ) }
	static VariableClass getMediating() { return VariableClass.findByName( MEDIATING ) }
	static VariableClass getProblem() { return VariableClass.findByName( PROBLEM ) }
	static VariableClass getSymptom() { return VariableClass.findByName( SYMPTOM ) }

}
