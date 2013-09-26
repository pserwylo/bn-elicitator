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

	"jquery-ui" {
		resource url: 'css/lib/jquery-ui/smoothness/jquery-ui.min.css'
		resource url: 'js/lib/jquery-ui/jquery-ui.min.js'

		resource url: 'css/lib/qtip/jquery.qtip.min.css'
		resource url: 'js/lib/qtip/jquery.qtip.min.js'
	}

    global {
		dependsOn( [ 'jquery', 'jquery-ui' ] )
		resource url: 'css/global.css'
		resource url: 'css/help.css'
		resource url: 'js/lib/klass/klass.min.js'
		resource url: 'js/util.js'
		resource url: 'js/help.js'
	}

	feedback {
		resource url: 'css/feedback.css'
		dependsOn( 'global' )
	}

	variableList {
		resource url: 'css/variableList.css'
	}

	floatingDialog {
		resource url: 'css/floatingDialog.css'
	}

	/* Controller- and View-specific modules. */

	contentView {
		resource url: 'css/contentView.css'
	}

	contentEdit {
		resource url: 'css/contentEdit.css'

	}

	auth {
		resource url: 'css/auth.css'
	}

	explain {
		dependsOn( [ 'global' ] )
		resource url: 'css/explain.css'
	}

	elicit {
		dependsOn( [ 'variableList' ] )
		resource url: 'css/elicit.css'
	}

	elicitProblems {
		dependsOn( [ 'elicit', 'floatingDialog' ] )
		resource url: 'css/elicitProblems.css'
	}

	elicitChildren {
		dependsOn( [ 'elicit', 'floatingDialog' ] )
		resource url: 'css/elicitParents.css'
	}

	elicitChildrenFirst {
		dependsOn( [ 'elicitChildren' ] )
		resource url: 'css/elicitChildrenFirst.css'
	}

	elicitProbabilities {
		dependsOn( [ 'elicit', 'global' ] )
		resource url: 'css/elicitProbabilities.css'
		resource url: 'js/das2004.js'
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
