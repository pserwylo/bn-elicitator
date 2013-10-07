if ( typeof bn === 'undefined' ) {
    bn = {};
}

bn.utils = {

    equalizeHeights : function( items ) {

        items.each( function( i, item ) {
            $( item ).css( 'height', '' );
        });

        var maxHeight = 0;
        items.each( function( i, item ) {
            if ( $( item ).height() > maxHeight ) {
                maxHeight = $( item ).height();
            }
        }).each( function( i, item ) {
            $( item ).height( maxHeight );
        });

    },

    ensureTopIsInView : function( item ) {
        var itemTop        = item.offset().top;
        var itemHeight     = item.outerHeight();
        var viewportScroll = $( window ).scrollTop();
        var viewportHeight = $( window ).height();

        // If the item is too far below the screen viewport, we need to scroll down to it, until the bottom of it is visible...
        if ( itemTop + itemHeight > viewportScroll + viewportHeight || itemTop < viewportScroll ) {
            var newScroll = itemTop - ( viewportHeight - itemHeight ) + 20;
            $( 'html, body' ).animate( { scrollTop : newScroll } );
        }
    },

    scrollToTopIfNotInView : function( item ) {
        var itemTop        = item.offset().top;
        var itemHeight     = item.outerHeight();
        var viewportScroll = $( window ).scrollTop();
        var viewportHeight = $( window ).height();

        // If the item is too far below the screen viewport, we need to scroll down to it, until the bottom of it is visible...
        if ( itemTop + itemHeight > viewportScroll + viewportHeight || itemTop < viewportScroll ) {
            $( 'html, body' ).animate( { scrollTop : itemTop - 20 } );
        }
    },

    scrollToTop : function( item ) {
        var itemTop = item.offset().top;
        $( 'html, body' ).animate( { scrollTop : itemTop - 20 } );
    }

};