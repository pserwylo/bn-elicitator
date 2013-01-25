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

	var self         = this;
	this.domElement  = domElement;
	this.isAlive     = true;
	this.nextOverlay = null;
	this.isVisible   = true;

	$( this.domElement ).find( 'button.dismiss').click( function() {
		self.remove();
	});

	var detached = $( this.domElement ).detach();
	$( 'body' ).append( detached );

}).methods({

	hide: function( animate ) {
		$( this.domElement ).hide();
		this.isVisible = false;
	},

	show: function() {
		if ( this.isAlive ) {
			$( this.domElement ).show( 'fade', 'slow' );
			this.isVisible = true;
			this.position();
			this.scrollTo();
		}
	},

	scrollTo: function() {
		if ( this.isAlive ) {

			var overlayTop = $( this.domElement ).offset().top;
			var overlayHeight = $( this.domElement ).outerHeight();

			var viewportScroll = $( window ).scrollTop();
			var viewportHeight = $( window ).height();

			// If the item is too far below the screen viewport, we need to scroll down to it, until the bottom of it is visible...
			if ( overlayTop + overlayHeight > viewportScroll + viewportHeight || overlayTop < viewportScroll) {
				$( 'body' ).animate( { scrollTop: overlayTop } );
			}
		}
	},

	remove: function() {
		$( this.domElement ).hide( 'fade', 'slow', function() { $( this ).remove(); } );
		$.post( window.config.webroot + 'helpRead/read', { messageHash: this.getHash() } );
		this.isAlive = false;

		if ( this.nextOverlay != null ) {
			this.nextOverlay.show();
		}
	},

	getHash: function() {
		return $( this.domElement ).find( 'input:hidden[name=hash]' ).val();
	},

	getIndex: function() {
		return $( this.domElement ).find( 'input:hidden[name=index]' ).val();
	},

	setNext: function( next ) {
		this.nextOverlay = next;
		return this;
	},

	/**
	 * Position the overlay.
	 * Take into account whether it is to the left or to the right of the item it is attached to.
	 */
	position: function() {

		if ( !this.isAlive || !this.isVisible ) {
			return false;
		}

		var forValue = $( this.domElement ).find( 'input:hidden[name=for]').val();
		if ( typeof forValue === "undefined" ) {

			$( this.domElement )
				.css( 'display' , 'block' )
				.css( 'position', 'absolute' )
				.css( 'top'     , 50 )
				.css( 'left'    , $( window).width() / 2 - $( this.domElement ).width() / 2 );

		} else {

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
				.css( 'position', 'absolute' )
				.css( 'top'     , top )
				.css( 'left'    , left );

			// If we end up off the screen for some reason, then just centre the message on the screen.
			if ( $( this.domElement ).offset().left < 5 ) {

				$( this.domElement )
					.addClass( 'doesnt-fit' )
					.css     ( 'left', 5 );

			} else if ( $( this.domElement ).offset().left + $( this.domElement ).width() > $( window ).width() - 30 ) {

				$( this.domElement )
					.addClass( 'doesnt-fit' )
					.css     ( 'left', $( window ).width() - 30 - $( this.domElement ).width() );

			} else {

				$( this.domElement ).removeClass( 'doesnt-fit' );

			}

		}

		return true;
	}
});

(function() {

	var overlays = [];

	/**
	 * In response to a window resize, and also after an initial timeout (so that we position stuff after any
	 * hiding/showing due to toggle buttons is performed).
	 */
	var positionAll = function() {
		for ( var i = 0; i < overlays.length; i ++ ) {
			if ( overlays[ i ].position() ) {
				return;
			}
		}
	};

	/**
	 * Used for the array.sort method, to sort a list of HelpOverlay objects.
	 * @param a {HelpOverlay}
	 * @param b {HelpOverlay}
	 * @return {number}
	 */
	var compareOverlays = function( a, b ) {
		if ( a.getIndex() < b.getIndex() ) {
			return -1;
		} else if (a.getIndex() > b.getIndex() ) {
			return 1;
		} else {
			if ( typeof console !== "undefined" ) {
				console.log( "Error: Two help messages have the same index on this page." );
			}
			return 0;
		}
	};

	$( '.help-overlay' ).each( function() {
		overlays.push( new HelpOverlay( this ) );
	});

	$( window ).resize( positionAll );
	setTimeout(
		function() {
			positionAll();
			overlays[ 0 ].scrollTo();
		}, 10
	);

	overlays.sort( compareOverlays );

	for ( var i = 1; i < overlays.length; i ++ ) {
		overlays[ i - 1 ].setNext( overlays[ i ] );
		overlays[ i ].hide( false );
	}


})();