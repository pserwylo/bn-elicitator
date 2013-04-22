class UrlMappings {

	static mappings = {

		"/$controller/$action?/$id?" {
			constraints { }
		}

		"/content/admin/$action?/$id?"(controller: "contentEdit")
		"/content/$page?"(controller: "contentView")

		"/"   ( controller:'explain' )
		"500" ( controller:'error', exception: Exception )
		"/admin/manage/$action?" ( controller: "adminManage" )
	}
}
