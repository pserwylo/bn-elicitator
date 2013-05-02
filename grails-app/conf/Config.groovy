// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [ CustomConfig ]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

	appenders {
		console     name: "stdout",
		            layout: pattern(conversionPattern: "%c{2} %m%n")

		rollingFile name: "bn",
		            maxFileSize: 1024,
		            file: "/tmp/bn-elicitator.log"
	}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName     = 'bn.elicitator.auth.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName  = 'bn.elicitator.auth.UserRole'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'roles'
grails.plugins.springsecurity.authority.className                = 'bn.elicitator.auth.Role'
grails.plugins.springsecurity.authority.nameField                = 'name'
grails.plugins.springsecurity.securityConfigType                 = "InterceptUrlMap"
grails.plugins.springsecurity.rememberMe.persistent              = true
grails.plugins.springsecurity.ui.encodePassword                  = false // The Domain class for users does this for us.
grails.plugins.springsecurity.rememberMe.persistentToken.domainClassName = 'bn.elicitator.auth.PersistentLogin'


grails.plugins.springsecurity.interceptUrlMap = [

	'/admin/**'       : ['ROLE_ADMIN'],
	'/adminmanage/**' : ['ROLE_ADMIN'],
	'/adminDash/**'   : ['ROLE_ADMIN'],
	'/email/**'       : ['ROLE_ADMIN'],
	'/output/**'      : ['ROLE_ADMIN'],
	'/user/**'        : ['ROLE_ADMIN'],

	'/elicit/**'      : [ 'ROLE_ADMIN', 'ROLE_CONSENTED' ],

	'/auth/oauth/**'  : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/register/**'    : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/oauth/**'       : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/login/**'       : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/logout/**'      : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/static/**'      : ['IS_AUTHENTICATED_ANONYMOUSLY'],


	'/explain/**'    : ['IS_AUTHENTICATED_REMEMBERED'],
	'/preference/**' : ['IS_AUTHENTICATED_REMEMBERED'],
	'/**'            : ['IS_AUTHENTICATED_REMEMBERED'],

]


grails.plugins.dynamicController.mixins = [
   'com.burtbeckwith.grails.plugins.appinfo.IndexControllerMixin'      : 'com.burtbeckwith.appinfo_test.AdminManageController',
   'com.burtbeckwith.grails.plugins.appinfo.Log4jControllerMixin'      : 'com.burtbeckwith.appinfo_test.AdminManageController',
   'com.burtbeckwith.grails.plugins.appinfo.SpringControllerMixin'     : 'com.burtbeckwith.appinfo_test.AdminManageController',
   'com.burtbeckwith.grails.plugins.appinfo.MemoryControllerMixin'     : 'com.burtbeckwith.appinfo_test.AdminManageController',
   'com.burtbeckwith.grails.plugins.appinfo.PropertiesControllerMixin' : 'com.burtbeckwith.appinfo_test.AdminManageController',
   'com.burtbeckwith.grails.plugins.appinfo.ScopesControllerMixin'     : 'com.burtbeckwith.appinfo_test.AdminManageController',
   'com.burtbeckwith.grails.plugins.appinfo.ThreadsControllerMixin'    : 'com.burtbeckwith.appinfo_test.AdminManageController',
   // 'app.info.custom.example.MyConfigControllerMixin'                   : 'com.burtbeckwith.appinfo_test.AdminManageController'
]


grails.plugins.springsecurity.oauth.domainClass = 'bn.elicitator.auth.OAuthID'

grails.plugins.springsecurity.ui.register.defaultRoleNames = [ 'ROLE_EXPERT' ]
grails.plugins.springsecurity.ui.register.emailBody        = ''
grails.plugins.springsecurity.ui.register.emailFrom        = 'peter.serwylo@monash.edu'
grails.plugins.springsecurity.ui.register.emailSubject     = 'Survey account'