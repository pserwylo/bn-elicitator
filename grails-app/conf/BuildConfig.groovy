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

		flatDir name:"netica", dirs:"/home/pete/code/NeticaJ/bin/64_bit"
		flatDir name:"jung", dirs:"/home/pete/code/jung/"

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

        runtime 'mysql:mysql-connector-java:5.1.16'
        runtime 'NeticaJ:NeticaJ:4.19'
        runtime 'annas:annas:2.0'
		runtime 'jung:jung-graph-impl:2.0.1'
		runtime 'jung:jung-io:2.0.1'
		runtime 'jung:jung-algorithms:2.0.1'
		runtime 'jung:jung-api:2.0.1'
		runtime 'jung:jung-visualization:2.0.1'
		runtime 'jung:collections-generic:2.0.1'
		runtime 'jung:collections-generic:4.01'
		runtime 'jung:colt:1.2.0'
		runtime 'jung:concurrent:1.3.4'
		runtime 'jung:stax-api:1.0.1'
		runtime 'jung:vecmath:1.3.1'
		runtime 'jung:wstx-asl:3.2.6'
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"
    }
}
