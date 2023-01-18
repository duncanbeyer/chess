package rungame;
import java.awt.*; //this import is used to get the size of the screen so the canvas size is scaled

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// stdlib.StdDraw is used by copying the class into the package. the import was screwed.
import java.util.ArrayList;
import javax.swing.*;

public class RunWindow {
	
	static Integer height;
	static boolean smart = true; // this boolean is used to toggle the smart suggestions. It starts true
	// because the smart suggestions start as on.
	static boolean turn = true; // this boolean starts as true and signifies which turn it is. when this 
	// value is true it is white turn and when it is set to false it is black turn
	static Integer pressedX;
	static Integer pressedY;
	static engine theEngine = new engine();
	static boolean isNums; // if this is set to true, then the board will fill with alphanumeric coordinates
	// and the spaces will continue to be updated with them.
	static ArrayList<int[]> highlightedYellow; 
	static ArrayList<int[]> highlightedRed;
	static ArrayList<int[]> highlightedBlue;
	// These three array lists are used for the smart suggestions. HighlightedYellow will be filled with all 
	// possible moves from the selected piece to empty spaces and highlightedRed will be filled with all 
	// such moves to spaces occupied by opposing pieces (other than the king). highlightedBlue runs after
	// those two, scanning every pair in both lists to see if either result in check. If they result in 
	// check for the opposing King, they will be removed from highlightedYellow or highlightedRed and added
	// to highlightedBlue. If the move would result in check for the piece's own king, then the move is 
	// removed from its list and it is not added to highlightedBlue. This way, when smart suggestions are 
	// active, all legal spaces will be highlighted either yellow, red, or blue when the player selects a 
	// piece.
	static boolean run = false; // this boolean is initialized to false and is only used while the opening
	// menu is running to wait for the "start game" button to be pressed.
	static boolean over = false; // this boolean is initialized to false and is only set to true when 
	// isCheckMate detects that the game is over. This variable being true trips the conditional that 
	// breaks the while loop the game runs in.
	static Stopwatch gameTimer = new Stopwatch();
	
	
	static String moveList; // This String is updated every turn with the current list of moves made in the 
	// game. the String arrays algebraic and first are used to help the update methods.
	static String[] algebraic = new String[] {"a","b","c","d","e","f","g","h"};
	static String[] first = new String[] {"","R","N","B","Q","K"};
	
	static boolean playerColor; // This boolean is initialized if the player has started a game against a 
	// computer, it will be true if the player is playing as white and false if the player is black
	
	static Integer enPassantX; // this variable is set to x coordinate of a pawn if the pawn has 
	// put itself in a position to be takes by en passant. it will be reset to null or a new value one turn
	// later.
	
	static boolean canKingCastleWhite;
	static boolean canKingCastleBlack;
	static boolean canRookCastleWhiteLeft;
	static boolean canRookCastleBlackLeft;
	static boolean canRookCastleWhiteRight;
	static boolean canRookCastleBlackRight;
	// These booleans are used to keep track of whether the Kings and rooks do or do not have
	// the ability to castle
	
	static boolean isCheck = false;
	
	public RunWindow() {
		
		makeCanvas(); //creates the game window and draws the squares for the board
		startSymbols();
		isNums = false; // this is initialized to false but if fillNums() is called then it will be set to
		// true and the spaces will be properly updates.
		// fillNums(); //optional method that places alphanumeric designations in each square.
		// fillNums() might be improved on to become an in-game toggle.
		
		
		highlightedYellow = new ArrayList<int[]>();
		highlightedRed = new ArrayList<int[]>();
		highlightedBlue = new ArrayList<int[]>();
		moveList = new String();
		canKingCastleBlack = true;
		canKingCastleWhite = true;
		canRookCastleBlackLeft = true;
		canRookCastleWhiteLeft = true;
		canRookCastleBlackRight = true;
		canRookCastleWhiteRight = true;
		
	}
	
	
	
	
	public static void makeCanvas() {
		
		// The purpose of this method is to draw the board at its starting position for the game. From
		// this state, it can be updated as the game progresses
		
		// This method measures the size of the screen, creates the canvas size according to a ratio of 
		// the size of the screen, then calls cover() on every coordinate to draw the spaces and outlines.
		// Then it sets the title of the window to "Chess" and initializes the turn signal by printing
		// "White Turn" at the top. Then is calls replaceBottomBox() which creates the box at the bottom 
		// of the side bar that says if someone is in check. Then it draws another box with text above it
		// for the options menu. This box is never updated so it only needs to be drawn once here.


		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		height = (int)size.getHeight();
		height = height / 2; //sets the height to the size of the sides of the game board
		StdDraw.setCanvasSize(height + (height/2), height);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
					cover(i,j);
			}
		}
		
		// the i iterator represents the column while j represents the row, for each space 
		// cover(i,j) is called. cover decides what color to put on the space and puts a black border around
		// it for visibility.
		
		StdDraw.setTitle("Chess");
		
		StdDraw.text(.833, .92, "White Turn");
		

		replaceBottomBox();
		
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.filledRectangle(.8345, .3, .15,.08);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.rectangle(.8345, .3, .15,.08);
		StdDraw.text(.8345, .3, "Options Menu");
		
	}
	
	
	public static void addMove(Integer oldX, Integer oldY, Integer newX, Integer newY, boolean check, boolean take, double castle) {
		
		piece temp = theEngine.getPiece(oldX,oldY);
		
		if (castle == 1) { // the move was a castle to the left
			moveList = moveList + "0-0-0";
		}
		else if (castle == 2) { // the move was a castle to the right
			moveList = moveList + "0-0";
		}
		
		

		else if (take) {
			if (temp.type == 0) {
				moveList = moveList + algebraic[oldX] + "x" + algebraic[newX] + (newY+1);
			}
			else {
				moveList = moveList + first[(int)temp.type] + "x" + algebraic[newX] + (newY+1);
			}
		}
		else {
			moveList = moveList + first[(int) temp.type] + algebraic[newX] + (newY+1);
		}
		
		if (check) {
			moveList = moveList + "+ ";
		}
		else moveList = moveList + " ";
	}
	
	
	
	public static void fillNums() {
		String[] letters = new String[]{"a","b","c","d","e","f","g","h"};
		for (int i = 0; i <= 7;i++) {
			for (Integer j = 1;j <= 8;j++) {
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.filledRectangle(.015 + (i*.0833), .1+((j-1)*.125),.015,.019);
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(.015 + (i*.0833), .1+((j-1)*.125), letters[i]+Integer.toString(j));
				}
			} 
		// every i increment becomes one full column.  Each i has its own for loop that increments through j,
		// using a similar concept to the makeCanvas squares where the increments multiple by the length of
		// a square, .125.  In this case I have decreased the offset so that the alphanumerators appear
		// at the bottom left of each square. Also I set the pen to black at the beginning of the method so 
		// that they are visible on both color backgrounds.
		isNums = true; // whenever this method is called, it means going from a state where isNums is
		// inactive to going to a state where it is active. When it is active, the coordinates on the board
		// will be updated.
		
		// This fills the numbers column-major because it is only drawing on the window and
		// not accessing data so it doesn't matter.
		}
	
	
	
	
	
	
	public static void replaceNum(Integer x, Integer y) {
		String[] letters = new String[]{"a","b","c","d","e","f","g","h"};
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(.015 + (x*.0833), .1 + ((y)*.125), .015, .019);
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.text(.015 + (x*.0833), .1+((y)*.125), letters[x]+Integer.toString(y+1));
		
		// the filledRectangle and text methods of StdDraw are taken closely from the fillNums method, only 
		// they are used singularly in this method.
	}
	
	
	public static void removeNums() {
		// this method is called when the user wants to remove the alphanumeric coordinates from the corners
		// of the spaces. After it does so, it sets isNums to false so that they do not update as moves are 
		// made.
		
		for (int i = 0; i < 8; i++) { 
			// this loop represents (in my head) the bottom row, each starting at y=0 and going up through
			// the second for loop.
			for (int k = 0; k < 8; k++) {
				if (theEngine.isNull(i, k)) cover(i,k); // if the space is null just cover it.
				else {
					cover(i,k);
					updateSymbol(i,k);
				}
			}
		}
		isNums = false;

		if (pressedX != null) {
			selected(pressedX,pressedY);
		}
	}
	
	
	public static void doNums() {
		// this method is called when the user toggles the numbers on or off
		
		if (isNums) removeNums();
		else fillNums();
		doSleep();
	}
	
	
	public static void yourTurn() {
		turn = !turn;
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.filledRectangle(.833, .92, .07 , 0.05);
		StdDraw.setPenColor(StdDraw.BLACK);
		if (turn) StdDraw.text(.833, .92, "White Turn");
		else StdDraw.text(.833, .92, "Black Turn");

	}
	
	
	public static void startSymbols() {
		for (int i = 0;i < 8;i++) { //create 8 pawn icons in their starting positions.
			StdDraw.picture(i*.0833 + .0416, .125 + .0625, "white pawn.png");
		} // .0416 is the offset to put the destination in the center of a tile, then .0833*i determines
		// how many tiles to the right to travel.  The same for y but larger because of the stretched board
		for (int i = 0;i < 8;i++) {
			StdDraw.picture(i*.0833 + .0416, .75 + .0625, "black pawn.png");
		}
		StdDraw.picture(0.0416, 0.0625, "white rook.png");
		StdDraw.picture(0.1249, 0.0625, "white knight.png");
		StdDraw.picture(0.2082, 0.0625, "white bishop.png");
		StdDraw.picture(0.2915, 0.0625, "white queen.png");
		StdDraw.picture(0.3748, 0.0625, "white king.png");
		StdDraw.picture(0.4581, 0.0625, "white bishop.png");
		StdDraw.picture(0.5414, 0.0625, "white knight.png");
		StdDraw.picture(0.6247, 0.0625, "white rook.png");
		StdDraw.picture(0.0416, 0.9375, "black rook.png");
		StdDraw.picture(0.1249, 0.9375, "black knight.png");
		StdDraw.picture(0.2082, 0.9375, "black bishop.png");
		StdDraw.picture(0.2915, 0.9375, "black black queen.png"); //the file has a typo i just worked around it
		StdDraw.picture(0.3748, 0.9375, "black king.png");
		StdDraw.picture(0.4581, 0.9375, "black bishop.png");
		StdDraw.picture(0.5414, 0.9375, "black knight.png");
		StdDraw.picture(0.6247, 0.9375, "black rook.png");
		
	}
	
	
	
	
	
	public static void cover(int x, int y) {
		
		// used to cover over spaces when pieces are moved. It is first called on the space that is being
		// moved to to get rid of the old icon, then once a new icon has been added there this method 
		// is called again to cover over the old icon on the space that is being moved from.
		
		if (Math.abs((x % 2)-(y % 2)) == 1) {
			// if both x and y are odd or both x and y are even then fill the space with cyan. if they are 
			// alternating either way then fill the space with white.
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.filledRectangle(x*0.0833 + 0.0416, y*0.125 + 0.0625, .0416 , 0.0625);
		} 
		else {
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.filledRectangle(x*0.0833 + 0.0416, y*0.125 + 0.0625, .0416 , 0.0625);
		}
		coverHollow(x,y); // coverHollow puts a black outline around the space
	}
	
	
	
	public static void coverHollow(int x, int y) {
		

			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.rectangle(x*0.0833 + 0.0416, y*0.125 + 0.0625, .0416 , 0.0625);

	}
	
	
	public static void replaceBottomBox() {
		
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.filledRectangle(.8345, .1, .15,.08);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.rectangle(.8345, .1, .15,.08);
		
	}
	
	
	
	
	public static void selectPawn(int x, int y, boolean itsColor) {
		// the purpose of this method is to highlight the possible moves for the selected pawn.
		// the x and y ints are coordinates and temp is the color of the piece there.
		
		highlightedYellow = theEngine.pawnAroundNull(x,y,itsColor);		
		highlightedRed = theEngine.pawnAroundTarget(x,y,itsColor);
		
		// these two arraylists contain coordinates to be highlighted yellow and red.
		// en passant moves are stored in highlightedRed
		highlightBlue();
		highlightYellow();
		highlightRed();
		
	}
	
	
	public static void selectRook(int x, int y, boolean itsColor) {
		
		// the purpose of this method is to highlight the possible moves for the selected rook.
		// the x and y ints are coordinates and temp is the color of the piece there.
			
		highlightedYellow = theEngine.rookAroundNull(x,y,itsColor);		
		highlightedRed = theEngine.rookAroundTarget(x,y,itsColor);
			
		// these two arraylists contain coordinates to be highlighted yellow and red.
		
		highlightBlue();
		highlightYellow();
		highlightRed();
		
	}
	
	public static void selectKnight(int x, int y, boolean itsColor) {
		
		// the purpose of this method is to highlight the possible moves for the selected knight.
		
		highlightedYellow = theEngine.knightAroundNull(x,y,itsColor);
		highlightedRed = theEngine.knightAroundTarget(x, y, itsColor);
		
		highlightBlue();
		highlightYellow();
		highlightRed();
		
	}
	
	public static void selectBishop(int x, int y, boolean itsColor) {
		
		// the purpose of this method is to highlight the possible moves for the selected bishop.

		
		highlightedYellow = theEngine.bishopAroundNull(x,y,itsColor);
		highlightedRed = theEngine.bishopAroundTarget(x,y,itsColor);
		
		highlightBlue();
		highlightYellow();
		highlightRed();
	}
	
	public static void selectQueen(int x, int y, boolean itsColor) {
		
		// the purpose of this method is to highlight the possible moves for the selected queen.

		
		highlightedYellow = theEngine.queenAroundNull(x,y,itsColor);
		highlightedRed = theEngine.queenAroundTarget(x,y,itsColor);
		
		highlightBlue();
		highlightYellow();
		highlightRed();
	}
	
	




	public static void selectKing(int x, int y, boolean itsColor) {
		
		highlightedYellow = theEngine.kingAroundNull(x,y,itsColor);
		highlightedRed = theEngine.kingAroundTarget(x,y,itsColor);
		
		highlightYellow();
		highlightRed();
	}
	
	
public static void highlightBlue() {
		
		// the purpose of this method is to scan the highlightedYellow and highlightedRed arraylists
		// and find any pairs that would result in putting the opposing king in check.
		
		int tempX;
		int tempY;
		double tempCheck;
		for (int i = 0; i < highlightedYellow.size();i++) {
			tempX = highlightedYellow.get(i)[0];
			tempY = highlightedYellow.get(i)[1];
			tempCheck = theEngine.isCheck(pressedX, pressedY, tempX, tempY);


			if (tempCheck != 1) { // if the pair results in a king being in check
				if (turn && tempCheck == 2) { // if a white piece is putting the white king in check
					highlightedYellow.remove(i); // then remove the possibility from the highlights
					i--; // must decrement by 1 in order to not skip the next pair
				}
				
				else if (turn) { // if a white piece is putting the black king in check
					highlightedBlue.add(new int[] {tempX,tempY}); // move the pair to highlightedBlue
					highlightedYellow.remove(i);					
					i--;
				}
				
				else if (!turn && tempCheck == 3) {
					highlightedYellow.remove(i); // then remove the possibility from the highlights
					i--; // must decrement by 1 in order to not skip the next pair
				}
				
				else if (!turn) { // if a black piece is putting the white king in check
					highlightedBlue.add(new int[] {tempX,tempY}); // move the pair to highlightedBlue
					highlightedYellow.remove(i);
					i--;
				}
			}
		}
		
		for (int i = 0; i < highlightedRed.size();i++) { // we can run the same loop on the highlightedRed
			tempX = highlightedRed.get(i)[0]; // arrayList
			tempY = highlightedRed.get(i)[1];

			tempCheck = theEngine.isCheck(pressedX, pressedY, tempX, tempY);


			if (tempCheck != 1) { // if the pair results in a king being in check
				if (turn && tempCheck == 2) { // if a white piece is putting the white king in check
					highlightedRed.remove(i); // then remove the possibility from the highlights
					i--; // must decrement by 1 in order to not skip the next pair
				}
				else if (turn) { // if a white piece is putting the black king in check
					highlightedBlue.add(new int[] {tempX,tempY}); // move the pair to highlightedBlue
					highlightedRed.remove(i);					
					i--;
				}
				else if (!turn && tempCheck == 3) {

					highlightedRed.remove(i); // then remove the possibility from the highlights
					i--; // must decrement by 1 in order to not skip the next pair
				}
				else if (!turn) { // if a black piece is putting the white king in check
					highlightedBlue.add(new int[] {tempX,tempY}); // move the pair to highlightedBlue
					highlightedRed.remove(i);					
					i--;
				}
			}
		}
		
		// this next section draws over the final coordinate pairs in blue
		StdDraw.setPenColor(StdDraw.BLUE);
		for (int i = 0;i < highlightedBlue.size();i++) {
			tempX = highlightedBlue.get(i)[0];
			tempY = highlightedBlue.get(i)[1];
			// tempX and tempY is a pair retrieved from highlightYellow array list
			StdDraw.filledRectangle(tempX*0.0833 + 0.0416, tempY*0.125 + 0.0625, .0416 , 0.0625);
			if (!theEngine.isNull(tempX, tempY)) updateSymbol(tempX,tempY);
			coverHollow(tempX,tempY); //this method clarifies the individual tiles by outlining them in black
			StdDraw.setPenColor(StdDraw.BLUE); // coverHollow changes the pen color so we must change it back
			if (isNums) {
				replaceNum(tempX,tempY);
				StdDraw.setPenColor(StdDraw.BLUE);
			}
		}
		
	}
	
	
	
	public static void highlightYellow() {
		// this method uses pairs of coordinates pulled from arraylist highlightedYellow and the method
		// iterates through them and highlights the spaces yellow.
		
		StdDraw.setPenColor(StdDraw.YELLOW);
		int tempX;
		int tempY;
		for (int i = 0;i < highlightedYellow.size();i++) {
			tempX = highlightedYellow.get(i)[0];
			tempY = highlightedYellow.get(i)[1];
			if (!theEngine.isNull(tempX, tempY)) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.filledRectangle(tempX*0.0833 + 0.0416, tempY*0.125 + 0.0625, .0416 , 0.0625);
				coverHollow(tempX,tempY); //this method clarifies the individual tiles by outlining them in black
				StdDraw.setPenColor(StdDraw.YELLOW); // coverHollow changes the pen color so we must change it back
				if (isNums) {
					replaceNum(tempX,tempY);
					StdDraw.setPenColor(StdDraw.YELLOW);
				}
				updateSymbol(tempX,tempY);
				continue;
			}

			// tempX and tempY is a pair retrieved from highlightYellow arraylist
			StdDraw.filledRectangle(tempX*0.0833 + 0.0416, tempY*0.125 + 0.0625, .0416 , 0.0625);
			coverHollow(tempX,tempY); //this method clarifies the individual tiles by outlining them in black
			StdDraw.setPenColor(StdDraw.YELLOW); // coverHollow changes the pen color so we must change it back
			if (isNums) {
				replaceNum(tempX,tempY);
				StdDraw.setPenColor(StdDraw.YELLOW);
			}
		}
	}
	
	
	public static void highlightRed() {
		// this method uses pairs of coordinates pulled from arraylist highlightedRed and the method
		// iterates through them and highlights the spaces red and replaced the icon on top of it.
		
		
		StdDraw.setPenColor(StdDraw.RED);
		Integer tempX;
		Integer tempY;
		for (int i = 0;i < highlightedRed.size();i++) {
			tempX = highlightedRed.get(i)[0];
			tempY = highlightedRed.get(i)[1];
			

			// tempX and tempY is a pair retrieved from highlightRed arraylist
			StdDraw.filledRectangle(tempX*0.0833 + 0.0416, tempY*0.125 + 0.0625, .0416 , 0.0625);
			updateSymbol(tempX,tempY); // In the scenario that highlightRed is highlighting a move to an
			// en passant space, updateSymbol will recognize that the space is null and will do nothing.
			
			coverHollow(tempX,tempY);
			if (isNums) {
				replaceNum(tempX,tempY);
				StdDraw.setPenColor(StdDraw.RED);
				// coverHollow leaves the pen color as white, but we want it to be red for the rest of 
				// this for loop, so we change it back.
			}
			
		}
	}
	
	
	
	
	public static void selectAll(int x, int y) {
		// this method is intended to highlight all the spaces that a piece can legally move to.
		// It will check the type of the selected piece and call a separate method for the specific type.
		// If a potential move is to an empty space the space will be yellow, if the potential move is to
		// take a piece the space will be red.
		
		piece temp = theEngine.getPiece(x, y);
		if (temp.type == 0) selectPawn(x,y,temp.color);
		else if (temp.type == 1) selectRook(x,y,temp.color);
		else if (temp.type == 2) selectKnight(x,y,temp.color);
		else if (temp.type == 3) selectBishop(x,y,temp.color);
		else if (temp.type == 4) selectQueen(x,y,temp.color);
		else {
			selectKing(x,y,temp.color);
		}
	}
	
	public static void unselectAll() {
		
		// this method goes through the highlightedYellow and highlightedRed arraylists are restores
		// them to their original tile color. For the red tiles it also replaces the icons.
		
		while (highlightedYellow.size() > 0) { // This loop iterates through the arraylist of yellow spaces
			cover(highlightedYellow.get(0)[0],highlightedYellow.get(0)[1]);
			updateSymbol(highlightedYellow.get(0)[0],highlightedYellow.get(0)[1]);
			// it covers the spaces properly by calling cover() on the coordinates
			if (isNums) replaceNum(highlightedYellow.get(0)[0],highlightedYellow.get(0)[1]);
			// If isNums then it replaces the coordinates
			highlightedYellow.remove(0);
			// Lastly, it removes the pair from the arraylist
		}
		while (highlightedRed.size() > 0) {
			cover(highlightedRed.get(0)[0],highlightedRed.get(0)[1]);
			if (theEngine.getPiece(highlightedRed.get(0)[0],highlightedRed.get(0)[1]) != null) {
				// this conditional checks for the case when the space is used for an en passant.
				updateSymbol(highlightedRed.get(0)[0],highlightedRed.get(0)[1]);
			}
			if (isNums) replaceNum(highlightedRed.get(0)[0],highlightedRed.get(0)[1]);
			highlightedRed.remove(0);
		}
		while (highlightedBlue.size() > 0) {
			cover(highlightedBlue.get(0)[0],highlightedBlue.get(0)[1]);
			if (!theEngine.isNull(highlightedBlue.get(0)[0],highlightedBlue.get(0)[1])) {
				updateSymbol(highlightedBlue.get(0)[0],highlightedBlue.get(0)[1]);
			}
			if (isNums) replaceNum(highlightedBlue.get(0)[0],highlightedBlue.get(0)[1]);
			highlightedBlue.remove(0);
		}
	}
	
	
	
	public static void unSelect(int x, int y, boolean save) { 
		// x and y coordinates, boolean decides whether to redraw the symbol over top
		//
		cover(x,y);
		if (save) updateSymbol(x,y);
		if (smart)unselectAll(); // removes highlights from possible moves
		if (isNums) replaceNum(x,y);
	}
	
	
	public static void selected(int x, int y) {
		// This method is used to make visible the space that has been legally selected by the player.
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.filledRectangle(x*0.0833 + 0.0416, y*0.125 + 0.0625, .0416 , 0.0625);
		coverHollow(x,y);
		updateSymbol(x,y);
		if (isNums) replaceNum(x,y); // if isNums, then the coordinate in the corner must be updated.
		if (smart) selectAll(x,y); //highlights possibe moves based on the selected piece
		// turns the background of the selected space yellow to highlight it.
	}
	
	public static Integer convertX(double input) {
		// this method converts a precise mouse input value between 0 and 1 to a board coordinate 0-7.
		for (int i = 0;i < 9;i++) {
			if (input < (.083*i))
				return i-1;
		}
		return 0;
	}
	
	
	
	
	public static Integer convertY(double input) {
		// the reason there is a separate convertY method is that the rectangular board requires a conversion
		// based on 2/3 the height, which is x, and a conversion purely based on height (which is this y)
		for (int i = 0;i < 9;i++) {
			if (input < (.125*i))
				return i-1;
		}
		return 0;
	}
	
	
	
	
	
	public static void doSleep() {
		// this little method is separate in order to make the main method easier to read, as its call can
		// be copy pasted and read more easily that the try catch statement.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {} // necessary to use the sleep
	}
	
	
	
	
	
	public static void updateSymbol(int newX, int newY) {
		
		// This was originally part of the updatePiece() method but was made independent so that it could
		// be reused by the piece select method.
		
		piece temp = theEngine.getPiece(newX,newY); // temporarily saves the piece to get the color
		// and type from it
		
		if (temp == null) return; // This is only tripped when an en passant space is sent here
	
		if (temp.color) { // if it is a white piece
			if (temp.type == 0) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "white pawn.png");
			}
			else if (temp.type == 1) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "white rook.png");
			}
			else if (temp.type == 2) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "white knight.png");
			}
			else if (temp.type == 3) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "white bishop.png");
			}
			else if (temp.type == 4) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "white queen.png");
			}
			else if (temp.type == 5) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "white king.png");
			}
		}
		else { // if it is a black piece
			if (temp.type == 0) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "black pawn.png");
			}
			else if (temp.type == 1) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "black rook.png");
			}
			else if (temp.type == 2) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "black knight.png");
			}
			else if (temp.type == 3) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "black bishop.png");
			}
			else if (temp.type == 4) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "black black queen.png");
			}
			else if (temp.type == 5) {
				StdDraw.picture(0.0416 + 0.0833*newX, 0.0625 + 0.125*newY, "black king.png");
			}
		}
	}
	
	
	public static boolean canCastle(int x, int y) {
		
		if (x == 0 && y == 0) {
			return (canKingCastleWhite && canRookCastleWhiteLeft);
		}
		if (x == 7 && y == 0) {
			return (canKingCastleWhite && canRookCastleWhiteRight);
		}
		if (x == 0 && y == 7) {
			return (canKingCastleBlack && canRookCastleBlackLeft);
		}
		if (x == 7 && y == 7) {
			return (canKingCastleBlack && canRookCastleBlackLeft);
		}
		return false;
		
		
		
	}
	
	
	
	
	
	public static void updatePiece(int oldX, int oldY, int newX, int newY) {
		// this method is only called after the move has been deemed completely legal. First the move is 
		// performed inside the engine, then the gui is updated.
		
		if (theEngine.getPiece(oldX, oldY).type == 5 && (Math.abs(newX-oldX) > 1)) {
			// If the piece moving is in the white  King's original location, the white king
			// is still able to castle, and the piece is moving more than one space
			// If this conditional is true, then we know the move is a castle
			
			if (newX == 2) { // If the king is castling to the left
				
				theEngine.move(0,newY,3,newY); // move the rook into its new space in the engine
				theEngine.move(oldX, oldY, newX+2, newY); // move the king to its castle

				cover(0,newY); // cover the space the rook moved from
				cover(oldX,oldY); // places an empty space (properly colored) on top of the old space
				
				updateSymbol(newX+2,newY); //places the new symbol over the castle
				updateSymbol(3,newY); // replace the symbol for the rook
				
				if (isNums) {
					replaceNum(newX+2,newY);
					replaceNum(newX,newY);
				}
				if (theEngine.getPiece(newX+2, newY).color) {
					canKingCastleWhite = false; 
					canRookCastleWhiteLeft = false;
					}
				
				else {
					canKingCastleBlack = false; 
					canRookCastleBlackLeft = false;
					}

			}
			else { // If the king is castling to the right
				
				
				// If the king is castling to the right
				theEngine.move(7,newY,5,newY); // move the rook into its new space in the engine
				theEngine.move(oldX, oldY, newX-1, newY); // move the king to its castle

				cover(7,newY); // cover the space the rook moved from
				cover(oldX,oldY); // places an empty space (properly colored) on top of the old space
				
				updateSymbol(newX-1,newY); //places the new symbol over the castle
				updateSymbol(5,newY); // replace the symbol for the rook
				
				if (isNums) {
					replaceNum(newX-1,newY);
					replaceNum(newX,newY);
				}
			
				if (theEngine.getPiece(newX-1, newY).color) {
					canKingCastleWhite = false; 
					canRookCastleWhiteRight = false;
				}
				
				else {
					canKingCastleBlack = false; 
					canRookCastleBlackRight = false;
				}

			}
			
		}
		
		
		
		
		else if (enPassantX != null) { // If it is possible that an en passant may be performed this turn
			if (theEngine.getPiece(oldX, oldY).type == 0 && newX == enPassantX) { 
				// If the piece moving is a pawn and its new space has the same x value as enPassantX
				if (theEngine.getPiece(oldX, oldY).color && theEngine.getPiece(newX, newY-1) != null) {
					// If the old piece is white and the space underneath it is not null
					if (!theEngine.getPiece(newX, newY-1).color && theEngine.getPiece(newX, newY-1).type == 0 && theEngine.getPiece(newX, newY) == null) {
					// If the space underneath the new space is black and it is a pawn and the new space is null
						theEngine.delete(newX, newY-1);
						cover(newX,newY-1);
					
					}
				}
				else if (!theEngine.getPiece(oldX, oldY).color && theEngine.getPiece(newX, newY+1) != null) {
					if (theEngine.getPiece(newX, newY+1).color && theEngine.getPiece(newX, newY+1).type == 0 && theEngine.getPiece(newX, newY) == null) {
					theEngine.delete(newX, newY+1);
					cover(newX,newY+1);
					}
				}
			}
		}
				
		if (theEngine.isNull(oldX, oldY)) {
			return;
		}
		

		
		theEngine.move(oldX, oldY, newX, newY); // this changes the values inside the actual engine board
		cover(oldX,oldY); // places an empty space (properly colored) on top of the old space
		cover(newX,newY);// places an empty space (properly colored) on top of the new space
		updateSymbol(newX,newY); //places the new symbol over the target space
		if (isNums) {
			replaceNum(newX,newY);
			replaceNum(oldX,oldY);
		}
		
		
		if (theEngine.getPiece(newX, newY).type == 0 && Math.abs(oldY-newY) == 2) enPassantX = newX;
		else if (enPassantX != null) enPassantX = null;
	}
	
	
	public static void toggleSmartSuggestions() {
		if (smart && pressedX != null) {
			unselectAll();
		}
		else if (!smart && pressedX != null) {
			selectAll(pressedX,pressedY);
		}
		smart = !smart;
	}
	
	
	public static void takesTimer(int x) {
		
		// This method is called from the stopwatch which is running in the background constantly if the
		// game was initialized with timed = true. Once every second, the stopwatch sends an int between
		// 0 and 600 representing how much time is left on the specific timer, counting down. The stopwatch
		// itself knows whose turn it is so it knows which stopwatch to count down with already. This 
		// method simply uses the turn boolean to determine whether to update the visual of the timer
		// for the white side or the black side, then uses int math to display the updated value every
		// second.

		
		
		
        if (turn) { // if it is the white turn
    		StdDraw.setPenColor(StdDraw.WHITE);
    		StdDraw.filledRectangle(.91725, .8, .04,.04);
    		// first we cover the old time using a white filled rectangle
    		StdDraw.setPenColor(StdDraw.BLACK);
    		StdDraw.rectangle(.91725, .8, .04,.04);
    		// second we update the outline of the timer box using a black rectangle
    		if (x % 60 >= 10) {
    			StdDraw.text(.91725, .8, (x / 60) + ":" + (x % 60));
    		}
    		else {
    			// this if/else conditional exists solely so that when the timer reaches a whole minute such
    			// as 9 minutes 0 and seconds left, 8 minutes 0 and seconds left, etc, it displays two 
    			// zeroes instead of the one which x % 60 would return.
    			StdDraw.text(.91725, .8, (x / 60) + ":0" + (x % 60));
    		}
        }
        else { // the same code is used but just drawn a little to the left because it is the black timer
    		StdDraw.setPenColor(StdDraw.WHITE);
    		StdDraw.filledRectangle(.75175, .8, .04,.04);
    		StdDraw.setPenColor(StdDraw.BLACK);
    		StdDraw.rectangle(.75175, .8, .04,.04);
    		if (x % 60 >= 10) {
    			StdDraw.text(.75175, .8, (x / 60) + ":" + (x % 60));
    		}
    		else {
    			StdDraw.text(.75175, .8, (x / 60) + ":0" + (x % 60));
    		}
        }
         
	}
	
	public void initialTimer(int x) {
		
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.filledRectangle(.91725, .8, .04,.04);
		StdDraw.filledRectangle(.75175, .8, .04,.04);

		// first we cover the old time using a white filled rectangle
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.rectangle(.91725, .8, .04,.04);
		StdDraw.rectangle(.75175, .8, .04,.04);

		// second we update the outline of the timer box using a black rectangle
		if (x % 60 >= 10) {
			StdDraw.text(.91725, .8, (x / 60) + ":" + (x % 60));
			StdDraw.text(.75175, .8, (x / 60) + ":" + (x % 60));
		}
		else {
			// this if/else conditional exists solely so that when the timer reaches a whole minute such
			// as 9 minutes 0 and seconds left, 8 minutes 0 and seconds left, etc, it displays two 
			// zeroes instead of the one which x % 60 would return.
			StdDraw.text(.91725, .8, (x / 60) + ":0" + (x % 60));
			StdDraw.text(.75175, .8, (x / 60) + ":0" + (x % 60));

		}
		
		StdDraw.text(.91725, .7, "White Timer");
		StdDraw.text(.75125, .7, "Black Timer");


	}
	
	
	public static boolean isCheckMate() {
		
		// This method is called after a move takes place that is legal and results in one of the kings
		// being put in check. This method uses the existing detection methods of the engine to create 
		// ArrayLists containing coordinate pairs for possible moves for each piece on the board. It then
		// sends these arraylists to the helper method which performs isCheck on the move. If the result is
		// anything other than the color's own king being in check, it means that it is possible to leave
		// check, resulting in isCheckMate returning false.
		
		
		for (int i = 0;i < 8;i++) {
			for (int j = 0;j < 8;j++) {
				if (!theEngine.isNull(i, j)) {
					ArrayList<int[]> yellow = new ArrayList<int[]>();
					ArrayList<int[]> red = new ArrayList<int[]>();
					// yellow and red are used in place of the highlightedYellow and highlightedRed 
					// arraylists used by the smart select methods. the yellow arraylists contains pairs
					// of legal moves to null spaces and the red arraylists contain pairs of legal moves
					// to occupied spaces.
			
					if (theEngine.getPiece(i, j).type == 0 && theEngine.getPiece(i, j).color != turn) { 
						// if the piece is a pawn of the color that was just put in check
						yellow = theEngine.pawnAroundNull(i, j, theEngine.getPiece(i, j).color);
						red = theEngine.pawnAroundTarget(i, j, theEngine.getPiece(i, j).color);
						if (checkMateHelper(yellow,red,i,j)) return false;
					}
					else if (theEngine.getPiece(i, j).type == 1 && theEngine.getPiece(i, j).color != turn) { 
						// if the piece is a rook of the color that was just put in check
						yellow = theEngine.rookAroundNull(i, j, theEngine.getPiece(i, j).color);
						red = theEngine.rookAroundTarget(i, j, theEngine.getPiece(i, j).color);
						if (checkMateHelper(yellow,red,i,j)) return false;
					}
					else if (theEngine.getPiece(i, j).type == 2 && theEngine.getPiece(i, j).color != turn) { 
						// if the piece is a knight of the color that was just put in check
						yellow = theEngine.knightAroundNull(i, j, theEngine.getPiece(i, j).color);
						red = theEngine.knightAroundTarget(i, j, theEngine.getPiece(i, j).color);
						if (checkMateHelper(yellow,red,i,j)) return false;
					}
					else if (theEngine.getPiece(i, j).type == 3 && theEngine.getPiece(i, j).color != turn) { 
						// of the piece is a bishop of the color that was just put in check
						yellow = theEngine.bishopAroundNull(i, j, theEngine.getPiece(i, j).color);
						red = theEngine.bishopAroundTarget(i, j, theEngine.getPiece(i, j).color);
						if (checkMateHelper(yellow,red,i,j)) return false;
					}
					else if (theEngine.getPiece(i, j).type == 4 && theEngine.getPiece(i, j).color != turn) { 
						// if the piece is a queen of the color that was just put in check
						yellow = theEngine.queenAroundNull(i, j, theEngine.getPiece(i, j).color);
						red = theEngine.queenAroundTarget(i, j, theEngine.getPiece(i, j).color);
						if (checkMateHelper(yellow,red,i,j)) return false;
					}
					else if (theEngine.getPiece(i, j).type == 5 && theEngine.getPiece(i, j).color != turn) { 
						// if the piece is a king of the color that was just put in check
						yellow = theEngine.kingAroundNull(i, j, theEngine.getPiece(i, j).color);
						red = theEngine.kingAroundTarget(i, j, theEngine.getPiece(i, j).color);
						if (checkMateHelper(yellow,red,i,j)) return false;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean checkMateHelper(ArrayList<int[]> yellow, ArrayList<int[]> red, int oldX, int oldY) {

		// When this method is called we know that the piece at oldX, oldY is of the color that was just put
		// in check, we know that yellow is full of prospective null-space moves, and we know that
		// red is full of prospective moves to occupied spaces. This method iterates through both arraylists
		// to see if the moves would result in their color leaving check. If so, then true is immediately
		// returned, meaning that the gamestate is not checkmate. If this method gets through all of the 
		// pairs and finds none that leave check, then it returns false, and the parent isCheckMate method 
		// either moves to the next location or determines that the game is in checkmate.
		



		if (theEngine.getPiece(oldX, oldY).color) { // if the piece being checked is white
			while (yellow.size() > 0) {
				if (theEngine.isCheck(oldX,oldY,yellow.get(0)[0],yellow.get(0)[1]) != 2) return true;
				yellow.remove(0);
			}
			while (red.size() > 0) {
				if (theEngine.isCheck(oldX,oldY,red.get(0)[0],red.get(0)[1]) != 2) return true;
				red.remove(0);
			}
		}
		else { // else if the piece being checked is black
			while (yellow.size() > 0) {
				if (theEngine.isCheck(oldX,oldY,yellow.get(0)[0],yellow.get(0)[1]) != 3) return true;
				yellow.remove(0);
			}
			while (red.size() > 0) {
				if (theEngine.isCheck(oldX,oldY,red.get(0)[0],red.get(0)[1]) != 3) return true;
				red.remove(0);
			}
		}
		return false;
		
		// isCheck will return 1 if the move results in no check, 2 if it results in white check, and 3 if
		// it results in black check.
	}
	

	
	public static void endGame() {
		
		// This method is only called once isCheckMate returns true and the game is over. It fills the text
		// check update box with the winner and sets the over boolean to false. Because of that boolean,
		// the while loop in which the game runs will trip a conditional that stops the game from running.
		
		if (turn) {
			replaceBottomBox();
			StdDraw.text(.833, .1, "White Wins!");
		}
		else {
			replaceBottomBox();
			StdDraw.text(.833, .1, "Black Wins!");
		}
		over = true;
	}
	
	public static void timeExpired(boolean winner) {
		
		if (winner) {
			replaceBottomBox();
			StdDraw.text(.833, .1, "White Wins!");
		}
		else {
			replaceBottomBox();
			StdDraw.text(.833, .1, "Black Wins!");
		}
		
		over = true;

	}
	
	
	
	
	public static void mouseIsPressed() {
		
		// when the mouse is pressed, tempx is set to the x coordinate after it is converted to a board
		// location.  tempy is set to the y coordinate after being converted
		
		Integer tempx;
		Integer tempy;
		
		tempx = convertX(StdDraw.mouseX()); // tempx is the x coordinate that was just pressed
		tempy = convertY(StdDraw.mouseY()); // tempy is the y coordinate that was just pressed
		if (tempx == pressedX && tempy == pressedY) { 
			// if the coordinates you just pressed match the coordinates last pressed

			unSelect(pressedX,pressedY,true);
			// whenever a proposed move fails, the initial highlighted space must be deselected
			// right before the saved pressedX and pressedY values are reset.
			pressedX = null;
			pressedY = null;
			doSleep();
		}
		else if (pressedX == null && pressedY == null) { //if this is the initial tile selection
			if (theEngine.isNull(tempx,tempy)) { //if the space initially selected is null
				doSleep();
			}
			else if (theEngine.board[tempx][tempy].color == turn) {
				// if it is your turn and you initially select a piece that is your color
				pressedX = tempx;
				pressedY = tempy; //set the pressedX and pressedY to the piece you pressed and 
				// then wait for you to press another color
				selected(pressedX,pressedY);
				doSleep();
			}
			else { // this is the initial selection but you chose a piece that is not your color
				doSleep();
			}
		}
		else { // if this is not the initial selection and it is a new location selected
			
			if (theEngine.isNull(tempx,tempy)) { // if the target space selected is empty
				double legality = theEngine.isLegal(pressedX, pressedY, tempx, tempy);
				if (legality == 0) { // the move is invalid
					unSelect(pressedX,pressedY, true);
					pressedX = null;
					pressedY = null;
					doSleep();
				}
				else if (legality == 1) { //the move is valid and no kings are in check
					addMove(pressedX,pressedY,tempx,tempy,false,false,0);
					unSelect(pressedX,pressedY, false);
					updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
					// move the piece and it will update the appearance of the board
					pressedX = null;
					pressedY = null;
					gameTimer.doInterval();
					if (isCheck) isCheck = false;
					yourTurn();
					replaceBottomBox(); // these four lines cover a section of the
					// side bar back to white because the user may be coming out of check. If not then the
					// section will not be altered visually
					doSleep();
				}
				else if (legality == 2) {
					if (!turn) {
						addMove(pressedX,pressedY,tempx,tempy,true,false,0);
						replaceBottomBox();
						StdDraw.text(.833, .1, "White King in Check");
						unSelect(pressedX,pressedY, false);
						updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
						// move the piece and it will update the appearance of the board
						pressedX = null;
						pressedY = null;
						
						if (isCheckMate()) endGame();
						if (!isCheck) isCheck = true;
						gameTimer.doInterval();
						yourTurn();
						doSleep();
					}
					else {
						unSelect(pressedX,pressedY, true);
						pressedX = null;
						pressedY = null;
						doSleep();
					}
				}
				else if (legality == 3) {
					if (turn) {
						addMove(pressedX,pressedY,tempx,tempy,true,false,0);
						replaceBottomBox();
						StdDraw.text(.833, .1, "Black King in Check");
						updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
						// move the piece and it will update the appearance of the board
						unSelect(pressedX,pressedY, false);
						pressedX = null;
						pressedY = null;
						if (isCheckMate()) endGame();
						if (!isCheck) isCheck = true;

						gameTimer.doInterval();
						yourTurn();
						doSleep();
					}
					else {
						unSelect(pressedX,pressedY, true);
						pressedX = null;
						pressedY = null;
						doSleep();
					}
				}
				else if (legality == 4) {
					unSelect(pressedX,pressedY, true);
					pressedX = null;
					pressedY = null;
					doSleep();
				}
			}
			
			else if (theEngine.board[tempx][tempy].color == turn) { // if you targeted your own color
				if (canCastle(tempx,tempy)) { // if the booleans say the castle is possible
					double legality = theEngine.isLegal(pressedX, pressedY, tempx, tempy);
					if (legality == 1) { // if the castle is legal
						if (tempx == 7) { // if the castle is on the right
							addMove(pressedX,pressedY,tempx,tempy,false,false,2);
						}
						else { // if the castle is on the left
							addMove(pressedX,pressedY,tempx,tempy,false,false,1);
						}
						updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
						// move the piece and it will update the appearance of the board
						unSelect(pressedX,pressedY, false);
						pressedX = null;
						pressedY = null;
						gameTimer.doInterval();
						if (isCheck) isCheck = false;
						yourTurn();
						replaceBottomBox(); // these two lines cover a section of the
						// side bar back to white because the user may be coming out of check. If not then the
						// section will not be altered visually
						doSleep();
					}
					if (legality == 2) { // if it results in white king being in check
						if (turn) { // and it is white turn
							unSelect(pressedX,pressedY, true);
							pressedX = null;
							pressedY = null;
							doSleep();
						}
						else {
							if (tempx == 7) { // if the castle is on the right
								addMove(pressedX,pressedY,tempx,tempy,false,false,2);
							}
							else { // if the castle is on the left
								addMove(pressedX,pressedY,tempx,tempy,false,false,1);
							}							replaceBottomBox();
							StdDraw.text(.833, .1, "White King in Check");
							updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
							// move the piece and it will update the appearance of the board
							unSelect(pressedX,pressedY, false);
							pressedX = null;
							pressedY = null;
							if (isCheckMate()) endGame();
							if (!isCheck) isCheck = true;
							gameTimer.doInterval();
							yourTurn();
							doSleep();
						}
					}
					else if (legality == 3) {
						if (turn) {
							if (tempx == 7) { // if the castle is on the right
								addMove(pressedX,pressedY,tempx,tempy,false,false,2);
							}
							else { // if the castle is on the left
								addMove(pressedX,pressedY,tempx,tempy,false,false,1);
							}							replaceBottomBox();
							StdDraw.text(.833, .1, "Black King in Check");
							updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
							// move the piece and it will update the appearance of the board
							unSelect(pressedX,pressedY, false);
							pressedX = null;
							pressedY = null;
							if (isCheckMate()) endGame();
							if (!isCheck) isCheck = true;
							gameTimer.doInterval();
							yourTurn();
							doSleep();
						}
						else {
							unSelect(pressedX,pressedY, true);
							pressedX = null;
							pressedY = null;
							doSleep();
						}
					}
				}
				else {
					unSelect(pressedX,pressedY, true);
					pressedX = null;
					pressedY = null;
					doSleep();
				}
			}
			else { // if there is a piece of the other color in the targeted space
				double legality = theEngine.isLegal(pressedX, pressedY, tempx, tempy);
				if (legality == 0) { // the move is invalid
					unSelect(pressedX,pressedY, true);
					pressedX = null;
					pressedY = null;
					doSleep();
				}
				else if (legality == 1) { //the move is valid and no kings are in check
					addMove(pressedX,pressedY,tempx,tempy,false,true,0);
					updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
					// move the piece and it will update the appearance of the board
					unSelect(pressedX,pressedY, false);
					pressedX = null;
					pressedY = null;
					gameTimer.doInterval();
					if (isCheck) isCheck = false;
					yourTurn();
					replaceBottomBox(); // these two lines cover a section of the
					// side bar back to white because the user may be coming out of check. If not then the
					// section will not be altered visually
					doSleep();
				}
				else if (legality == 2) {
					if (!turn) {
						addMove(pressedX,pressedY,tempx,tempy,true,true,0);
						replaceBottomBox();
						StdDraw.text(.833, .1, "White King in Check");
						updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
						// move the piece and it will update the appearance of the board
						unSelect(pressedX,pressedY, false);
						pressedX = null;
						pressedY = null;
						if (isCheckMate()) endGame();
						if (!isCheck) isCheck = true;
						gameTimer.doInterval();
						yourTurn();
						doSleep();
					}
					else {
						unSelect(pressedX,pressedY, true);
						pressedX = null;
						pressedY = null;
						doSleep();
					}
				}
				else if (legality == 3) {
					if (turn) {
						addMove(pressedX,pressedY,tempx,tempy,true,true,0);
						replaceBottomBox();
						StdDraw.text(.833, .1, "Black King in Check");
						updatePiece(pressedX,pressedY,tempx,tempy); // updatePiece will call the engine to
						// move the piece and it will update the appearance of the board
						unSelect(pressedX,pressedY, false);
						pressedX = null;
						pressedY = null;
						if (isCheckMate()) endGame();
						if (!isCheck) isCheck = true;
						gameTimer.doInterval();
						yourTurn();
						doSleep();
					}
					else {
						unSelect(pressedX,pressedY, true);
						pressedX = null;
						pressedY = null;
						doSleep();
					}
				}
				else if (legality == 4) {
					unSelect(pressedX,pressedY, true);
					pressedX = null;
					pressedY = null;
					doSleep();
				}
			}
		}
	}
	
	
	
	public static void Settings() {
		
		
		JFrame settingsMenu = new JFrame();
		settingsMenu.setTitle("Settings");
		settingsMenu.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 200,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - 140);
		
		settingsMenu.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(10,10,10,10);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		
		JLabel coordinateImage = new JLabel(new ImageIcon(RunWindow.class.getResource("/coordsFinal.png")));
		settingsMenu.add(coordinateImage,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		
		JLabel smartImage = new JLabel(new ImageIcon(RunWindow.class.getResource("/smartFinal.png")));
		settingsMenu.add(smartImage,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		
		JButton coords = new JButton("Toggle Coordinates");
		settingsMenu.add(coords, gbc);
		coords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doNums();
				settingsMenu.dispose();
				JFrame f = new JFrame();
				if (isNums) JOptionPane.showMessageDialog(f,"Space Coordinates Activated");
				else JOptionPane.showMessageDialog(f,"Space Coordinates Deactivated");
			}
		});
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		
		JButton smartSuggest = new JButton("Toggle Smart Suggestions");
		settingsMenu.add(smartSuggest, gbc);
		smartSuggest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleSmartSuggestions();
				settingsMenu.dispose();
				JFrame f = new JFrame();
				if (smart) JOptionPane.showMessageDialog(f,"Smart Suggestions Activated");
				else JOptionPane.showMessageDialog(f,"Smart Suggestions Deactivated");


			}
		});
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		
		JButton newGame = new JButton("Start a New Game");
		settingsMenu.add(newGame, gbc);
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StdDraw.clear();
				makeCanvas(); //creates the game window and draws the squares for the board
				startSymbols();
				if (isNums) fillNums();
				highlightedYellow.clear();
				highlightedRed.clear();
				highlightedBlue.clear();
				turn = true;
				theEngine.reset();
				settingsMenu.dispose();
			}
		});
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		
		JButton newTimedGame = new JButton("Start a Timed Game");
		settingsMenu.add(newTimedGame, gbc);
		newTimedGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				gameTimer.reset(); // this line ensures any existing timer will be cancelled
				gameTimer.start(); // start will create its own jframe to get the timer and interval
				// amounts from the user and start the timer and then its own main method.
				
				settingsMenu.dispose();
			}
		});
		
		settingsMenu.pack();
		settingsMenu.setSize(400, 280);
		settingsMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		settingsMenu.setVisible(true);
		
	}
	
	
	

	
	
	
	public static void startScreen() {
		
		
		JFrame frame = new JFrame("Chess Menu");
		frame.setLayout(new GridBagLayout());

		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		
		
		frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 250,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - 100);

		
		ImageIcon img = new ImageIcon(RunWindow.class.getResource("/white rook.png"));
		frame.setIconImage(img.getImage());
		
		JButton button = new JButton("Start Game");
		frame.add(button,gbc);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run = true;
				RunWindow FullGame = new RunWindow();
				frame.dispose();
			}
		});
		
		JButton button2 = new JButton("Start Timed Game");
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		frame.add(button2,gbc);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameTimer.start();
				frame.dispose();
			}
		});
		
		frame.pack();
		frame.setSize(500, 100);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}


	
	
	
	public static void main(String[] args) {
		
		startScreen();
		
		while (!run) {
			doSleep();
		}

		while (true) {
			if (StdDraw.isMousePressed()) {
				if (StdDraw.mouseX() < .6666) mouseIsPressed();
				else if (StdDraw.mouseX() < .983 && StdDraw.mouseX() > .6836 && StdDraw.mouseY() < .3819 && StdDraw.mouseY() > .2199) {
					// If the user has clicked in the "options menu" box
					Settings();
					doSleep();
				}

			}
			if (over) break;
		}
		
		
		
	}
	
	
	}
	
	
	

