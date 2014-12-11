# bn-elicitator

Bayesian Network elicitator, using a methodology more akin to a survey then traditional interviews.

## About

This is a project I'm working on during my PhD thesis. Although continually evolving, by the end of the research project, I will have much more detail (and indeed evaluations) to describe here.

Currently, the process is along the lines of this:
 1) Describe the variables to use in the model to bn-elicitator (including the states they can take).
 2) Place variables into categories, where entire categories are dependent on other categories (e.g. "symptoms" pare dependent on "problems", but not the other way around).
 3) People visit the site, signup, get allocated questions to answer, then are given a survey to respond to.
 4) Responses are collated into a BN structure.

In time, I will update this README with more concise details and instructions.

## License

This project is licensed under the GPLv3 license.

http://www.gnu.org/licenses/gpl.html


## Acknowledgements


### FamFam icon set

http://www.famfamfam.com/lab/icons/silk


### Project Troia

http://www.project-troia.com

External dependency used for combining multiple (possibly conflicting) responses. This is an open source implementation of the Dawid & Skene algorithm. We pass several responses of the form "X does influence Y" and "X does NOT influence Y" to let it make a judgement about which to choose for inclusion in the final model.


### Libraries / Dependencies

* http://www.jquery.com
* http://www.grails.org
* http://www.graphviz.org - Required for generating SVG's for the BN (uses the commandline 'dot' command).
