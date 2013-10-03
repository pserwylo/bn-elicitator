grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

		// flatDir name:"netica", dirs:"/home/pete/code/NeticaJ/bin/64_bit"
		// flatDir name:"jung", dirs:"/home/pete/code/jung/"

        // uncomment these to enable remote dependency resolution from public Maven repositories
        // mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime 'mysql:mysql-connector-java:5.1.23'
        runtime 'Jama:Jama:1.0.3'

        // runtime 'NeticaJ:NeticaJ:4.19'
    }

    plugins {
		runtime ":hibernate:3.6.10.1"
		runtime ":jquery:1.7.1"
		runtime ":resources:1.1.6"
		runtime ":database-migration:1.3.2"
		runtime ':oauth:2.0.1'

		compile ":mail:1.0.1", {
			excludes 'spring-test'
		}

		compile ":jquery:1.7.1"
		compile ":resources:1.1.6"
		compile ":famfamfam:1.0.1"
		compile ":spring-security-core:1.2.7.3"
		compile ":spring-security-ui:0.2"
		compile ":spring-security-oauth:2.0.1.1"
        compile ":ckeditor:3.6.2.2"
		compile ":rest-client-builder:1.0.2"


		// Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:7.0.42"
    }
}
