http://en.wikipedia.org/wiki/Repertory_grid

Apply heuristics to order the list of cycles to remove (e.g. the most recent)

Consider feeding the existing BN into the system as a user who proposes relationships.
They could be weighted according to how much you want the network to be modified.

Identify kernel theory for research

Prompt if they click next without finding any variables which are useful.

Write about Self selection of participants, and how they are netizens


# ROUND TWO TESTING

> Mandatory comments for relationships 

Two people asked for this - but it may require too much extra time. Perhaps just for the ones where there are not any comments arguing whatever your position is (i.e. yes or no).


> Force people to view disagreements.

This might work like the "needs review" system on the index page: You need to review each variable that is not under complete agreement.


> And this. I can't believe I said "No" and gave a supporting reason.  May be enable the comments only after the user tick the box "I think it does".

I need to fix the first round, because I think people accidentally unchecked them by clicking the checkbox in the list. I should remove that checkbox completely.
Perhaps a quick "click yes or no" for each variable, which classifies it into two lists below, which they can then review.
This has the added benefit of ensuring that all "No" options are intentional, not by default.

> The systems bombed out when I pressed the "keep direct relationship" button on the screen below after using the go back and go forward arrows on Firefox.

I'll investigate. Looks like the redirect is not being done properly. I want it to redirect without ever showing the link to the browser, so that "Back" goes back to "/problems" rather than "/keepRedunantRelationship"

NEED BETTER ERROR HANDLING:

Firstly, a proper error page, secondly, email me when an error occurs with stacktrace and user info.

> Add a "Not finished with variable yet, but return to main menu anyway."

# Logging

I want to make sure that after the survey is complete, I can inspect certain phenomenon in order to make comment about the appropriateness of the system.

The following is a list of info I need to log:

 * When they change their mind (as well as if they commented, how many other people commented too, essentially what info was available to them when they changed their mind)
 * Removing of cycles
 * Retaining potentially redundant relationships
 * Delete potentially redundant relationships
 * Delete other relationships after being prompted about potential redundant relationships
 * Proportion of relationships which they comment about

