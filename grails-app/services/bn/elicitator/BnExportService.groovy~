package bn.elicitator

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import org.semanticweb.owlapi.model.OWLOntology;

import norsys.netica.*

import javax.annotation.PreDestroy

/*import annas.graph.*
import annas.graph.drawing.**/

import edu.uci.ics.jung.graph.*
import edu.uci.ics.jung.visualization.*
import edu.uci.ics.jung.visualization.decorators.*
import edu.uci.ics.jung.visualization.renderers.*
import edu.uci.ics.jung.algorithms.layout.*
import org.apache.commons.collections15.*

class BnExportService {
	
	private static Environ environment

	def grailsApplication
	
	public BnExportService() {
		if ( environment == null )
		{
			if ( Environ.getDefaultEnviron() != null )
			{
				environment = Environ.getDefaultEnviron()
			}
			else
			{
				String neticaKey = grailsApplication.config.bn.elicitator.export.netica.key
				if ( neticaKey?.size() > 0 )
				{
					environment = new Environ( neticaKey );
				}
				else
				{
					throw new Exception( "Must specify 'bn.elicitator.export.netica.key' in the 'bn-elicitator.properties' file to export BNs." )
				}
			}
		}
	}
	
	Net createBn() 
	{
		
		ArrayList<Variable> variables = Variable.list()
		ArrayList<Relationship> relationships = Relationship.findAllByCreatedByAndDelphiPhase( ShiroUser.current, AppProperties.properties.delphiPhase )
		
		Net net = new Net( environment )
		net.name = "BayesianNetwork"
		HashMap<String, Node> nodes = []
		
		for ( Relationship relationship in relationships )
		{
			Node child = nodes.get( relationship.child.label )
			Node parent = nodes.get( relationship.parent.label )
			
			if ( child == null && relationship.child != null )
			{
				child = new Node( relationship.child.label, 1, net )
				nodes.put( relationship.child.label, child )
			}
			
			if ( parent == null && relationship.parent != null )
			{
				parent = new Node( relationship.parent.label, 1, net )
				nodes.put( relationship.parent.label, parent )
			}
			
			if ( child != null && parent != null )
			{
				child.addLink( parent )
			}
		}
		
		return net;

	}
	
	BufferedImage drawBnSnipped( Variable childVar )
	{
		
	}
	
	BufferedImage drawBn()
	{
		this.drawBn( this.createGraph() )
	}
	
	BufferedImage drawBn( Graph<Variable, Relationship> graph )
	{
		
		Dimension preferredSize = new Dimension( 300, 300 )
		
		Layout<Variable, Relationship> layout = new KKLayout<Variable, Relationship>( graph )
		layout.size = preferredSize
		BasicVisualizationServer<Variable, Relationship> server = new BasicVisualizationServer<Variable, Relationship>( layout )
		server.preferredSize = preferredSize
		server.setDoubleBuffered( false )
		
		Transformer<Relationship, Paint> edgeDrawPaintTransformer = new Transformer<Relationship, Paint>() {
			Paint transform( Relationship rel ) {
				float i = 1.0f;
				if ( (double)rel?.confidence != null )
				{
					i = 1.0f - (double)rel?.confidence / 100
				}
				Color color = new Color( i, i, i )
				return color
			}
		}
		
		float width = 120
		float height = 30
		Transformer<Variable, Shape> vertexShapeTransformer = new Transformer<Variable, Shape>() {
			Shape transform( Variable var ) {
				Ellipse2D.Float ellipse = new Ellipse2D.Float()
				ellipse.setFrame( -width / 2, -height / 2, width, height )
				return ellipse
			}
		}
		
		Transformer<Variable, Paint> vertexFillPaintTransformer = new Transformer<Variable, Paint>() {
			Paint transform( Variable var ) {
				Color.WHITE
			}
		}
		
		server.renderContext.edgeDrawPaintTransformer = edgeDrawPaintTransformer
		server.renderContext.arrowDrawPaintTransformer = edgeDrawPaintTransformer
		server.renderContext.arrowFillPaintTransformer = edgeDrawPaintTransformer
		server.renderContext.vertexLabelTransformer =  new ToStringLabeller()
		server.renderContext.vertexShapeTransformer =  vertexShapeTransformer
		server.renderContext.vertexFillPaintTransformer =  vertexFillPaintTransformer
		server.renderer.vertexLabelRenderer.position = Renderer.VertexLabel.Position.CNTR
		server.background = Color.WHITE
		
		BufferedImage image = new BufferedImage( (int)preferredSize.width, (int)preferredSize.height, BufferedImage.TYPE_INT_ARGB )
		Graphics2D g = image.createGraphics()
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON )
		
		server.paintComponent( g )
		
		return image
		
		return null
		
	}
	
	Graph<Variable, Relationship> createGraph()
	{
		this.createGraph( Relationship.findAllByCreatedBy( ShiroUser.current, AppProperties.properties.delphiPhase ) )
	}
	
	Graph<Variable, Relationship> createGraph( ArrayList<Relationship> relationships )
	{
		Graph<Variable, Relationship> graph = new DirectedSparseGraph<Variable, Relationship>()
		
		for ( Relationship relationship in relationships )
		{
			if ( !graph.containsVertex( relationship.child ) )
			{
				graph.addVertex( relationship.child )
			}
			
			if ( !graph.containsVertex( relationship.parent ) )
			{
				graph.addVertex( relationship.parent )
			}
			
			graph.addEdge( relationship, [ relationship.parent, relationship.child ] )
		}
		
		return graph
		
	}
	
	
	/*BufferedImage drawBnAnnas() 
	{
		def graph = this.createGraphAnnas()
		
		BufferedImage image = new BufferedImage( 300, 400, BufferedImage.TYPE_INT_ARGB )
		// Graphics2D g = image.createGraphics()
		// g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON )
		// g.setStroke( new BasicStroke( 2 ) )
		
		new GraphDrawer<Variable, DefaultArc<String>>( 
			graph, 
			new DefaultDrawableNode<Variable>(),
			new DefaultDrawableArc<DefaultArc<String>>(),
			new LinePlacer<Variable, DefaultArc<String>>() 
		).draw( image )
		
		return image
	}
	
	DirectedGraph<Variable, DefaultArc<String>> createGraphAnnas()
	{
		ArrayList<Variable> variables = Variable.list()
		ArrayList<Relationship> relationships = Relationship.findAllByCreatedBy( ShiroUser.current )
		
		HashMap<String, Variable> nodes = []
		
		DirectedGraph<Variable, DefaultArc<String>> graph = new DirectedGraph<String, DefaultArc<String>>()
		
		for ( Relationship relationship in relationships )
		{			
			if ( !graph.contains( relationship.child ) )
			{
				graph.addNode( relationship.child )
			}
			
			if ( !graph.contains( relationship.parent ) ) 
			{
				graph.addNode( relationship.parent )
			}
			
			graph.addArc( relationship.child, relationship.parent, new DefaultWeight( (double)relationship.confidence / 100 ) )
		}
		
		return graph

	}*/
	
	String serialize( Net network )
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Streamer streamer = new Streamer( output, "bayesian-network.dne", network.getEnviron() )
		network.write( streamer )
		return output.toString()
	}
	
}
