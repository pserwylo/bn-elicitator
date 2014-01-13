package bn.elicitator.graphics

import org.apache.commons.io.IOUtils

class GraphvizPngImage extends GraphvizImage {

	GraphvizPngImage(String dot) {
		super(dot)
	}

	@Override
	protected String getType() {
		"png"
	}

	@Override
	protected String getMimeType() {
		"image/png"
	}

	@Override
	protected String getExtension() {
		"png"
	}
}

class GraphvizSvgImage extends GraphvizImage {

	GraphvizSvgImage(String dot) {
		super(dot)
	}

	@Override
	protected String getType() {
		"svg"
	}

	@Override
	protected String getMimeType() {
		"image/svg+xml"
	}

	@Override
	protected String getExtension() {
		"svg"
	}
}

class GraphvizDotImage extends GraphvizImage {

	GraphvizDotImage(String dot) {
		super(dot)
	}

	@Override
	protected String getType() {
		""
	}

	@Override
	protected String getMimeType() {
		"text/plain"
	}

	@Override
	protected String getExtension() {
		"dot"
	}
}

abstract class GraphvizImage {

	protected final String dot

	public GraphvizImage(String dot) {
		this.dot = dot
	}

	abstract protected String getType()

	abstract protected String getMimeType()

	abstract protected String getExtension()

	public String generate() {

		String output = ""
		File file = null
		try {

			file = File.createTempFile( "bn-dot-", ".dot" )
			file.write( dot );

			Process dotProcess = new ProcessBuilder().command( "dot", cliArgs(), file.absolutePath ).start()
			BufferedInputStream input = new BufferedInputStream( dotProcess.inputStream )
			dotProcess.waitFor()

			output = IOUtils.toString( input )

		} catch ( Exception ioe ) {
			println ioe
		} finally {
			file?.delete()
		}
		return output
	}

	private String cliArgs() {
		List<String> args = []
		if (getType()?.size() > 0) {
			args.add( "-T${getType()}" )
		}
		return args.join(" ")
	}

}
