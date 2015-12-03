package bn.elicitator.events

import bn.elicitator.auth.User
import org.apache.log4j.helpers.ISO8601DateFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * For the CPT evaluation part of the survey, no logging was setup. However, there were access logs recorded by
 * the Tomcat server, so this class allows analysis of these pave view logs as if they were events logged by BNE.
 */
class PageViewEventFromLog extends LoggedEvent {

    private static final DateFormat ISO_FORMAT = new ISO8601DateFormat()

    private static List<String> PATHS_TO_IGNORE = [
        // Stuff to do with logging in is often seen once and then never again.
        /cars\/login\/auth/,
        /cars\/explain/,
        /cars\/j_spring_security_check/,
        /cars\/login\/ajaxSuccess/,
        /cars\/login\/authfail.*/,
        /cars\/oauth.*/,
        /cars\/auth\/oauth.*/,

        // This is an admin page which is not visible to regular participants.
        /cars\/user\/.*/,
            
        // Errors are not interesting enough to count as an event, because they often appear with people who visited
        // the site, saw an error, then left.
        /cars\/error\/jsError/,
    ]
    
    private String page
    
    private UserFromIp userFromIp
    
    public PageViewEventFromLog(UserFromIp user, Date time, String page) {
        this.user = user
        this.userFromIp = user
        this.date = time;
        this.delphiPhase = 1
        this.description = ""
        this.page = page
    }

    public UserFromIp getUserFromIp() { this.userFromIp }
    public String getPage() { this.page }
    public String toString() {
        "Page View [Time: ${ISO_FORMAT.format(date)}, Page: $page]"
    }

    @Override
    String getDescription() {
        return null
    }

    static List<PageViewEventFromLog> createMultiple(String tomcatAccessLogCOntents) {
        def regex = /(.*) - - \[(.*)\] ".* \/(.*) .*".*/
        def dateFormat = new SimpleDateFormat( "dd/MMM/yyyy:HH:mm:ss ZZZZZ")
        Map<String, UserFromIp> users = [:]
        List<PageViewEventFromLog> logEntries = tomcatAccessLogCOntents.split("\n").collect { String logLine ->
            def matcher = logLine =~ regex
            if (matcher.size() > 0) {
                final ArrayList<String> bits = matcher[0] as ArrayList
                final String ip = bits[1]
                final String path = bits[3]
                
                for (String toIgnore : PATHS_TO_IGNORE) {
                    if (path =~ toIgnore) {
                        return null
                    }
                }
                
                final Date date = dateFormat.parse(bits[2])
                
                if (!users.containsKey(ip)) {
                    users.put(ip, new UserFromIp(ip))
                }

                return new PageViewEventFromLog(users[ip], date, path)
            } else {
                return null
            }
        }.findAll { it != null }

        int numberOfCompletions = logEntries.count { it.page == "cars/feedback/save" }

        Map<UserFromIp, List<PageViewEventFromLog>> meaningfulIps = logEntries.groupBy {
            it.userFromIp
        }.findAll {
            it.value.size() >= 10
        }

        def result = meaningfulIps.entrySet()*.value.flatten()
        return result
    }
    
    static class UserFromIp extends User {
        
        private final String ip;
        
        public UserFromIp(String ip) { this.ip = ip }
        public Long getId() { ip.hashCode() }
        public String toString() { ip }
        
    }
    
}
