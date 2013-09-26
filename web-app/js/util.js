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

    }

};