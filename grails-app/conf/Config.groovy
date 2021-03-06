// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [
	"file:/etc/bn-elicitator/config.groovy",
	"file:${userHome}/.grails/${appName}-config.groovy",
	"classpath:${appName}-config.groovy",
]

if ( System.properties[ "${appName}.config.location" ] ) {
	grails.config.locations << "file:" + System.properties[ "${appName}.config.location" ]
}

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

	/*root {
		all()
	}*/

	error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
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
grails.plugins.springsecurity.oauth.registration.roleNames       = [ 'ROLE_EXPERT' ]
grails.plugins.springsecurity.auth.ajaxLoginFormUrl              = "/login/auth"
grails.plugins.springsecurity.adh.errorPage                      = "/login/denied"

grails.plugins.springsecurity.useSwitchUserFilter = true

grails.plugins.springsecurity.interceptUrlMap = [

	'/admin/**'         : ['ROLE_ADMIN'],
	'/adminmanage/**'   : ['ROLE_ADMIN'],
	'/adminDash/**'     : ['ROLE_ADMIN'],
	'/email/**'         : ['ROLE_ADMIN'],
	'/output/**'        : ['ROLE_ADMIN'],
	'/user/**'          : ['ROLE_ADMIN'],
	'/content/admin/**' : ['ROLE_ADMIN'],

	'/j_spring_security_switch_user': ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'],

	'/elicit/**' : [ 'ROLE_ADMIN', 'ROLE_CONSENTED' ],

	'/auth/oauth/**'    : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/register/**'      : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/oauth/**'         : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/login/**'         : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/logout/**'        : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/static/**'        : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/content/**'       : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/home/**'          : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/uploads/Image/**' : ['IS_AUTHENTICATED_ANONYMOUSLY'],
	'/'                 : ['IS_AUTHENTICATED_ANONYMOUSLY'],


	'/explain/**'    : ['IS_AUTHENTICATED_REMEMBERED'],
	'/preference/**' : ['IS_AUTHENTICATED_REMEMBERED'],
	'/**'            : ['IS_AUTHENTICATED_REMEMBERED'],

]

ckeditor {
	config = "/js/myckconfig.js"
	skipAllowedItemsCheck = false
	defaultFileBrowser = "ofm"
	upload {
		basedir = "/uploads/"
		overwrite = false
		link {
			browser = true
			upload = false
			allowed = []
			denied = ['html', 'htm', 'php', 'php2', 'php3', 'php4', 'php5',
					'phtml', 'pwml', 'inc', 'asp', 'aspx', 'ascx', 'jsp',
					'cfm', 'cfc', 'pl', 'bat', 'exe', 'com', 'dll', 'vbs', 'js', 'reg',
					'cgi', 'htaccess', 'asis', 'sh', 'shtml', 'shtm', 'phtm']
		}
		image {
			browser = true
			upload = true
			allowed = ['jpg', 'gif', 'jpeg', 'png']
			denied = []
		}
		flash {
			browser = false
			upload = false
			allowed = ['swf']
			denied = []
		}
	}
}

grails.plugins.springsecurity.oauth.domainClass = 'bn.elicitator.auth.OAuthID'

grails.plugins.springsecurity.ui.register.defaultRoleNames = [ 'ROLE_EXPERT' ]
grails.plugins.springsecurity.ui.register.emailSubject     = "Confirm survey registration"
grails.plugins.springsecurity.ui.register.emailBody        = '''\
Hi $user.realName,<br/>
<br/>
Thanks for registering to complete this survey.<br/>
Please visit <a href="$url">$url</a> to complete the registration.<br/>
<br/>
Cheers,<br />
pete.
'''

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
