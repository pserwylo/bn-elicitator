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
 * Both the subject and body of an email are templatable.
 * Each template has its own placeholders which may be unique to that template, but also has global placeholders which
 * are available to all emails (such as the recipients name and the link to unsubscribe).
 *
 * It is up to the {@link EmailService} to work closely with this to make sure that the relevant placeholders are
 * available at time of sending.
 *
 * @see EmailService
 */
class EmailTemplate {

	static constraints = {
	}

	static mapping = {
		description( type: "text" )
		body( type: "text" )
	}

	/**
	 * An identifier which will be used to reference this email (e.g. in javascript).
	 */
	String name

	/**
	 * Describes the point of this email, so that even if we delete the default subject and body, we know when and
	 * why the email will be sent.
	 */
	String description

	String subject

	String body

	/**
	 * Placeholders specific to this template. This template will also have {@link EmailTemplate#GLOBAL_PLACEHOLDER_NAMES}
	 * available.
	 */
	List<String> placeholderNames = []

	static final FIRST_PHASE_STARTING = "firstPhaseStarting";
	static final PHASE_COMPLETE       = "phaseComplete";
	static final STUDY_COMPLETE       = "studyComplete";
	static final ERROR                = "error";

	static EmailTemplate getFirstPhaseStarting() { return EmailTemplate.findByName( FIRST_PHASE_STARTING ) }
	static EmailTemplate getPhaseComplete()      { return EmailTemplate.findByName( PHASE_COMPLETE       ) }
	static EmailTemplate getStudyComplete()      { return EmailTemplate.findByName( STUDY_COMPLETE       ) }
	static EmailTemplate getErrorEmail()         { return EmailTemplate.findByName( ERROR                ) }

	/**
	 * @see EmailTemplate#GLOBAL_PLACEHOLDER_NAMES
	 */
	static final PH_USER = "[User]"

	/**
	 * @see EmailTemplate#GLOBAL_PLACEHOLDER_NAMES
	 */
	static final PH_LINK = "[Link]"

	/**
	 * @see EmailTemplate#GLOBAL_PLACEHOLDER_NAMES
	 */
	static final PH_UNSUBSCRIBE_LINK = "[UnsubscribeLink]"

	/**
	 * @see EmailTemplate#getFirstPhaseStarting()
	 */
	static final PH_START_DATE = "[StartDate]"

	/**
	 * @see EmailTemplate#getPhaseComplete()
	 */
	static final PH_COMPLETED_PHASE = "[CompletedPhase]"

	/**
	 * @see EmailTemplate#getPhaseComplete()
	 */
	static final PH_NEXT_PHASE = "[NextPhase]"

	/**
	 * @see EmailTemplate#getPhaseComplete()
	 */
	static final PH_EXPECTED_PHASE_DURATION = "[ExpectedPhaseDuration]"

	/**
	 * @see EmailTemplate#getErrorEmail()
	 */
	static final PH_EXCEPTION_STACK_TRACE = "[ExceptionStackTrace]"

	/**
	 * @see EmailTemplate#getErrorEmail()
	 */
	static final PH_EXCEPTION_TYPE = "[ExceptionType]"

	/**
	 * @see EmailTemplate#getErrorEmail()
	 */
	static final PH_EXCEPTION_MESSAGE = "[ExceptionMessage]"

	/**
	 * @see EmailTemplate#getErrorEmail()
	 */
	static final PH_ERROR_MESSAGE = "[ErrorMessage]"

	/**
	 * @see EmailTemplate#getErrorEmail()
	 */
	static final PH_ERROR_USER = "[ErrorUser]"

	static final List<String> GLOBAL_PLACEHOLDER_NAMES = [ PH_USER, PH_LINK, PH_UNSUBSCRIBE_LINK ]


}
