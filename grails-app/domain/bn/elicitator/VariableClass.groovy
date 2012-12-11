/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
