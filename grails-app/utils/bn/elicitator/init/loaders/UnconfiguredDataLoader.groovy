package bn.elicitator.init.loaders
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
import bn.elicitator.AppProperties
import bn.elicitator.init.DataLoader

import javax.servlet.ServletContext

class UnconfiguredDataLoader extends DataLoader {

	protected String getHomePageContent() {
		"""
			<h2>Why am I seeing this?</h2>
			<p>
				If you are seeing this, it is because you have not finished configuring the software. Usually, the 
				introductory message you want to display to your participants would be displayed here, so you will 
				want to set that up.
			</p>

			<h2>Where to from here?</h2>
			<p>
				Check out the <a href="https://github.com/pserwylo/bn-elicitator/blob/master/README.md">README</a> for details on
				how to configure a <code>DataLoader</code>. This is the part of the software responsible for specifying
				the variables to use in elicitation, as well as the content of this and various other pages throughout 
				the system.
			</p>
		"""
	}
	
	protected AppProperties getProperties( ServletContext servletContext )
	{
		String explanatoryStatement = """
			<h2>Why am I seeing this?</h2>
			<p>
				This is where you would usually see a consent form for the participants to read through, 
				check a box to say they agree, and then proceed.
			</p>
			<p>
				To configure it, you need to set the <pre>explanatoryStatement</pre> property in the
				<pre>getProperties()</pre> method of your <pre>DataLoader</pre>.
			</p>
"""

		new AppProperties(
			adminEmail          : "your.email@example.com",
			url                 : "http://localhost:8080/bn-elicitator",
			title               : "Bayesian Network Elicitator (requires configuring to complete installation)",
			delphiPhase         : 1,
			elicitationPhase    : AppProperties.ELICIT_2_RELATIONSHIPS,
			explanatoryStatement: explanatoryStatement
		)
	}

}
