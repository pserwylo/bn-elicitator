http://en.wikipedia.org/wiki/Repertory_grid

Apply heuristics to order the list of cycles to remove (e.g. the most recent)

Consider feeding the existing BN into the system as a user who proposes relationships.
They could be weighted according to how much you want the network to be modified.

Identify kernel theory for research

Explain WHY we collect confidence

Add ticks to confidence slider.

Prompt if they click next without finding any variables which are useful.

Write about Self selection of participants, and how they are netizens



ROUND TWO STUFF:

Comments from Mark:
Mandatory comments
Force people to view disagtreements, put less influence on agreement, and hide the useless ones. Use the same "needs review" metaphore as in the main list.

Comments from Grace:
> 2. Overall I think both tabs are much too wordy.  People won't read all the lenghy words!
Alrighty, I recently refactored the code to replace all of the strings with i18n messages, so they are very easy to change.

> 5. What about if I want to remove one of the alternative relationships? Like the second under "You said" below?
>
> Vehicle Year -> Airbag
> You said:
>
>     Vehicle Year -> Car Make and Model -> Airbag
>     Vehicle Year -> Car Strength -> Damage to clients car -> Cost of Clients Car -> Airbag
>
Yeah, I've had this mentioned to me by a couple of others to. The problem is that you need to specify which of the "x" number of relationships in the chain above you want to remove. I think I've settled on making the right arrows into buttons, and when you click them, it gives you the option to remove it.

> 6. Where should I go after I have fixed all the relationships?  No button to go further!
Hmmm. This sounds like a bug. There will need to be a button on the page, and I should probably add a menu at the top too, though if I can get away without a menu I'd be happy.




# Meetings comments:

Change the "Which of the following variables have an influence..." text for subsequent rounds, to highlight that they have already made a decision, and just need to review.

Change "Show details" to "review" or something more consise for subsequent rounds.

Group variables into "Has influence" and "doesn't have influence" groups, and show the number of people who said the same thing on the right hand side.

Remove all variables that 0% of people were interested in.

Clean up form on the right ("show details form") so that it only hase checkbox, text area, and other peoples reasons. Rename the question up the top to invite them to review.

REMOVE CONFIDENCE

No more agree/disagree. 

Add key up to the top of the index page, so that the lightbulb means "needs review" and the tick doesn't. Then remove the "(needs review)" text (but add to alt tag of lightbupb)."
