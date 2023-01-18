package rungame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Stopwatch {
	
static int timeWhite;
static int timeBlack;
static int interval;
// This class serves as the stopwatches for both the black and the white timers in the timed game, it runs
// both by running constantly and simply switching between which value to decrement when the turn changes.

static Timer timer;


public Stopwatch() {
	
}







public void main(String[] args) {

    int delay = 1000;
    int period = 1000;
    // the delay and period are set to 1000 so the timer runs every second.
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
        public void run() {
        	RunWindow.takesTimer(sendVal(RunWindow.turn));
        	// every second the timer runs, it sends a value from one of the two timer ints to the 
        	// takesTimer method in the RunWindow class.
        }
    }, delay, period);
}




private static final int sendVal(boolean color) {
	// the main method calls this method every second using the turn boolean from the RunWindow as 
	// the input variable. Using the turn, this method decides which int to decrement and return. Before it
	// decrements or returns one, it checks every time if one of them has run out because at that point the 
	// game will end.
	
    if (timeWhite == 0) {
    	RunWindow.timeExpired(true);
        timer.cancel();
    }
    else if (timeBlack == 0) {
    	RunWindow.timeExpired(false);
    	timer.cancel();
    }
    
    if (color) {
    	return --timeWhite;
    }
    else {
    	return --timeBlack;
    }
    
}


public void reset() {
	// this method is called from RunWindow to make sure there is never more than one timer running at once
	if (timer != null) timer.cancel();
	
}


public void start() {
	
	// this method opens its own jframe to get the timer and interval sizes from the user. It sets both color
	// timers to the inputted size and sets the interval to the inputted size.  
	
	JFrame timeValsMenu = new JFrame("Settings");
	// Creates the frame and sets the title to "Settings"

	timeValsMenu.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 200,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - 100);
	// This line uses the toolkit to find the pixel dimensions of the screen and initializes the location of 
	// the JFrame to be at the center of the screen
	
	ImageIcon img = new ImageIcon(RunWindow.class.getResource("/white rook.png"));
	// The RunWindow.class.getResource looks for the specified file "/white rook.png" in the project's
	// src folder and returns its path. This is used to initialize the ImageIcon img variable.
	
	
	timeValsMenu.setIconImage(img.getImage());
	// the ImageIcon img is called using img.getImage() to retrieve the Image file and is used to set the 
	// icon image of the JFrame.
	
	timeValsMenu.setLayout(new GridBagLayout());
	// GridBagLayout is used to keep the layout of the gui clear.
	
	GridBagConstraints gbc = new GridBagConstraints();
	// the GridBagConstraints gbc is initialized here, it will be used by every component, so it must be
	// altered in between their being added.
	

	gbc.insets = new Insets(10,10,10,10);
	// The insets determines how much space is in between each component. I believe each value is the space 
	// to the left, right, top, and bottom of the component.
	
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 0;
	JLabel label1 = new JLabel("Game Time in Minutes:");
	timeValsMenu.add(label1,gbc);
	gbc.gridx = 1;
	gbc.gridy = 0;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 0;
	JTextField textField = new JTextField("");
	timeValsMenu.add(textField,gbc);
	
	gbc.gridx = 0;
	gbc.gridy = 2;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 0;
	JLabel label2 = new JLabel("Turn Interval Time in Seconds:");
	timeValsMenu.add(label2,gbc);
	
	gbc.gridx = 1;
	gbc.gridy = 2;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1;
	JTextField getInterval = new JTextField("");
	timeValsMenu.add(getInterval,gbc);
	
	gbc.gridx = 0;
	gbc.gridy = 4;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.gridwidth = 2;
	JButton newTimedGame = new JButton("Start");
	timeValsMenu.add(newTimedGame,gbc);
	newTimedGame.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (textFieldHandler(textField.getText()) != null && intervalHandler(getInterval.getText()) != null) {
				// The getText() and getInterval() methods are used to check if the String values
				// inside the input fields can be converted into Integers. If they cannot, then the methods
				// will return the integers as null and this if statement will fail.
			
				
				timeValsMenu.dispose();
				// This menu disappears
				
				RunWindow FullGame = new RunWindow();
				// This statement initializes the actual game
				
				if (textFieldHandler(textField.getText()) != -1) {
					
					// textFieldHandler returns -1 when the text field is null, meaning the user has not
					// inputed anything. When this conditional is tripped, the user has inputted a valid
					// number. The game is being initialized and this conditional initializes its clock.
					// When textFieldHandler returns -1, this block is skipped and the game is initialized
					// without a timer.
					
					
					timeWhite = textFieldHandler(textField.getText());
					timeBlack = textFieldHandler(textField.getText());
					interval = intervalHandler(getInterval.getText());
					// We initialize these class variables here because we know based on the conditional
					// that the inputs are valid.
					
					main(null);
					// the main method of the stopwatch is called to start them counting down.
					
					FullGame.initialTimer(timeWhite);
					// This statement only runs after the game window and engine have been initialized in 
					// FullGame, and it simply displays the intitial times for the timer on-screen so that
					// they appear before they stay counting down.
					
				}
				
				
				
				RunWindow.run = true;
				// Finally, as this boolean is set to true, the game window will exit the menu while-loop and
				// will begin to detect user inputs on the gui.
			}
			else {
				// This block is run when the inputed values are not legal.
				JFrame f = new JFrame();
				JOptionPane.showMessageDialog(f,"Invalid Input");
				//  These two lines display a message on screen that the input was invalid.
				
				timeValsMenu.dispose();
				start();
				// This method then closes and restarts the start() method in order to clear the input
				// text fields. The f JOptionPane does not need dispose() or any EXIT_ON_CLOSE because 
				// the JOptionPane is clicked on and closed by the user.
			}
		}
	});
	
	gbc.gridx = 0;
	gbc.gridy = 6;
	gbc.gridwidth = 2;
	JButton back = new JButton("Go Back");
	// The purpose of this button is so that the user can go back to the previous menu. It will work for 
	// both the start game menu and the settings menu.
	
	timeValsMenu.add(back,gbc);
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (RunWindow.height != null) {
				// The height variable will be null if a RunWindow variable has not yet been initialized.
				// That means that this conditional is checking to see if a RunWindow has been initialized,
				// which would mean that this window is being called by the settings menu, not the menu 
				// at the start of the game.
				
				timeValsMenu.dispose();
				RunWindow.Settings();
				// To go back from this point, the button closes this new timed game menu and reopens the
				// settings menu.
			}
			else {
				// If this else block is run, it means that the back button is being pressed from the 
				// start game menu.
				
				timeValsMenu.dispose();
				RunWindow.startScreen();
				// If the user wants to go back to the start game menu, then this back button closes the
				// timed game menu and runs the startScreen() method to open another start game menu. While
				// it does this, the original main method from which the original startScreen() and then
				// this menu were called is still running a while loop checking the variable run. The user 
				// could theoretically go back and forth between this start timed game menu and the original
				// start screen as much as they want while still being in this same while loop.
				
			}
		}
	});
	
	gbc.gridx = 0;
	gbc.gridy = 8;
	gbc.gridwidth = 2;
	
	JButton info = new JButton("Timed Game Info");
	
	// This JFrame is a bit of experimentation in making an info box to explain the timed game. It uses 
	// JLabels and PNGs to explain how to initialize a game and how it works. It uses GridBagLayout to 
	// customize the placement of JLabels and images. Some of the JLabels also incorporate html to further
	// customize the placement of their messages.
	
	
	timeValsMenu.add(info,gbc);
	info.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFrame f = new JFrame();
			
			f.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc2 = new GridBagConstraints();
			
			gbc2.gridx = 0;
			gbc2.gridy = 0;
			gbc2.gridwidth = 1;
			// gridwidth is set to 1 for most of the components so that they fill one column.
			gbc2.insets = new Insets(10,0,10,0);
			// These insets are used to create some space between components. Only top and bottom values
			// are used because the images create enough space horizontally already.
			

			
			ImageIcon img1 = new ImageIcon(RunWindow.class.getResource("/firstAgainAgain.png"));
			
			JLabel input1 = new JLabel(img1);
			f.add(input1,gbc2);
			// This first image in the window. With weight values of 0 it stays centered in the 0,0 row and
			// column.
			
			
			gbc2.gridx = 1;
			gbc2.gridy = 0;


			
			
			f.add(new JLabel("--------->"),gbc2);
			
			gbc2.gridx = 2;
			gbc2.gridy = 0;

			
			ImageIcon img2 = new ImageIcon(RunWindow.class.getResource("/second.png"));
			JLabel clock1 = new JLabel(img2);
			f.add(clock1,gbc2);
			
			
			gbc2.weightx = 1;
			// the weightx is set to 1 so that these components leave some room next to the edges of the 
			// window.
			gbc2.gridx = 0;
			gbc2.gridy = 2;
			
			f.add(new JLabel("^^^^^^"),gbc2);
			f.add(new JLabel("<html><br/><br/>The game clock starts at the inputed amount.</html>"),gbc2);
			// This html incorporation is necessary to perform a new line in the JLabel text. Because we 
			// want the carrots above this text to be centered, we give the string its own label. In order
			// to ensure that this text is directly underneath the carrots, not separated by the insets,
			// it is set to the same gridy, but html is used to send it two lines down. This is used
			// also for the next line, just in the gridx = 2 column instead of gridx = 0 column.
			
			gbc2.gridx = 2;
			f.add(new JLabel("^^^^^^"),gbc2);
			f.add(new JLabel("<html><br/><br/>The timers are displayed in-game.</html>"),gbc2);
			
			gbc2.gridx = 0;
			gbc2.gridy = 4;
			f.add(new JLabel("<html>If no clock time is inputed, a non-timed game will start.</html>"),gbc2);

			gbc2.gridx = 2;
			f.add(new JLabel("<html>If a clock time is inputed but not an interval, then <br/> a timed game without intervals will start.</html>"),gbc2);

			
			
			gbc2.gridx = 0;
			gbc2.gridy = 6;
			gbc2.gridwidth = 3;
			// by setting the gridwidth to 3, this next label is free to take up all the space in the window.
			// its gridx is set to 0 because one gridx of gridwidth 3 takes up the space of all three gridx
			// columns which are currently set to gridwidth 1. This is all done to place this text in the 
			// center of the window at gridy = 6. 

			f.add(new JLabel("If a player's timer runs out before checkmate, that player loses and the game ends."),gbc2);

			gbc2.gridy = 8;
			// like the previous JLabel text, we want this button to be directly in the center of the 
			// window, only down by one more gridy row. Because GridBagConstraint.fill is at its default
			// value of NONE, the button will not fill up its entire row of width 3.
			JButton goBack = new JButton("Ok");
			
			f.add(goBack,gbc2);
			goBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					f.dispose();
					// This button doesn't need to open another start() menu because this info window acts
					// as a JOptionPane and doesn't close the original window. It only closes itself.
				}
			});
			
			

			
			f.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 400,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - 175);


			

			
			f.pack();
			f.setSize(800,400);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setVisible(true);
		}
	});
	
	
	
	
	timeValsMenu.pack();
	timeValsMenu.setSize(300, 300);
	timeValsMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	timeValsMenu.setVisible(true);
	// This is all regular JFrame initialization
	
}


public void doInterval() {
	// This method handles the interval inputted by the user for the timed game. If the timed game is running,
	// that means the user inputted an integer for the interval. Even if the user inputted 0, meaning 
	// there is no effective interval being used for the game, this method still runs, just adding 0 to 
	// the timers for black and white. This may be optimized in the future.
	
	if (RunWindow.turn) {
		timeBlack += interval;
	}
	else timeWhite += interval;
}


public Integer textFieldHandler(String k) {
	Integer ret;
	try {
		if (k.length() == 0) {
			return -1;
		}
		ret = Integer.valueOf(k);
	} catch (NumberFormatException e){
		return null;
	}
	
	// This method initializes an Integer and, inside a try catch, attempts to convert the String input
	// k to an Integer. If the try catches an error, then the method returns null.
	
	if (ret < 1) return null;
	// If the user inputs a timer that is less than one minute, then return null.
	
	return ret * 60;
	// This final return is run when the checks above determine the String k can be converted to a legal
	// integer, in which case it is returned * 60 because the user inputed the time in minutes and it must
	// be converted to seconds to count down.
}

public Integer intervalHandler(String k) {

	if (k.length() == 0) return 0;
	// if the interval was left empty then just use zero.
	Integer ret;
	try {
		ret = Integer.valueOf(k);
	} catch (NumberFormatException e) {
		return null;
	}
	
	// This method is the same as textFieldHandler() except that the final conditional checking the value
	// of the interval checks whether the inputted interval is less than 0, in which case it returns null. 
	// Unlike the timer, the interval can be set to zero. After that, just like textFieldHandler(), this
	// method returns the inputed string converted to an Integer.
	
	if (ret < 0) {

		return null;
	}
	return ret;
}









}