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


import bn.elicitator.init.DataLoader
import org.springframework.beans.factory.access.BootstrapException;

import javax.servlet.ServletContext;

class BnBootStrap {

	def grailsApplication

	def init = { ServletContext servletContext ->

		Class loaderClass = grailsApplication.config.bn?.dataLoaderClass
		if ( !loaderClass ) {
			throw new BootstrapException(
					"Couldn't find bn.dataLoaderClassName in config. Please specify a class that extends DataLoader." )
		}

		try {
			DataLoader loader = loaderClass.newInstance()
			loader.init( servletContext )
		} catch ( Exception e ) {
			throw new BootstrapException( "Error when loading data from ${loaderClass?.name}: $e.message.", e );
		}

	}

}
