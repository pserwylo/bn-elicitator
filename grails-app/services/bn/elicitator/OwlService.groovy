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

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

class OwlService {

	private OWLOntology ontology
	
	/**
	 * Lazily loads the ontology from the filesystem as required.
	 * @return
	 */
    private OWLOntology getOntology() {

		if ( ontology == null )
		{
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager()
			ontology = manager.loadOntologyFromOntologyDocument( new File( ServletContextHolder.servletContext.getRealPath( 'WEB-INF/resources/ontology.owl' ) ) );
		}
		return ontology
		
    }
	
	/*public ArrayList<Variable>*/ def getAllVariables() {
		
		ArrayList<Variable> variableList = new ArrayList<Variable>();
		
		OWLOntology onto = this.getOntology()
		
		OWLAnnotationProperty commentAnnotation = onto.OWLOntologyManager.OWLDataFactory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) )
		OWLAnnotationProperty labelAnnotation = onto.OWLOntologyManager.OWLDataFactory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#label" ) )
		
		Set<OWLClass> classes = onto.classesInSignature
		
		for ( OWLEntity c in classes )
		{
			String label = c.iri.remainder

			Set<OWLAnnotation> labelAnnotations = c.getAnnotations( onto, labelAnnotation )
			ArrayList<String> synonyms = []
			int suffixSize = "^^xsd:string".length();
			for ( OWLAnnotation a in labelAnnotations )
			{
				String synonym = a.value
				if ( synonym.length() > suffixSize )
				{
					String suffix = synonym.substring( synonym.length() - suffixSize )
					if ( suffix == "^^xsd:string" )
					{
						String prefix = synonym.substring( 0, synonym.length() - suffixSize )
						prefix = prefix.substring( 1, prefix.length() - 1 ) // Remove quotes from start and end...
						synonyms.add( prefix )
					} 
				}
			}
			String readableLabel = synonyms.size() > 0 ? synonyms.asList()[ 0 ] : label
			
			String description = ""
			Set<OWLAnnotation> commentAnnotations = c.getAnnotations( onto, commentAnnotation )
			if ( commentAnnotations.size() > 0 )
			{
				OWLAnnotation comment = commentAnnotations.asList()[ 0 ]
				description = comment.value

				if ( description.length() > suffixSize )
				{
					String suffix = description.substring( description.length() - suffixSize )
					if ( suffix == "^^xsd:string" )
					{
						description = description.substring( 0, description.length() - suffixSize )
						description = description.substring( 1, description.length() - 1 ) // Remove quotes from start and end...
					}
				}

			}
			
			variableList.add( new Variable( label: label, description: description, synonyms: synonyms, readableLabel: readableLabel ) );
		}
		
		return variableList
		
	}
}
