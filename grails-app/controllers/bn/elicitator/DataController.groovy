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

import grails.converters.JSON
import java.awt.image.*
import java.awt.*
import javax.imageio.*

import annas.graph.* 
import annas.graph.drawing.*

import java.util.ArrayList;

/**
 * Used to bunch up a suit of utility functions for ajax requests.
 * @author Peter Serwylo (peter.serwylo@monash.edu)
 *
 */
class DataController {

	def owlService

	def bnService
	
    def index() { }
	
	def ontologyTest = {
		
		render "<p>"
		render this.owlService.allVariables*.synonyms.join( "</p><p>" )
		render "</p>"
		
	}
	
	def getVariablesFromOntology = {
		
		String searchString = params['term'] ? params['term'] : ''
		
		ArrayList<Variable> variables = owlService.getAllVariables()
		ArrayList<String> interestingVariableLabels = variables.findAll { var ->
			var.label.toLowerCase().contains( searchString?.toLowerCase() )
		}.label
		
		render interestingVariableLabels as JSON
	
	}
	
	def displaySnippet = { 
		
		String label = params['for']
		if ( label != null )
		{
			Variable var = Variable.findByLabel( label )
			if ( var != null )
			{
				def graph = this.bnService.createGraph( Relationship.findAllByCreatedByAndChildAndDelphiPhase( ShiroUser.current, var, AppProperties.properties.delphiPhase ) )
				def image = this.bnService.drawBn( graph )
				ImageIO.write( image, "png", response.outputStream )
				return
			}
		}
		
	}
	
	def displayResults = {
		
		ImageIO.write( this.bnService.drawBn(), "png", response.outputStream )
		
	}
	
}
