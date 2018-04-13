# reader

This is a program I'm working on that can interpret the English text on cards in Magic: The Gathering, and convert the sentences to instructions that can be understood by a computer.

Magic: The Gathering is a popular card game that's been around for nearly 25 years. There is well over 2500 cards in the game, each with different rules and textboxes. I wanted to create this project so that I can take a step in creating my own Magic: The Gathering simulator without the need of individually programming the functions of each of these cards.

As it currently stands, this program works by first gathering the basic information of the card, such as the mana cost, the type, the color, and other simple information. The program has a different set of possible effects based on what kind of card it is.

Once this has been narrowed down, it's a matter of filling in specific variables.
- Is there a target? How many? What kind of target?
- Are players gaining life? Who? How much life?
- Is there an effect occuring until a certain period?

These types of questions can be answered by searching for specific words in a specific order in the text box.

Once this program is completed, a database of every MtG card including its basic information and textbox can be fed to it, allowing any card to be immediately processed and understood by the computer without the need of programming the functionality of the card. 
