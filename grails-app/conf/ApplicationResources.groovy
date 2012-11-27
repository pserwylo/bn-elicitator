modules = {
    global {

		dependsOn( [ 'jquery', 'jquery-ui' ] )

		resource url: 'css/global.css'

	}

	variableList {

		resource url: 'css/variableList.css'

	}

	floatingDialog {

		resource url: 'css/floatingDialog.css'

	}

	/**
	 * Styles for the Elicit controller.
	 */
	elicit {

		dependsOn( [ 'variableList', 'floatingDialog' ] )

		resource url: 'css/elicit.css'

	}

	/**
	 * Styles for the Admin controller.
	 */
	admin {

		dependsOn( [] )

		resource url: 'css/admin.css'

	}
}