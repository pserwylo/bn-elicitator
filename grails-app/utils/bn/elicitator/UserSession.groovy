package bn.elicitator

import bn.elicitator.auth.User
import bn.elicitator.events.LoggedEvent

/**
 * A "session" is a bit of a hairy fairy term, but it relates to a single block of time spent by the user on the website.
 * In our case, we'll refer to a session the same way that Google Analytics does: as a period of activity with no more
 * than 30 minutes between the browser interacting with the server.
 */
class UserSession {
    
    User user
    private List<LoggedEvent> events = []
    
    boolean includes( LoggedEvent event ) {
        if ( events.size() == 0 ) {
            return true
        } else if ( event.delphiPhase != delphiPhase ) {
            return false
        } else {
            long timeSince = event.date.time - endDate.time
            return ( timeSince >= 0 && timeSince < 1000 * 60 * 30 )
        }
    }
    
    void add( LoggedEvent event ) {
        events.add( event )
    }

    Date getStartDate() { events.size() > 0 ? events[ 0 ].date : null }
    Date getEndDate()   { events.size() > 0 ? events[ events.size() - 1 ].date : null }
    int getDurationInSecs() { ( endDate.time - startDate.time ) / 1000 }
    int getDelphiPhase() { events.size() > 0 ? events[ 0 ].delphiPhase : 0 }
    
    public String toString() {
        "Session [User: $user.id, Duration: ${durationInSecs}s]"
        
    }
    
}
