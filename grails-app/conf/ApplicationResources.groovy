/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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