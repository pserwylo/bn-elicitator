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

var HelpOverlay = klass( function( domElement ) {

	this.domElement = domElement;

	// Dismiss the overlay
	// When they click the button, delete the item from the page entirely (why the hell not?)
	$( this.domElement ).find( 'button.dismiss').click( function() {
		$( domElement ).remove();
	});

	this.position();

}).methods({

	/**
	 * Position the overlay.
	 * Take into account whether it is to the left or to the right of the item it is attached to.
	 */
	position: function() {

		var forValue = $( this.domElement ).find( 'input:hidden[name=for]').val();
		if ( typeof forValue !== "undefined" ) {

			var forItem = $( '#' + forValue );
			var top = forItem.offset().top - 20;

			var locationValue = $( this.domElement ).find( 'input:hidden[name=location]' ).val();
			var location = ( typeof locationValue !== "undefined" ) ? locationValue : "right";

			var left = 0;
			if ( location == "left" ) {
				left = forItem.offset().left - $( this.domElement ).outerWidth() - 20;
			} else {
				left = forItem.offset().left + forItem.outerWidth() + 20;
			}

			$( this.domElement )
				.css( 'display' , 'block' )
				.css( 'position', 'absolute' )
				.css( 'top'     , top )
				.css( 'left' , left )
		}
	}

});

(function() {

	$( '.help-overlay' ).each( function() {

		var overlay = this;




	})

})();