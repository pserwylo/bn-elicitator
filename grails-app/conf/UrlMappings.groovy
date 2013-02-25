class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?" {
			constraints { }
		}

		"/"   ( controller:'explain' )
		"500" ( controller:'error', exception: Exception )
	}
}
