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
