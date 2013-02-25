class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?" {
			constraints { }
		}

		"/"   ( controller:'explain' )
		"500" ( controller:'error', exception: Exception )
		"400" ( controller:'error', action: "invalidInput" )
		"404" ( controller:'error', action: "notFound" )
	}
}
