http://en.wikipedia.org/wiki/Repertory_grid

Apply heuristics to order the list of cycles to remove (e.g. the most recent)

Consider feeding the existing BN into the system as a user who proposes relationships.
They could be weighted according to how much you want the network to be modified.

Identify kernel theory for research

Prompt if they click next without finding any variables which are useful.

Write about Self selection of participants, and how they are netizens


# ROUND TWO TESTING

## Comments from various people

> Mandatory comments for relationships 

Two people asked for this - but it may require too much extra time. Perhaps just for the ones where there are not any comments arguing whatever your position is (i.e. yes or no).


> Force people to view disagreements.

This might work like the "needs review" system on the index page: You need to review each variable that is not under complete agreement.


> Where should I go after I have fixed all the relationships?  No button to go further!

Hmmm. This sounds like a bug. There will need to be a button on the page, and I should probably add a menu at the top too, though if I can get away without a menu I'd be happy.


> Add key up to the top of the index page, so that the lightbulb means "needs review" and the tick doesn't. Then remove the "(needs review)" text (but add to alt tag of lightbupb)."

Is there a key wherever the icons are present?


> And this. I can't believe I said "No" and gave a supporting reason.  May be enable the comments only after the user tick the box "I think it does".

I need to fix the first round, because I think people accidentally unchecked them by clicking the checkbox in the list. I should remove that checkbox completely.
Perhaps a quick "click yes or no" for each variable, which classifies it into two lists below, which they can then review.
This has the added benefit of ensuring that all "No" options are intentional, not by default.


> The systems bombed out when I pressed the "keep direct relationship" button on the screen below after using the go back and go forward arrows on Firefox.

I'll investigate. Looks like the redirect is not being done properly. I want it to redirect without ever showing the link to the browser, so that "Back" goes back to "/problems" rather than "/keepRedunantRelationship"

NEED BETTER ERROR HANDLING:

Firstly, a proper error page, secondly, email me when an error occurs with stacktrace and user info.


> Once I remove a direct relationship, I cannot undo it.  What about if I want to change my mind or I did it by accident?

Good question. This will need some thought. At the least, I can add a notification at the top which says "Undo" and stores the relationship which was deleted in the session.


> Once all of the variables haven been reviewed.  Do you want people to review the "x potential problems"?  Or it is optional?

It is optional, so perhaps I should emphasise that.


> Placing the changing "Problematic relationships (which cause cycles)" at the top  is not a good idea when you have pages of relationship to go through.  You cannot expect the user to go back up to the top of the list.  Chances are they will just hit the button back to the list.

Seeing as cycles tend to induce many possibly redundant relationships, perhaps we should have a screen where cycles are shown (and only cycles) and then after they are all resolved, show the potentially redundant relationships.
