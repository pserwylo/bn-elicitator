class UrlMappings {

	static mappings = {

		"/content/admin/$action?/$id?" ( controller: "contentEdit" )
		"/content/$page?"              ( controller: "contentView")

		"500"                          ( controller:'error', exception: Exception )
		"/admin/manage/$action?"       ( controller: "adminManage" )
		"/auth/oauth/$action?"         ( controller: "springSecurityOAuth" )

		"/$controller/$action?/$id?"   ( )
		"/"                            ( controller:'home' )


	}
}
