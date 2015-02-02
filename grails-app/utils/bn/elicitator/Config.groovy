package bn.elicitator

/**
 * Wrapper around some commonly wanted grails config values. 
 */
class Config {
    
    private final def conf;
    
    public static Config get( def grailsApplication ) {
        new Config( grailsApplication )
    }
    
    public Config( def grailsApplication ) {
        conf = grailsApplication.config
    }
    
    public boolean isFacebookLoginEnabled() {
        conf.oauth?.providers?.facebook?.secret as Boolean
    }

    public boolean getCanSendEmails() {
        conf.grails?.mail?.username as Boolean
    }
    
}
