/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2015 Peter Serwylo (peter.serwylo@monash.edu)
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

		if ( !grailsApplication.config.containsKey( 'bn' ) ) {
			throw new BootstrapException(
					"Couldn't find bn-elicitator config. Looking in: ${grailsApplication.config.grails.config.locations}" )
		}
		
		File loaderFile = new File( grailsApplication.config.bn?.dataLoaderClass as String )
		if ( !loaderFile.exists() ) {
			throw new BootstrapException(
					"Could not find data loader at \"$loaderFile.absolutePath\"." )
		}
		
		Class loaderClass = new GroovyClassLoader().parseClass( loaderFile )
		if ( !loaderClass ) {
			throw new BootstrapException(
					"Couldn't find bn.dataLoaderClassName in config. Please specify a class that extends DataLoader." )
		}

		try {
			def loader = loaderClass.newInstance()
			if ( ! ( loader instanceof DataLoader ) ) {
				throw new BootstrapException( 
						"Class defined in \"$loaderFile.absolutePath\" does not extend bn.elicitator.utils.DataLoader." )
			}
			
			( (DataLoader) loader ).init( servletContext )
		} catch ( Exception e ) {
			throw new BootstrapException( 
					"Error when loading data from ${loaderClass?.name}: $e.message.", e );
		}

	}

}
