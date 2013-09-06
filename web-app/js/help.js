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

bn.HelpInfo = klass( function( targetId, title, text, index ) {
	this.title  = title;
	this.text   = text;
	this.targetId = targetId;
	this.index  = index;
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

	queue : function( index, targetId, title, text ) {
		this.helpQueue[ index ] = new bn.HelpInfo( targetId, title, text, index );
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
				var result = $( "#" + helpInfo.targetId ).qtip({
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
						/*
						my       : 'left center',
						at       : 'right center',
						*/
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