package bn.elicitator.init.loaders
/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2013 Peter Serwylo (peter.serwylo@monash.edu)
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

class DevelopmentChan2010DataLoader extends Chan2010DataLoader {

	protected void initOther() {
		updateHomePage()
		initTestUsers( 50, false )
	}


}
