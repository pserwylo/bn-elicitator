# bn-elicitator

Bayesian Network elicitator, using a methodology more akin to a survey then traditional interviews.

## About

This is a project I'm working on during my PhD thesis.
Although continually evolving, by the end of the research project, I will have much more detail 
(and indeed evaluations) to describe here.

Currently, the process is along the lines of this:

 * Describe the variables to use in the model to bn-elicitator (including the states they can take).
 * Place variables into categories, where entire categories are dependent on other categories (e.g. "symptoms" pare dependent on "problems", but not the other way around).
 * People visit the site, signup, get allocated questions to answer, then are given a survey to respond to.
 * Responses are collated into a BN structure.

## License

This project is licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html).

## Setup

### Dependencies

#### Building

You will require [Grails](http://www.grails.org) in order to run this software.
The current version of BN Elicitator was built and tested using Grails 2.3.4 ([.zip, 136mb](http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/grails-2.3.4.zip)).
It may be possible to upgrade to 2.4.* or later, but this hasn't been tested yet.

#### Running

The bn-elicitator.war ([view downloads](https://github.com/pserwylo/bn-elicitator/releases)) file contains all of the dependencies inside, so you should be able to plop it into any J2EE server.
Follow the relevant documentation for your server (e.g. Tomcat, Jetty, etc) for how to do this.

### CustomConfig.groovy

The first thing you'll need to do is `cp grails-app/conf/CustomConfig.groovy.example grails-app/conf/CustomConfig.groovy` and set values as appropriate. They should all be documented and explain what is required.

### Loading data

In order to configure your site, you will need to create a class that extends `bn.elicitator.init.DataLoader` and
override the relevant methods. A good starting point is to copy `bn.elicitator.init.loaders.ExampleLoader` and change
what you see fit.

If you ran Grails before this stage, it will have already initialised the database you specified in your `CustomConfig.groovy`.
In this case, you should drop tat database and re-create it before running grails again.

TODO: In the future, I'd like to change this so that it uses a more "groovy"-like DSL in order to specify variables,
states, etc.

### Running grails

From the command line, `cd` to the same directory as this README file. Then run `grails run-app`.

There will be a default user for the website called "admin", with the password "I'm an admin user".

## Acknowledgements

* [Grails](http://www.grails.org)
* [jQuery](http://www.jquery.com)
* [GraphViz](http://www.graphviz.org) - Required for generating SVG's for the BN (uses the commandline 'dot' command).
* [FamFamFam silk icons](http://www.famfamfam.com/lab/icons/silk)
* [Project Troia](http://www.project-troia.com) (External dependency used for combining multiple (possibly conflicting) responses. This is an open source implementation of the Dawid & Skene algorithm. We pass several responses of the form "X does influence Y" and "X does NOT influence Y" to let it make a judgement about which to choose for inclusion in the final model.)
