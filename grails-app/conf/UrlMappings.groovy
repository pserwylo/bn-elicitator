class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?" {
			constraints { }
		}

		"/"   ( controller:'explain' )
		"400" ( controller:'error', action: 'invalidInput' )
		"404" ( controller:'error', action: 'notFound'     )
		"500" ( controller:'error', action: 'serverError'  )
	}
}
