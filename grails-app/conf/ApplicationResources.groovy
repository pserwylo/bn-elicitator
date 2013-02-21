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

	/* Global and utility styles */

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

	/* Controller- and View-specific modules. */

	elicit {
		dependsOn( [ 'variableList' ] )
		resource url: 'css/elicit.css'
	}

	elicitProblems {
		dependsOn( [ 'elicit', 'floatingDialog' ] )
		resource url: 'css/elicitProblems.css'
	}

	elicitParents {
		dependsOn( [ 'elicit', 'floatingDialog' ] )
	}

	elicitParentsFirst {
		dependsOn( [ 'elicit', 'floatingDialog' ] )
		resource url: 'css/elicitParentsFirst.css'
	}

	elicitList {
		dependsOn( [ 'elicit' ] )
		resource url: 'css/elicitList.css'
	}

	admin {
		dependsOn( [ 'variableList' ] )
		resource url: 'css/admin.css'
	}

	adminUsers {
		dependsOn( [ 'admin', 'floatingDialog' ] )
		resource url: 'css/adminUsers.css'
	}
}