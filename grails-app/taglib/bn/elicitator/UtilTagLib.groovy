package bn.elicitator

class UtilTagLib {

	def backToTop = { attrs, body ->
		out << "<span class='back-to-top'><a href='#top'>(back to top)</a></span>"
	}

	def top = { attrs, body ->
		out << "<a name='top'></a>	"
	}

}
