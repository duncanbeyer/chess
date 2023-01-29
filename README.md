Chess
=======


This is a chess engine and GUI that I created in Java. 


It uses the Stdlib Stddraw library to run the main game window.

Other GUIs (menus) use JFrame.

I wrote almost all of it during winter break to keep myself occupied. I finished it off with the en passant and castling operations at 
the beginning of the following college term. It was all written quickly using whatever logic I could think to do first. I had to look up what the exact rules
were for operations such as en passant and castling but the logic of implementing it is all my own.

If you are interested in the specific logics of the game, the program is rife with comments.

Features
=======
When the game is run, a menu opens and the player can select either a regular game or a timed game.

Both versions are plaver vs player.  

The game features smart suggestions that, upon selecting a piece, highlight different moves including
a regular move, taking an opposing piece, putting the opposing king in check, performing en passant, and castling.

The smart suggestions can be toggled on and off.

The game also features coordinates, which can be toggled to appear in the corner of each space. 

To the right of the chess board, a text box at the top of the sidebar indicates which player's turn it currently is.

At the other end of the sidebar, another text box will indicate when one of the kings is in check and that king's color.

If either players achieves checkmate, the textbox that displays if a player in check will update to say which player won and the game will end.

Menu
=======
In the lower part of the sidebar is a button to open an "Options Menu". It's not really a button, it just uses stddraw mouse
detection to tell if the player clicks it. When pressed, the menu gives the player the ability to toggle spatial coordinates, 
toggle smart suggestions, start a new game, or start a timed game. 

Visual indications of the coordinates and smart suggestions are visible above their toggles to let the player know for sure
what they are toggling.

Toggling the coordinates or suggestions closes the menu, pressing "Start a New Game" closes the menu and the current game and 
starts a new one in a new window. Pressing "Start a Timed Game" opens the timed game menu.


Timed Game
=======
The timed game menu allows the player to input a game time in minutes and a turn interval in seconds. 

There is a button at the bottom of the menu labeled "Timed Game Info" which opens a new JOptionPane 
that explains with words and pictures how a timed game works. 

If the user presses "Start Game" without inputting anything, a regular game without timers starts. If the user inputs game time
but not a turn interval time, then a timed game starts with an interval of 0.

There is a "Go Back" button under the "Start Game" button which takes the player back to the previous menu. 

If the user starts a timed game, then the board will begin the same but there will be two timers underneath the box indicating the turn.

The timers are labeled "White Timer" and "Black Timer". 

They work properly, pausing and resuming when turns end, and if an interval was specified, it will add to the timer at the start of each turn.

