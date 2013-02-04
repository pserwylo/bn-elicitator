http://en.wikipedia.org/wiki/Repertory_grid

Apply heuristics to order the list of cycles to remove (e.g. the most recent)

Bug in calculating number of kept relationships.

Consider feeding the existing BN into the system as a user who proposes relationships.
They could be weighted according to how much you want the network to be modified.

Identify kernel theory for research



Explain WHY we collect confidence

Add ticks to confidence slider.

Prompt if they click next without finding any variables which are useful.

Write about Self selection of participants, and how they are netizens



ROUND TWO STUFF:

Make the comment in the relationship chain be the last comment that was made, regardless of which round its in.
Sort the main list by disagreement (asc) to make users get that feeling of progress straight away.
Message explaining the agreement/disagreement/nobody cares.
Message when there is zero agreement/disagreement/discontentness.

Comments from Mark:
Mandatory comments
Force people to view disagtreements, put less influence on agreement, and hide the useless ones. Use the same "needs review" metaphore as in the main list.

Comments from Grace:
> 1. Why is there no button to go back to the previous menu once you choose "show details" or "Show X potential problems"?  What if I change my mind once I see what one tab looks like and want to use the other tab instead?
Roger that. I'll add a button.

> 2. Overall I think both tabs are much too wordy.  People won't read all the lenghy words!
Alrighty, I recently refactored the code to replace all of the strings with i18n messages, so they are very easy to change.

> 3. Under tab "Show X potential problems", Just delete all the self explanatory explanations "If you think that the way in which ...", and may be replace with "Which one would you choose?" if you really want the user to choose one not both"
Yeah, I've noticed that they tend to repeat themselves. I'll look into that wording.

> 3.a. Also remove on button " ... (previously you decided to keep)"
As above. Will do, but I'll probably put it next to the button, in a smaller font. I still think its somewhat important, but also I agree that the button is grossly oversized.

> 4. After you press a bottom to keep or remove a relationship, the screen always goes to the top.  If you have a long list of relationships to fix, it is really tedious to have to scroll back down bypassing all the relationships you have fixed.   Can you save the cursor position so that the screen refreshes back to where you left off and not the top?
Gotcha. I can make it sent through the index location as well as the relationship we're removing. Then when it redirects back, I'll send the page to where it was.

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

> 7.  There need to be an intro at the beginning on what is expected from this round.  And a finish "thank you" screen to show that the user has completed this round.

Yup, I've implemented some help overlays which you see the first time you load up the page. They are contextual, in that they are able to point at specific elements on the page, but there are also ones which centre on the page (e.g. when you start a new round, it explains why you are seeing the same thing again):
