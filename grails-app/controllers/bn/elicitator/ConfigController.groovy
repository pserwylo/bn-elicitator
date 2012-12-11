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

import grails.converters.JSON;
import grails.util.Environment;
import grails.util.GrailsUtil;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

class ConfigController {

	OwlService owlService
	
    def index = {
		
	}
	
	/**
	 * Let the admin upload the ontology to be used.
	 * For testing, I'm just using one already on the server...
	 * @return
	 */
	def uploadOntology = {
		render "UPLOAD ONTOLOGY HERE"
	}
	
	/**
	 * Development environment helper function.
	 * Will initialize variables, constraints and constraint groups to reasonable defaults
	 * for the MEMMG ontology and the resource allocation BN. Then it will redirect to the
	 * config screens to show its handywork.
	 */
	def init = {
		
		// render ConstraintGroup.findAll()
		
		redirect( [ action: "selectVariables" ] )
		
	}
	
	/**
	 * Select which variables from the ontology are to be investigated when 
	 * building the BN.
	 * @return
	 */
	def selectVariables = {
		
		ArrayList<Variable> allVariables = owlService.getAllVariables()
		ArrayList<Variable> savedVariables = Variable.findAll()
		
		[ allVariables: allVariables, savedVariables: savedVariables ]
		
	}
	
	def saveConfig = {
		
	}
	
	def saveSelectedVariables = {
		
		ArrayList<Variable> allVars = owlService.getAllVariables()
		ArrayList<Variable> savedVars = Variable.findAll()
		ArrayList<String> varsToSave = params.variables
		
		// First, delete all variables which were previously selected but are 
		// not any more.
		for ( var in savedVars )
		{
			String found = varsToSave.find { s -> s == var.label }
			if ( found == null )
			{
				var.delete()
			}
		}
		
		// Now we save any new ones which are not already saved...
		for ( var in varsToSave )
		{
			// If we need to save, first find reference to it from the ontology
			// Keep in mind that it may be junk input and therefore may not
			// actually be a variable we know/care about...
			Variable toSave = allVars.find { v -> v.label == var }
			if ( toSave != null )
			{
				// Check if we actually need to save it (may already be saved)...
				Variable alreadySaved = savedVars.find { v -> v.label == var }
				if ( alreadySaved == null )
				{
					toSave.save()
				}
			}
		}

	}
	
}
