if ( typeof bn === 'undefined' ) {
    bn = {};
}

bn.das2004 = {};

bn.das2004.Manager = klass(function( questionSelector, nextLink ) {

    this.nextLink         = nextLink;
    this.questionSelector = questionSelector;
    this.questions        = $( this.questionSelector );

    var parent = this.questions.parent();
    this.questions.detach().sort( function() { return Math.random() - 0.5; } ).appendTo( parent );

    if ( this.questions.length == 0 ) {
        this.nextScreen();
        return;
    }

    this.currentQuestionIndex = 0;
    $( '#total-scenarios' ).html( this.questions.length );
    this.currentQuestion().show();

}).methods({

    nextScreen : function() {
        document.location = this.nextLink;
    },

    /**
     * Can use this to prevent us getting out of sync with the showing and hiding of questions.
     * Previously, you could click a radio button, trigger a change, and while it is moving onto
     * the next question, you could still click the original questions radio buttons which caused strife.
     * @param item
     * @returns {boolean}
     */
    isCurrent : function( item ) {
        var parent  = $( item ).closest( this.questionSelector );
        var current = this.currentQuestion();
        return ( parent.length > 0 && current.length > 0 && parent.get( 0 ) === current.get( 0 ) )
    },

    currentQuestion : function() {
        return $( this.questions[ this.currentQuestionIndex ] );
    },

    nextQuestion : function() {
        $( 'div.qtip' ).filter( ':visible' ).qtip( 'hide' );
        var self = this;
        var previousQuestion = this.currentQuestion();
        self.currentQuestionIndex ++;
        previousQuestion.fadeOut( 100, function() {
            if ( self.currentQuestionIndex < self.questions.length ) {
                self.currentQuestion().fadeIn( 800 );
                self.equalizeHeights( self.currentQuestion().find( '.ui-button' ) );
                $( '#scenario-number' ).html( self.currentQuestionIndex + 1 );
            } else {
                self.nextScreen();
            }
        });
    },

    equalizeHeights : function( items ) {
        bn.utils.equalizeHeights( items );
    }

});