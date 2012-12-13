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

package bn.elicitator

/**
 * Keeps track of each time a user is emailed.
 * We keep a reference to the template which was used so that we can filter based on this, but we also store the
 * {@link EmailLog#subject} and {@link EmailLog#body} because they will be customized (with placeholders) for specific
 * users before they are sent. This way, we can see exactly what was sent (not what was supposed to be sent). This may
 * help with debugging placeholder problems (e.g. not getting replaced properly).
 *
 * I'm aware that this would grow quite large in a heavily used system, but I don't think it is an issue for a BN
 * elicitation task. Even if there were 1000 participants and we emailed each 100 times, we should probably be fine in
 * most environments.
 */
class EmailLog {

	static mapping = {
		body( type: "text" )
	}

	ShiroUser recipient

	String subject

	String body

	EmailTemplate template

}
