package bn.elicitator.init.loaders

/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2015 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import bn.elicitator.AppProperties
import bn.elicitator.State
import bn.elicitator.Variable
import bn.elicitator.init.DataLoader

import javax.servlet.ServletContext

class ExampleDataLoader extends DataLoader {

    @Override
    protected String getHomePageContent() {
        """
        <h2>Welcome to our study</h2>
        <p>
            This is the landing page for your study. You probably want to leave the button below, because at this
            stage it is not automatically added to the landing page. Feel free to style it as you wish though.
            Note that the <code>&#91;serverURL&#93;</code> palceholder will automatically be replaced with the
            web address (e.g. http://localhost:8080/bn-elicitator/) as configured in
        </p>
        
        <div style='text-align: center; margin-top: 2em;'>
            <p>
                Does this sound like something you can help with?
                <br />
                <button class='big' onclick='document.location = "[serverURL]/explain"'>Register / Log in</button>
            </p>
        </div>
        """
    }

    /**
     * This will create a number of people with the username and password:
     *  * expert1
     *  * expert2
     *  * etc...
     */
    @Override
    protected int getNumTestUsersToCreate() {
        10
    }

    @Override
    protected AppProperties getProperties(ServletContext servletContext) {
        new AppProperties(
            adminEmail          : "your.email@example.com",
            url                 : "http://localhost:8080/bn-elicitator",
            title               : "Name of your study",
            explanatoryStatement: explanatoryStatement
        )
    }
    
    private String getExplanatoryStatement() {
        """
        <h2>Consent form</h2>
        <p>
            This is where you display your consent form to the user. It will be put below a heading with the
            label "Explanatory Statement", and a checkbox stating "I have read and understood this explanatory statement.".
            Participants will not be able to continue until they have checked this box and then clicked "Continue".
        </p>
        """
    }

    /**
     * Background variables can influence any of:
     *  * other background variables
     *  * mediating variables
     *  * problem variables
     *  * symptom variables
     */
    @Override
    protected List<Variable> getBackgroundVariables() {
        
        [
                
            new Variable(
                label: "Variable_1",
                readableLabel: "Variable One",
                usageDescription: "Does the state of Variable One <em>directly</em> influence any of these?",
                description: "Help text for this variable." ),
                
            new Variable(
                label: "Variable_2",
                readableLabel: "Variable Two",
                usageDescription: "Does the state of Variable Two <em>directly</em> influence any of these?",
                description: "Help text for this variable." ),
                
        ]
        
    }
    
    /**
     * Mediating variables can influence any of:
     *  * other mediating variables
     *  * problem variables
     *  * symptom variables
     */
    @Override
    protected List<Variable> getMediatingVariables() {
        []
    }

    /**
     * Mediating variables can influence any of:
     *  * other problem variables
     *  * symptom variables
     */
    @Override
    protected List<Variable> getProblemVariables() {
        []
    }

    /**
     * Mediating variables can influence any of:
     *  * other symptom variables
     */
    @Override
    protected List<Variable> getSymptomVariables() {
        []
    }

    /**
     * Specify states for each variable defined int he get...Variables methods.
     */
    @Override
    protected Map<String,List<State>> getVariableStates() {

        [

            "Variable_1": [
                new State( "False", "Variable One is <em>not</em> engaged." ),
                new State( "True", "Variable One <em>is</em> engaged." ),
            ],

            "Variable_2" : [
                new State( "Low", "<em>Below</em> 5.0 mega-deelios" ),
                new State( "Medium", "<em>Between</em> 5.0 and 10.0 mega-deelios" ),
                new State( "High", "<em>Above</em> 10.0 mega-deelios" ),
            ]

        ]
        
    }
}
