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

if ( typeof bn === 'undefined' ) {
	bn = {};
}

bn.HelpInfo = klass( function( uniqueId, targetId, title, text, index ) {

	this.title    = title;
	this.text     = text;
	this.targetId = targetId;
	this.uniqueId = uniqueId;
	this.index    = index;

}).methods({

	getId : function() {
		return "tooltip-" + this.index;
	},

	getDom : function() {
		return $( "#qtip-" + this.getId() );
	}

});

bn.HelpClass = klass( function() {

	var self = this;

	/** @var bn.HelpInfo[] */
	this.helpQueue = [];
	this.current = -1;
	$( document).ready( function() {
		setTimeout( function() {
			self.next();
		}, 20 );
	})

}).methods({

	getCurrent : function() {
		if ( this.current < this.helpQueue.length && typeof this.helpQueue[ this.current ] !== "undefined" ) {
			return this.helpQueue[ this.current ];
		} else {
			return null;
		}
	},

	queue : function( index, uniqueId, targetId, title, text ) {
		bn.log( "Queueing help message for '" + targetId + "'." );
		this.helpQueue[ index ] = new bn.HelpInfo( uniqueId, targetId, title, text, index );
	},

	hideCurrent : function() {
		var info = this.getCurrent();
		if ( info != null ) {
			info.getDom().qtip( "destroy" );
		}
	},

	show : function() {

		var self = this;
		var helpInfo = this.getCurrent();
		if ( helpInfo != null ) {

			$(document).ready( function() {

				var target = $( "#" + helpInfo.targetId );

				if ( target.length == 0 ) {

					bn.log( "Couldn't show help for '" + helpInfo.targetId + "' - element not found." );

				} else {

					bn.log( "Showing help for '" + helpInfo.targetId + "'." );

					target.qtip({
						id       : helpInfo.getId(),
						suppress : false,
						show     : { ready : true },
						hide     : false,
						content  : {
							text  : helpInfo.text,
							title : helpInfo.title,
							button : true
						},
						style : {
							classes : 'qtip-green qtip-rounded qtip-shadow'
						},
						position : {
							 my       : 'top center',
							 at       : 'bottom center',
							viewport : $( window ),
							adjust   : {
								method : 'flipinvert'
							}
						},
						events : {
							render : function() {
								helpInfo.getDom().find( '.qtip-close' ).click( function() {
									self.next();
								})
							}
						}
					});

					$.post( bn.config.webroot + 'helpRead/read', { uniqueId : helpInfo.uniqueId } );
				}
			});
		}
	},

	next : function() {
		this.hideCurrent();
		this.current ++;
		while ( this.current < this.helpQueue.length && typeof this.helpQueue[ this.current ] === "undefined" ) {
			this.current ++;
		}
		this.show();
	}

});

bn.help = new bn.HelpClass();