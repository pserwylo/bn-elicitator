package bn.elicitator.output

class HtmlMatrixOutputGraph extends MatrixOutputGraph {

	private static Integer MAX_LEVEL = 10
	private static String HEX_CHARS = "0123456789abcdef"

	public Boolean includeHeaders = false

	public Integer cellSize = 0;

	private Integer calcLevel( Float strength ) {
		return Math.round( strength * MAX_LEVEL )
	}

	private String generateStyles() {

		List<Integer> levels = []
		for ( Integer i in 0..MAX_LEVEL ) {
			levels.add( i )
		}
		"""
		<style>

			table.matrix {
				border-collapse: collapse;
				border-spacing: 0;
				border: solid 1px #888;
			}

			${ cellSize > 0 ? """
				table.matrix td {
					width:    ${cellSize-1}px;
					height:   ${cellSize}px;
				}
				""" : ''}

			${levels.collect { generateLevelStyle( it ) }.join( "" )}
		</style>
		"""
	}

	private String generateLevelStyle( Integer level ) {

		Integer index = HEX_CHARS.size() - 1 - (float)level / MAX_LEVEL * ( HEX_CHARS.size() - 1 )
		String colour = HEX_CHARS[ index ] * 3

		"""
		.level-$level {
			background: #$colour;
			${level > MAX_LEVEL / 2 ? 'color: white;' : ''}
		}
		"""
	}

	@Override
	String generateGraph() {

		String output = """
			${generateStyles()}
			<table class='matrix'>
			"""

		if ( includeHeaders ) {
			"""
			<tr>
				<th></th><th><div class='header'>${allVariables*.readableLabel.join("</div></th><th>")}</div></th>
			</tr>
			"""
		}

		allVariables.each { parent ->
			output += """
				<tr>
					${ includeHeaders ? "<th><a title='$parent.readableLabel'>$parent.readableLabel</a></div></th>" : '' }
					${allVariables.collect { child ->
						Float strength = getCount( parent, child )
						Integer level = calcLevel( strength )
						"<td class='level-$level'><a title='$strength'></a></td>"
					}.join("")}
				</tr>
				"""
		}

		output += """
			</table>
			"""

		return output

	}

	@Override
	String getContentType() {
		return "text/html"
	}

	@Override
	String getFileExtension() {
		return ".html"
	}
}
