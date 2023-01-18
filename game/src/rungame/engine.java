package rungame;

import java.util.ArrayList;

public class engine {
	
	piece[][] board; // x then y, horizontal first then vertical

	int[] whiteKingLoc = new int[]{4,0};
	int[] blackKingLoc = new int[]{4,7};

	
	public engine() {
		board = new piece[8][8];
		initialize(board);
	}
	
	
	
	public void reset() {
		board = new piece[8][8];
		initialize(board);
		whiteKingLoc = new int[] {4,0};
		blackKingLoc = new int[] {4,7};
	}
	
	public boolean isNull(int x, int y) {
		return (board[x][y] == null);
	}
	
	
	public boolean workAround(int x, int y) {
		return (x < 8 && x >= 0 && y < 8 && y >= 0);
	}
	
	
	public void initialize(piece[][] theBoard) {
		
		for (int i = 0;i < 8; i++) { // initialize white pawns
			theBoard[i][1] = new piece(0,true);
		}
		for (int i = 0;i < 8; i++) { //initialize black pawns
			theBoard[i][6] = new piece(0,false);
		}
		
		theBoard[0][0] = new piece(1,true); //initialize white then black rooks
		theBoard[7][0] = new piece(1,true);
		theBoard[7][7] = new piece(1,false);
		theBoard[0][7] = new piece(1,false);
		
		theBoard[1][0] = new piece(2,true); //initialize white then black knights
		theBoard[6][0] = new piece(2,true);
		theBoard[1][7] = new piece(2,false);
		theBoard[6][7] = new piece(2,false);
		
		theBoard[2][0] = new piece(3,true); //initialize white then black bishops
		theBoard[5][0] = new piece(3,true);
		theBoard[2][7] = new piece(3,false);
		theBoard[5][7] = new piece(3,false);
		
		theBoard[3][0] = new piece(4,true); //initialize white and black kings and queens
		theBoard[4][0] = new piece(5,true);
		theBoard[3][7] = new piece(4,false);
		theBoard[4][7] = new piece(5,false);
		
	}
	

	
	public void move(int oldX, int oldY, int newX, int newY) { //moves the piece at oldX,oldY to newX,newY
		
		board[newX][newY] = board[oldX][oldY];
		board[oldX][oldY] = null;

		
		if (board[newX][newY].type == 5) { // if the moved piece is a king
			if (board[newX][newY].color) { // check its color
				whiteKingLoc[0] = newX;    // and update its position in whiteKingLoc or blackKingLoc
				whiteKingLoc[1] = newY;
			}
			else {
				blackKingLoc[0] = newX;
				blackKingLoc[1] = newY;
			}
		}

	}
	
	public piece getPiece(int x,int y) {
		return board[x][y];
	}
	
	public double isLegal(int oldX, int oldY, int newX, int newY) { //checks if a move is allowed. 
		// returns 0 if illegal, 1 if legal, 2 if it puts white king in check, 
		// 3 if it puts black king in check, 4 if you tried to take the king
		
		
		if (oldX == newX && oldY == newY) return 0; //if the same space is selected
		
		boolean isEmpty = (board[newX][newY] == null); // keeping track of whether the new space is empty
		
		
		if (!isEmpty) {
			if (board[newX][newY].type == 5) return 0; //if the new space is a king
		}

		
		if (board[oldX][oldY].type == 5) { // if the king is moving. This needs to go before
			// we check to see if a piece is trying to take one of the same color
			return isKingLegal(oldX,oldY,newX,newY);
		}
		
		if (!isEmpty) {
			if (board[newX][newY].color == board[oldX][oldY].color) return 0; 
		}
		
		int distanceX = oldX-newX;
		int distanceY = oldY-newY; //set these to the distances between old and new spaces
		
		// negative distanceX means the new value is larger therefore to the right
		// negative distanceY means the new value is larger therefore farther up
		
		
		if (board[oldX][oldY].type == 0) { //if the type of the piece moving is a pawn
			return isPawnLegal(oldX,oldY,newX,newY);
			
		}
		
		if (board[oldX][oldY].type == 1) { //if the type of the piece moving is a rook, should work for both rooks
			if (distanceX != 0 && distanceY != 0) return 0; //making sure its only moving up or down
			return isRookLegal(oldX,oldY,newX,newY);
		}
			
		
		
		if (board[oldX][oldY].type == 2) { //there cannot be anything in a knight's path, we already checked whether the space its moving to has an illegal piece.
			if (!((Math.abs(distanceX) == 1 && Math.abs(distanceY) == 2) || (Math.abs(distanceX) == 2 && Math.abs(distanceY) == 1))) return 0;
			// ^^^^^^^^ this is checking, using the distances, whether the move is legal for a knight
			return isCheck(oldX, oldY, newX, newY);
		}
		
		if (board[oldX][oldY].type == 3) { //in case the type of moving piece is bishop
			if (Math.abs(distanceX) != Math.abs(distanceY)) return 0; //making sure its moving diagonally
			return isBishopLegal(oldX,oldY,newX,newY);
		}
		
		else { //in case the type of the moving piece is queen
			if ((distanceX == 0 && distanceY != 0) || (distanceX != 0 && distanceY == 0)) {
				return isRookLegal(oldX,oldY,newX,newY); //if it moves like a rook then reuse the rook legal method
			}
			else if (Math.abs(distanceX) == Math.abs(distanceY)) {
				return isBishopLegal(oldX,oldY,newX,newY); // if it moves like a bishop then reuse the bishop legal method
			}
			else return 0; //if it doesnt move like a rook or bishop then return illegal
		}
		
	
		
	}
	
	
//	public double isPawnLegal(int oldX, int oldY, int newX, int newY) {
//		return 10;
//	} // used for testing
	
	
	public double isPawnLegal(Integer oldX, Integer oldY, Integer newX, Integer newY) { // 
		int distanceX = oldX-newX;
		int distanceY = oldY-newY; //set these to the distances between old and new spaces
		
		// negative distanceX means the new value is larger therefore to the right
		// negative distanceY means the new value is larger therefore farther up
			if (board[oldX][oldY].color) { //if it is a white pawn
				if (distanceY == -2) { //if it is the initial two space pawn move
					if (isNull(newX,newY) && distanceX == 0 && oldY == 1) { 
						// if the initial two space target is empty and there is no horizontal movement and it starts at its spawnpoint						
						return isCheck(oldX, oldY, newX, newY);
					}
					else return 0; // if the three above criteria are not met and it is moving two up return illegal
				}
				if (distanceY != -1) return 0; // if it moves further than space up return illegal
				if (distanceX == -1 || distanceX == 1) { //if it is diagonal one or straight up
					
					
					
					
					if (RunWindow.enPassantX == newX && isNull(newX,newY)) {
						return isCheck(oldX,oldY,newX,newY);
					}
					
					
					
					
					if (isNull(newX,newY)) return 0; // if it is not taking a piece return illegal
					else if (!board[newX][newY].color) { //if is taking a piece of the opposite color
							return isCheck(oldX, oldY, newX, newY);
					}
					else return 0; // return illegal if it is moving one space diagonally into one of its own color
				}
				else if (distanceX == 0) {
					if (isNull(newX,newY)) {
						return isCheck(oldX, oldY, newX, newY);
					}
					else return 0;
				}
			}
		
			if (!board[oldX][oldY].color) { //if it is a black pawn
				if (distanceY == 2) { //if it is the initial two space pawn move
					if (isNull(newX,newY) && distanceX == 0 && oldY == 6) { 
						// if the initial two space target is empty and there is no horizontal movement and it starts at its spawnpoint
						return isCheck(oldX, oldY, newX, newY);
					}
					else return 0; // if the three above criteria are not met and it is moving two down return illegal
				}
				if (distanceY != 1) return 0; // if it moves further than space up return illegal
				if (distanceX == -1 || distanceX == 1) { //if it is diagonal one or straight up
					
					
					
					
					
					if (RunWindow.enPassantX == newX && isNull(newX,newY)) {
						return isCheck(oldX,oldY,newX,newY);
					}
					
					
					
					
					if (isNull(newX,newY)) return 0; // if it is not taking a piece return illegal
					else if (board[newX][newY].color) { //if is taking a piece of the opposite color
							return isCheck(oldX, oldY, newX, newY);
					}
					else return 0; // return illegal if it is moving one space diagonally into one of its own color
				}
				else if (distanceX == 0) {
					if (isNull(newX,newY)) {
						return isCheck(oldX, oldY, newX, newY);
					}
					else return 0;
				}
			}
		return 0; // if it is a pawn but the move was not legal this is the return value
	}
	
	
	public double isRookLegal(int oldX,int oldY,int newX,int newY) {
		
		int i;
		
		int distanceX = oldX-newX;
		int distanceY = oldY-newY; //set these to the distances between old and new spaces
		
		// negative distanceX means the new value is larger therefore to the right
		// negative distanceY means the new value is larger therefore farther up
		
			if (distanceX == 0 && distanceY > 0) { //if it is moving downwards
				for (i = oldY-1;i > newY; i--) { //checking if there is a piece in the path of the move
					if (board[oldX][i] != null) return 0;
				}
			}
			else if (distanceX == 0 && distanceY < 0) { //if it is moving upwards
				for (i = oldY+1;i < newY; i++) { //checking if there is a piece in the path of the move
					if (board[oldX][i] != null) return 0;
				}
			}
			else if (distanceX > 0 && distanceY == 0) { //if it is moving to the left
				for (i = oldX-1;i > newX; i--) { // if there is a piece in the path then return illegal
					if (board[i][oldY] != null) return 0;
				}
			}
			else if (distanceX < 0 && distanceY == 0) { //if it is moving to the right
				for (i = oldX+1;i < newX; i++) { // if there is a piece in the path then return illegal
					if (board[i][oldY] != null) return 0;
				}
			}
			return isCheck(oldX, oldY, newX, newY);
		}
	
	
	
	public double isBishopLegal(int oldX, int oldY, int newX, int newY) {
		
		int i; // create the iterator
		int distanceX = oldX-newX;
		int distanceY = oldY-newY; //set these to the distances between old and new spaces
		
		// negative distanceX means the new value is larger therefore to the right
		// negative distanceY means the new value is larger therefore farther up
		
		if (distanceX > 0 && distanceY > 0) { //if it is moving down to the left
			for (i = 1;oldX - i > newX;i++) { // if there is a piece in the path then return illegal
				if (board[oldX-i][oldY-i] != null) return 0;
			}
		}
		if (distanceX > 0 && distanceY < 0) { //if it is moving up and to the left
			for (i = 1;oldX - i > newX;i++) { // if there is a piece in the path then return illegal
				if (board[oldX-i][oldY+i] != null) return 0;
			}
		}
		if (distanceX < 0 && distanceY > 0) { //if it is moving down to the right
			for (i = 1;oldX + i< newX;i++) { // if there is a piece in the path then return illegal
				if (board[oldX+i][oldY-i] != null) return 0;
			}
		}
		if (distanceX < 0 && distanceY < 0) { //if it is moving up to the right
			for (i = 1;oldX + i < newX;i++) { // if there is a piece in the path then return illegal
				if (board[oldX+i][oldY+i] != null) return 0;
			}
		}
		return isCheck( oldX, oldY, newX, newY);
	}
	
	public double isKingLegal(int oldX, int oldY, int newX, int newY) {
		
		double tester = 0;

		if ((oldX == 4 && oldY == 0) && (newX == 0 && newY == 0)) {
			// If the proposed move is a castle with the left white rook
			if (RunWindow.canKingCastleWhite && RunWindow.canRookCastleWhiteLeft && !RunWindow.isCheck) {
				// if both the white king and white left rook are able to castle and the
				// white king is not currently in check
				if (isNull(3,0) && isNull(2,0) && isNull(1,0)) {
					// If the spaces between the rook and king are empty
					for (int i = 3;i >= 1;i--) {
						// This for loop checks if the king were in the two spaces toward the
						// side of the castle it would be in check. If it would not be in check
						// in either then it would return the value either 1 or 3 which would
						// indicate that the move is legal. If the check does not pass then 
						// this method ultimately returns 0 for illegal.
						if (i == 1) return tester;
						tester = isCheck(4,0,i,0);
						if (tester != 1 && tester != 3) {
							break;
						}
					}
				}
				
			}
		}
		else if ((oldX == 4 && oldY == 0) && (newX == 7 && newY == 0)) {
			// If the proposed move is a castle with the right white rook
			if (RunWindow.canKingCastleWhite && RunWindow.canRookCastleWhiteRight && !RunWindow.isCheck) {
				// if both the white king and white right rook are able to castle and the
				// white king is not currently in check
				if (isNull(5,0) && isNull(6,0)) {
					for (int i = 6;i >= 4;i--) {
						if (i == 4) return tester;
						tester = isCheck(4,0,i,0);
						if (tester != 1 && tester != 3) {
							break;
						}
					}
				}
				
			}
		}
		
		else if ((oldX == 4 && oldY == 7) && (newX == 0 && newY == 7)) {
			// If the proposed move is a castle with the left black rook
			if (RunWindow.canKingCastleBlack && RunWindow.canRookCastleBlackLeft && !RunWindow.isCheck) {
				// if both the black king and black left rook are able to castle and the
				// black king is not currently in check
				if (isNull(3,7) && isNull(2,7) && isNull(1,7)) {
					for (int i = 3;i >= 1;i--) {
						if (i == 1) return tester;
						tester = isCheck(4,7,i,7);
						if (tester != 1 && tester != 3) {
							break;
						}
					}
				}
				
			}
			
		}
		
		else if ((oldX == 4 && oldY == 7) && (newX == 7 && newY == 7)) {
			// If the proposed move is a castle with the right black rook
			if (RunWindow.canKingCastleBlack && RunWindow.canRookCastleBlackRight && !RunWindow.isCheck) {
				// if both the black king and black right rook are able to castle and the
				// black king is not currently in check
				if (isNull(5,7) && isNull(6,7)) {
					if (isNull(5,7) && isNull(6,7)) {
						for (int i = 6;i >= 4;i--) {
							if (i == 4) return tester;
							tester = isCheck(4,7,i,7);
							if (tester != 1 && tester != 3) {
								break;
							}
						}
					}
				}
				
			}
			
		}
		
		
		
		
		int distanceX = oldX-newX;
		int distanceY = oldY-newY; //set these to the distances between old and new spaces
		
		// negative distanceX means the new value is larger therefore to the right
		// negative distanceY means the new value is larger therefore farther up
		
		if ((Math.abs(distanceX) == 0 || Math.abs(distanceX) == 1) && (Math.abs(distanceY) == 0 || Math.abs(distanceY) == 1)) {
			return isCheck(oldX, oldY, newX, newY);
		}
		return 0;
	}
	
	public double checkReturn(boolean color, piece bitesTheDustOld, piece bitesTheDustNew, int oldX, int oldY, int newX, int newY, boolean legal, boolean enPassant) {
		
		
		
		// the boolean color input tells this method
		// if the king in check is white (true) or black (false). bitesTheDustOld holds a copy by value of
		// the piece that was at the old location before isCheck moved it. bitesTheDustNew holds a copy
		// by value of whatever was at the new location (might by null) before isCheck moved it. The 
		// four int inputs tell this method where to relocate the bitesTheDust pieces so that this method
		// can return the board to before the test and return its solution. The final boolean is used
		// when all the check scans failed to tell this method to return 1 for legal.
		
		
		// the isCheck method holds onto the values of both old and new locations of the potential move and
		// performs the move to test whether it is in check. When it comes time to return a value it calls
		// this method, which returns the board to its state before the move. This is done because if the
		// moves is illegal then the board must not be changed.
		
		
		if (enPassant) { // If the move being checked is an enPassant
			board[oldX][oldY] = new piece(bitesTheDustOld.type, bitesTheDustOld.color);
			// set the space at oldX, oldY equal to bitesTheDustOld
			board[newX][newY] = null;
			// The space being moved to is always null, so replace it will null.
			if (color) { // If the piece being tested is white
				board[newX][newY-1] = new piece(bitesTheDustNew.type, bitesTheDustNew.color);
				// replace the pawn being taken by enPassant with bitesTheDustNew
			}
			else {
				board[newX][newY+1] = new piece(bitesTheDustNew.type, bitesTheDustNew.color);
				// same but different for the black piece because their pawns move down
			}
		}
		
		else {
			board[oldX][oldY] = new piece(bitesTheDustOld.type, bitesTheDustOld.color);
			if (bitesTheDustNew != null) {
				board[newX][newY] = new piece(bitesTheDustNew.type, bitesTheDustNew.color); 
				}
				else board[newX][newY] = null;
		}
		
		if (legal) return 1;
		// 1 is returned when there is no check and the move is valid.
		else if (color) return 2;
		// 2 is returned when the white king is in check.
		else return 3;
		// 3 is returned when the black king is in check.
	}
	
	
	
	
	public double isCheck(int oldX, int oldY, int newX, int newY) { //returns 1 if no check, 2 if white check, 3 if black check
		int[] tempWhiteKing = new int[] {whiteKingLoc[0], whiteKingLoc[1]}; // create temp white and black king locations by value
		int[] tempBlackKing = new int[] {blackKingLoc[0], blackKingLoc[1]};
		
		piece bitesTheDustOld = new piece(board[oldX][oldY].type,board[oldX][oldY].color);
		piece bitesTheDustNew = null;
		
		
		boolean isEnPassant = false; // This var is basically only used at the beginning of the method in order
		// to both keep track of whether the move is en passant and so that the board can be moved properly
		// before the kings are checked.
		
		if (board[oldX][oldY].type == 0 && Math.abs(oldX-newX) == 1 && Math.abs(oldY-newY) == 1 && isNull(newX,newY)) {
			// If the piece moving is a pawn, it is moving diagonally, and it is moving into a null space
			// this is all we need to check because the only scenario where these conditionals are 
			// true is when en passant is being performed.
			isEnPassant = true;
		}
		
		
		else if (!isNull(newX,newY)) { // If the space being moved to has a piece in it that needs to be saved,
			// then copy it by value into the bitesTheDustNew variable.
			bitesTheDustNew = new piece(board[newX][newY].type,board[newX][newY].color);
		}




		
		board[newX][newY] = new piece(board[oldX][oldY].type,board[oldX][oldY].color);
		board[oldX][oldY] = null;
		
		if (isEnPassant) {
			
			if (board[newX][newY].color) { // if the piece being moved (it has already moved) is white
				bitesTheDustNew = new piece(getPiece(newX,newY-1).type,getPiece(newX,newY-1).color);
				// We create the new var using values because we need to change the value of the piece in
				// the space to test isCheck, and if we simply referred to it then the change would happen
				// to both values.
				board[newX][newY-1] = null;
				// This statement clears the space of the piece being taken by en passant, it is only
				// ever one underneath the white pawn's new location
			}
			else {
				bitesTheDustNew = new piece(board[newX][newY+1].type,board[newX][newY+1].color);
				board[newX][newY+1] = null;
				// these are all the same commands, just for the black pawn instead.
			}
		}
		
		
		
		if (board[newX][newY].type == 5) { // making sure we know where both kings are on the temp board
			if (board[newX][newY].color) { // in case the proposed move involves one of them
				tempWhiteKing[0] = newX;
				tempWhiteKing[1] = newY;
			}
			else {
				tempBlackKing[0] = newX;
				tempBlackKing[1] = newY;
			}
		}
		int wKingX = tempWhiteKing[0];
		int wKingY = tempWhiteKing[1];
		
		//
		//
		// THESE FOUR FOR LOOPS CHECK THE SPACES UP, DOWN, LEFT, AND RIGHT OF THE WHITE KING
		//
		//
		
		for (int i = wKingY+1;i <= 7;i++) { //checking the spaces above the white king, testing for rook and queen
			if (board[wKingX][i] != null) { //if the space is not empty
				if (board[wKingX][i].color) break; //if it is a white piece then break
				else { // if it is a black rook or queen then return white check
					if (board[wKingX][i].type == 1 || board[wKingX][i].type == 4) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		for (int i = wKingY-1;i >= 0;i--) { //checking the spaces below the white king, testing for rook and queen
			if (board[wKingX][i] != null) { //if the space is not empty
				if (board[wKingX][i].color) break; //if it is a white piece then break
				else { // if it is a black rook or queen then return white check
					if (board[wKingX][i].type == 1 || board[wKingX][i].type == 4) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		for (int i = wKingX+1;i <= 7;i++) { //checking the spaces to the right of the white king, testing for rook and queen
			if (board[i][wKingY] != null) { //if the space is not empty
				if (board[i][wKingY].color) break; //if it is a white piece then break
				else { // if it is a black rook or queen then return white check
					if (board[i][wKingY].type == 1 || board[i][wKingY].type == 4) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		for (int i = wKingX-1;i >= 0;i--) { //checking the spaces to the left of the white king, testing for rook and queen
			if (board[i][wKingY] != null) { //if the space is not empty
				if (board[i][wKingY].color) break; //if it is a white piece then break
				else { // if it is a black rook or queen then return white check
					if (board[i][wKingY].type == 1 || board[i][wKingY].type == 4) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		
		//
		//
		// THESE WHILE LOOPS CHECK THE SPACES DIAGONAL OF THE WHITE KING
		//
		//
		
		int i = wKingX + 1;
		int k = wKingY + 1;
		while (i <= 7 && k <= 7) { //checking the diagonals to the up right of the white king
			if (board[i][k] != null) { //if the space is not empty
				if (board[i][k].color) break; //if it is a white piece then break
				else { // if it is a black pawn one space up right of the king then return white check
					if (board[i][k].type == 0 && i-wKingY == 1) {// for up right and up left of 
						// the white king we need to check if a black pawn in that spot 
						// could put the white king in check
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return white check
					}
				}
				break;
			}
			i++;
			k++;
		}
		
		i = wKingX - 1;
		k = wKingY + 1;
		while (i >= 0 && k <= 7) { //checking the diagonals to the up left of the white king
			if (board[i][k] != null) { //if the space is not empty
				if (board[i][k].color) break; //if it is a white piece then break
				else { // if it is a black pawn one space up left of the king then return white check
					if (board[i][k].type == 0 && k-wKingY == 1) { // for up right and up left of 
						// the white king we need to check if a black pawn in that spot 
						// could put the white king in check
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return white check
					}
				}
				break;
			}
			i--;
			k++;
		}
		
		i = wKingX - 1;
		k = wKingY - 1;
		while (i >= 0 && k >= 0) { //checking the diagonals to the down left of the white king
			if (board[i][k] != null) { //if the space is not empty
				if (board[i][k].color) break; //if it is a white piece then break
				else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return white check
				}
				break;
			}
			i--;
			k--;
		}
		
		i = wKingX + 1;
		k = wKingY - 1;
		while (i <= 7 && k >= 0) { //checking the diagonals to the down right of the white king
			if (board[i][k] != null) { //if the space is not empty
				if (board[i][k].color) break; //if it is a white piece then break
				else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return white check
				}
				break;
			}
			i++;
			k--;
		}
		
		//
		//
		// THESE 8 CONDITIONALS CHECK THE SPECIFIC KNIGHT SPACES AROUND THE WHITE KING
		//
		//
		
		i = wKingX + 1;
		k = wKingY + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) { // this line makes sure we are not checking indices that are out of bounds. This would happen if the king is near the edge.
			if (board[i][k] != null) { //checking the 8 specific knight spaces surrounding the white king
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX + 2;
		k = wKingY + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX + 2;
		k = wKingY - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX + 1;
		k = wKingY - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX - 1;
		k = wKingY - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX - 2;
		k = wKingY - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX - 2;
		k = wKingY + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = wKingX - 1;
		k = wKingY + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == false && board[i][k].type == 2) {
					return checkReturn(true, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		
		
		int bKingX = tempBlackKing[0];
		int bKingY = tempBlackKing[1];
		
		
		
		//
		//
		// THESE FOUR FOR LOOPS CHECK THE SPACES UP, DOWN, LEFT, AND RIGHT OF THE BLACK KING
		//
		//
		
		for (i = bKingY+1;i <= 7;i++) { //checking the spaces above the black king, testing for rook and queen
			if (board[bKingX][i] != null) { //if the space is not empty
				if (!board[bKingX][i].color) break; //if it is a white piece then break
				else { // if it is a white rook or queen then return black check
					if (board[bKingX][i].type == 1 || board[bKingX][i].type == 4) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		for (i = bKingY-1;i >= 0;i--) { //checking the spaces below the black king, testing for rook and queen
			if (board[bKingX][i] != null) { //if the space is not empty
				if (!board[bKingX][i].color) break; //if it is a black piece then break
				else { // if it is a white rook or queen then return black check
					if (board[bKingX][i].type == 1 || board[bKingX][i].type == 4) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		for (i = bKingX+1;i <= 7;i++) { //checking the spaces to the right of the black king, testing for rook and queen
			if (board[i][bKingY] != null) { //if the space is not empty
				if (!board[i][bKingY].color) break; //if it is a black piece then break
				else { // if it is a white rook or queen then return white check
					if (board[i][bKingY].type == 1 || board[i][bKingY].type == 4) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		for (i = bKingX-1;i >= 0;i--) { //checking the spaces to the left of the black king, testing for rook and queen
			if (board[i][bKingY] != null) { //if the space is not empty
				if (!board[i][bKingY].color) break; //if it is a black piece then break
				else { // if it is a white rook or queen then return black check
					if (board[i][bKingY].type == 1 || board[i][bKingY].type == 4) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					break;
				}
			}
		}
		
		
		
		//
		//
		// THESE WHILE LOOPS CHECK THE SPACES DIAGONAL OF THE BLACK KING
		//
		//
		
		i = bKingX + 1;
		k = bKingY + 1;
		while (i <= 7 && k <= 7) { //checking the diagonals to the up right of the black king
			if (board[i][k] != null) { //if the space is not empty
				if (!board[i][k].color) break; //if it is a black piece then break
				else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return black check
				}
				break;
			}
			i++;
			k++;
		}
		
		i = bKingX - 1;
		k = bKingY + 1;
		while (i >= 0 && k <= 7) { //checking the diagonals to the up left of the black king
			if (board[i][k] != null) { //if the space is not empty
				if (!board[i][k].color) break; //if it is a black piece then break
				else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return black check
				}
				break;
			}
			i--;
			k++;
		}
		
		i = bKingX - 1;
		k = bKingY - 1;
		while (i >= 0 && k >= 0) { //checking the diagonals to the down left of the black king
			if (board[i][k] != null) { //if the space is not empty
				if (!board[i][k].color) break; //if it is a black piece then break
				else { // if it is a black pawn one space down left of the king then return white check
					if (board[i][k].type == 0 && bKingY-k == 1) { // for down right and down left of the black king we 
						// need to check if a white pawn in that spot could put the black king in check
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return black check
					}
				}
				break;
			}
			i--;
			k--;
		}
		
		i = bKingX + 1;
		k = bKingY - 1;
		while (i <= 7 && k >= 0) { //checking the diagonals to the down right of the black king
			if (board[i][k] != null) { //if the space is not empty
				if (!board[i][k].color) break; //if it is a black piece then break
				else { // if it is a black pawn one space down left of the king then return white check
					if (board[i][k].type == 0 && bKingY-k == 1) { // for down right and down left of the black king we 
						// need to check if a white pawn in that spot could put the black king in check
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
					}
					else if (board[i][k].type == 4 || board[i][k].type == 3) {
						return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
						//if the piece is a queen or bishop return black check
					}
				}
				break;
			}
			i++;
			k--;
		}
		
		//
		//
		// THESE 8 CONDITIONALS CHECK THE SPECIFIC KNIGHT SPACES AROUND THE BLACK KING
		//
		//
		
		
		i = bKingX + 1;
		k = bKingY + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) { // this line makes sure we are not checking indices that are out of bounds. This would happen if the king is near the edge.
			if (board[i][k] != null) { //checking the 8 specific knight spaces surrounding the black king
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX + 2;
		k = bKingY + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX + 2;
		k = bKingY - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX + 1;
		k = bKingY - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (!isNull(i,k)) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX - 1;
		k = bKingY - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX - 2;
		k = bKingY - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX - 2;
		k = bKingY + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		i = bKingX - 1;
		k = bKingY + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) { 
				if (board[i][k].color == true && board[i][k].type == 2) {
					return checkReturn(false, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,false,isEnPassant);
				}
			}
		}
		
		
		
		return checkReturn (board[newX][newY].color, bitesTheDustOld,bitesTheDustNew,oldX,oldY,newX,newY,true,isEnPassant); 
		// supposedly all of the conditionals above will catch cases where a piece has a king in check.
		// The first boolean input is the color of the original moving piece, then the pieces that were 
		// moved so that they can be moved back, the locations to move them to, a 1 to show that the move
		// is legal, and then the isEnPassant variable which was declared as either true or false at
		// the beginning of the method.
	}
	
	
	public ArrayList<int[]> pawnAroundNull(int x, int y, boolean itsColor) {
		// this method checks what the legal open spaces around the selected pawn are and returns them in
		// the form of an arraylist containing int arrays containing pairs.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		
		
		temp.clear();
		if (itsColor) { // if the selected pawn is white
			if (isNull(x,y+1)) {
				temp.add(new int[] {x,y+1});
				if (y == 1 && isNull(x,y+2)) temp.add(new int[] {x,y+2});
				

			}
		}
		else { // if the selected pawn is black
			if (isNull(x,y-1)) {
				temp.add(new int[] {x,y-1});
				if (y == 6 && isNull(x,y-2)) temp.add(new int[] {x,y-2});

			}
		}
		return temp;
	}
	
	
	public ArrayList<int[]> pawnAroundTarget(int x, int y, boolean itsColor) {
		// This method checks for legal spaces around a pawn where it would be taking another piece.
		// Because the pawnAroundNull method checks the spaces in front of the pawn where it would move to a
		// null space, this method needs only to check the diagonals where a pawn could possible take
		// another piece.
		
		// en passant moves are also checked in this method and are added to the highlightedRed arraylist in
		// the main RunWindow. 
		
		ArrayList<int[]> temp = new ArrayList<int[]>();
		temp.clear();
		
		if (itsColor) { // If the pawn is white
			if (x+1 < 8) { // If the pawn is not on the right edge of the board
				if (!isNull(x+1,y+1)) { // If the space up and to the right up the pawn is not null
					if (!getPiece(x+1,y+1).color) temp.add(new int[] {x+1,y+1});
					// If there is a piece of opposite color diagonal right of the pawn add it to the list
				}
				else if (!isNull(x+1,y) && RunWindow.enPassantX != null) { // CHECKING FOR EN PASSANT: if the space to the right is not null
					if (!getPiece(x+1,y).color && getPiece(x+1,y).type == 0) temp.add(new int[] {x+1,y+1});
					// If there is a piece to the right and it is opposite color and pawn add it to the list
				}
			}
			if (x-1 > 0) { // If the pawn is not on the left edge of the board
				if (!isNull(x-1,y+1)) {// If the space up and to the left of the pawn is not null
					if (!getPiece(x-1,y+1).color) temp.add(new int[] {x-1,y+1});
					// If there is a piece of opposite color diagonal left of the pawn add it to the list
				}
				else if (!isNull(x-1,y) && RunWindow.enPassantX != null) { // CHECKING FOR EN PASSANT: if the space to the left is not null
					if (!getPiece(x-1,y).color && getPiece(x-1,y).type == 0) temp.add(new int[] {x-1,y+1});
					// If there is a piece to the left and it is opposite color and pawn add it to the list
				}
			}
		}
		else { // If the pawn is black
			if (x+1 < 8) { // If the pawn is not on the right edge of the board
				if (!isNull(x+1,y-1)) { // If the space down and to the right of the pawn is not null
					if (getPiece(x+1,y-1).color) temp.add(new int[] {x+1,y-1});
					// If there is a piece of opposite color diagonal left of the pawn add it to the list
				}
				else if (!isNull(x+1,y) && RunWindow.enPassantX != null) { // CHECKING FOR EN PASSANT: if the space to the right is not null
					if (getPiece(x+1,y).color && getPiece(x+1,y).type == 0) temp.add(new int[] {x+1,y-1});
					// If there is a piece to the right and it is opposite color and pawn add it to the list
				}
			}
			if (x-1 > 0) { // If the pawn is not on the left edge of the board
				if (!isNull(x-1,y-1)) { // If the space down and to the left of the pawn is not null
					if (getPiece(x-1,y-1).color) temp.add(new int[] {x-1,y-1});
					// If there is a piece of opposite color diagonal left of the pawn add it to the list
				}
				else if (!isNull(x-1,y) && RunWindow.enPassantX != null) { // CHECKING FOR EN PASSANT: if the space to the left is not null
					if (getPiece(x-1,y).color && getPiece(x-1,y).type == 0) temp.add(new int[] {x-1,y-1});
					// If there is a piece to the left and it is opposite color and pawn add it to the list
				}
			}
		}
		return temp;
	}

	
	public ArrayList<int[]> rookAroundNull(int x, int y, boolean itsColor) {
		// this method returns an arraylist containing possible moves to an open space for a selected rook.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		int i;
		for (i = x+1;i < 8;i++) { // checking spaces to the right of the rook
			if (isNull(i,y)) temp.add(new int[] {i,y});
			else break;
		}
		for (i = x-1;i >= 0;i--) { // checking spaces to the left of the rook
			if (isNull(i,y)) temp.add(new int[] {i,y});	
			else break;
			}
		for (i = y+1;i < 8;i++) { //checking spaces above the rook
			if (isNull(x,i)) temp.add(new int[] {x,i});	
			else break;
		}
		for (i = y-1;i >= 0;i--) { //checking spaces below the rook
			if (isNull(x,i)) temp.add(new int[] {x,i});	
			else break;
		}
		return temp;
	}
	
	public ArrayList<int[]> rookAroundTarget(int x, int y, boolean itsColor) {
		// this method returns an arraylist containing possible moves to an open space for a selected rook.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		int i;
		for (i = x+1;i < 8;i++) { // checking spaces to the right of the rook
			if (!isNull(i,y)) { // if the space is not empty
				if (getPiece(i,y).color != itsColor) { // if the piece occupying the space is the other color
					temp.add(new int[] {i,y}); 
					break;
				}
				else break; // if the piece in the path is the same color then break
			}
		}
		for (i = x-1;i > 0;i--) { // checking spaces to the left of the rook
			if (!isNull(i,y)) { // if the space is not empty
				if (getPiece(i,y).color != itsColor) { // if the piece occupying the space is the other color
					temp.add(new int[] {i,y}); 
					break;
				}
				else break;
			}
		}
		for (i = y+1;i < 8;i++) { //checking spaces above the rook
			if (!isNull(x,i)) { // if the space is not empty
				if (getPiece(x,i).color != itsColor) { // if the piece occupying the space is the other color
					temp.add(new int[] {x,i}); 
					break;
				}
				else break;
			}
		}
		for (i = y-1;i > 0;i--) { //checking spaces below the rook
			if (!isNull(x,i)) { // if the space is not empty
				if (getPiece(x,i).color != itsColor) { // if the piece occupying the space is the other color
					temp.add(new int[] {x,i}); 
					break;
				}
				else break;
			}
		}
		return temp;
	}
	

	
	public ArrayList<int[]> knightAroundNull(int x, int y, boolean itsColor) {
		// this method returns an arraylist containing possible moves to an open space for a selected rook.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		
		int i = x + 1;
		int k = y + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) { // this line makes sure we are not checking indices that are out of bounds. 
			// This would happen if the knight is near the edge.
			if (board[i][k] == null) temp.add(new int[] {i,k});
		}
		i = x + 2;
		k = y + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});

		}
		i = x + 2;
		k = y - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});

		}
		i = x + 1;
		k = y - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});

		}
		i = x - 1;
		k = y - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});

		}
		i = x - 2;
		k = y - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});

		}
		i = x - 2;
		k = y + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});

		}
		i = x - 1;
		k = y + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] == null) temp.add(new int[] {i,k});
		}
		return temp;
	}
	
	
	public ArrayList<int[]> knightAroundTarget(int x, int y, boolean itsColor) {
		// this method returns an arraylist containing possible moves to an occupied space for a selected rook.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		
		int i = x + 1;
		int k = y + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) { // this line makes sure we are not checking indices that are out of bounds. 
			// This would happen if the knight is near the edge.
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x + 2;
		k = y + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x + 2;
		k = y - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x + 1;
		k = y - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x - 1;
		k = y - 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x - 2;
		k = y - 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x - 2;
		k = y + 1;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		i = x - 1;
		k = y + 2;
		if (i >= 0 && i <= 7 && k >= 0 && k <= 7) {
			if (board[i][k] != null) {
				if (board[i][k].color != itsColor) temp.add(new int[] {i,k});
			}
		}
		return temp;
	}
	
	public ArrayList<int[]> bishopAroundNull(int x, int y, boolean itsColor) {
		// this method returns an arraylist containing possible moves to an open space for a selected bishop.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		int tempIterator; // this will be used in all 4 for loops
		for (tempIterator = 1;x+tempIterator < 8 && y+tempIterator < 8;tempIterator++) { // testing up and to the right of the selected piece
			if (isNull(x + tempIterator, y + tempIterator)) temp.add(new int[] {x+tempIterator,y+tempIterator});
			else break;
		}
		for (tempIterator = 1;x + tempIterator < 8 && y - tempIterator >= 0;tempIterator++) { //testing down and to the right of the selected piece
			if (isNull(x + tempIterator, y - tempIterator)) temp.add(new int[] {x+tempIterator,y-tempIterator});
			else break;
		}
		for (tempIterator = 1;x - tempIterator >= 0 && y + tempIterator < 8;tempIterator++) { //testing up and to the left of the selected piece
			if (isNull(x - tempIterator, y + tempIterator)) temp.add(new int[] {x-tempIterator,y+tempIterator});
			else break;
		}
		for (tempIterator = 1;x - tempIterator >= 0 && y - tempIterator >= 0;tempIterator++) { //testing up and to the left of the selected piece
			if (isNull(x - tempIterator, y - tempIterator)) temp.add(new int[] {x-tempIterator,y-tempIterator});
			else break;
		}
	return temp;
	}
	
	
	public ArrayList<int[]> bishopAroundTarget(int x, int y, boolean itsColor) {
		// this method returns an arraylist containing possible moves to an occupied space for a selected bishop.
		ArrayList<int[]> temp = new ArrayList<int[]>();
		int tempIterator; // this will be used in all 4 for loops
		for (tempIterator = 1;x+tempIterator < 8 && y+tempIterator < 8;tempIterator++) { // testing up and to the right of the selected piece
			if (!isNull(x + tempIterator, y + tempIterator)) {
				if (board[x+tempIterator][y+tempIterator].color != itsColor) {
					temp.add(new int[] {x+tempIterator,y+tempIterator});
				}
				break;
			}
		}
		for (tempIterator = 1;x + tempIterator < 8 && y - tempIterator >= 0;tempIterator++) { //testing down and to the right of the selected piece
			if (!isNull(x + tempIterator, y - tempIterator)) {
				if (board[x+tempIterator][y-tempIterator].color != itsColor) {
					temp.add(new int[] {x+tempIterator,y-tempIterator});
				}
				break;
			}
		}
		for (tempIterator = 1;x - tempIterator >= 0 && y + tempIterator < 8;tempIterator++) { //testing up and to the left of the selected piece
			if (!isNull(x - tempIterator, y + tempIterator)) {
				if (board[x-tempIterator][y+tempIterator].color != itsColor) {
					temp.add(new int[] {x-tempIterator,y+tempIterator});
				}
				break;
			}
		}
		for (tempIterator = 1;x - tempIterator >= 0 && y - tempIterator >= 0;tempIterator++) { //testing down and to the left of the selected piece
			if (!isNull(x - tempIterator, y - tempIterator)) {
				if (board[x-tempIterator][y-tempIterator].color != itsColor) {
					temp.add(new int[] {x-tempIterator,y-tempIterator});
				}
				break;
			}
		}
		return temp;
	}

	public ArrayList<int[]> queenAroundNull(int x, int y, boolean itsColor) {
		
		ArrayList<int[]> temp = rookAroundNull(x,y,itsColor);
		temp.addAll(bishopAroundNull(x,y,itsColor));
		
		return temp;
	}

	public ArrayList<int[]> queenAroundTarget(int x, int y, boolean itsColor) {
		
		ArrayList<int[]> temp = rookAroundTarget(x,y,itsColor);
		temp.addAll(bishopAroundTarget(x,y,itsColor));
		
		return temp;
	}

	public ArrayList<int[]> kingAroundNull(int x, int y, boolean itsColor) {
		ArrayList<int[]> temp = new ArrayList<int[]>();
		try {
			if (isNull(x+1,y+1)) temp.add(new int[] {x+1,y+1});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x+1,y)) temp.add(new int[] {x+1,y});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x+1,y-1)) temp.add(new int[] {x+1,y-1});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x,y+1)) temp.add(new int[] {x,y+1});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x,y)) temp.add(new int[] {x,y});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x,y-1)) temp.add(new int[] {x,y-1});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x-1,y+1)) temp.add(new int[] {x-1,y+1});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x-1,y)) temp.add(new int[] {x-1,y});
		} catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if (isNull(x-1,y-1)) temp.add(new int[] {x-1,y-1});
		} catch(ArrayIndexOutOfBoundsException e) {}
		
		temp.addAll(kingCastle(x,y,itsColor)); // If there are possibilities to castle, they 
		// are added here. If not, then nothing is added.
		
		
		return temp;
	}

	public ArrayList<int[]> kingAroundTarget(int x, int y, boolean itsColor) {
		ArrayList<int[]> temp = new ArrayList<int[]>();

		// the workAround method returns true if the inputted ints are in the scope of the array (0 <= x < 8)
		// using the workAround method streamlines this sections which needs to test 9 specific spaces.
		
		if (workAround(x+1,y+1)) {
			if (!isNull(x+1,y+1)) if (getPiece(x+1,y+1).color != itsColor) temp.add(new int[] {x+1,y+1}); }
		if (workAround(x+1,y)) {
			if (!isNull(x+1,y)) if (getPiece(x+1,y).color != itsColor) temp.add(new int[] {x+1,y}); }
		if (workAround(x+1,y-1)) {
			if (!isNull(x+1,y-1)) if (getPiece(x+1,y-1).color != itsColor) temp.add(new int[] {x+1,y-1}); }
		if (workAround(x,y+1)) {
			if (!isNull(x,y+1)) if (getPiece(x,y+1).color != itsColor) temp.add(new int[] {x,y+1}); }
		if (workAround(x,y)) {
			if (!isNull(x,y)) if (getPiece(x,y).color != itsColor) temp.add(new int[] {x,y}); }
		if (workAround(x,y-1)) {
			if (!isNull(x,y-1)) if (getPiece(x,y-1).color != itsColor) temp.add(new int[] {x,y-1}); }
		if (workAround(x-1,y+1)) {
			if (!isNull(x-1,y+1)) if (getPiece(x-1,y+1).color != itsColor) temp.add(new int[] {x-1,y+1}); }
		if (workAround(x-1,y)) {
			if (!isNull(x-1,y)) if (getPiece(x-1,y).color != itsColor) temp.add(new int[] {x-1,y}); }
		if (workAround(x-1,y-1)) {
			if (!isNull(x-1,y-1)) if (getPiece(x-1,y-1).color != itsColor) temp.add(new int[] {x-1,y-1}); }
		
		
		return temp;
	}
	
	
	public ArrayList<int[]> kingCastle(int x, int y, boolean itsColor) {
		// This method is a helper for KingAroundNull, which gathers a list of spaces the selected
		// king can move to that would not take a piece. This method makes an arraylist and 
		// checks to see if castling is legal on both sides of the king. If it is, then it
		// adds a coordinate pair in the form of an array to the arraylist, which is returned
		// at the end of the method to be concatenated with the main highlightedYellow arraylist
		
		ArrayList<int[]> temp1 = new ArrayList<int[]>();
		
		if (itsColor && RunWindow.canKingCastleWhite) { 
			// If the king selected is white and it can legally castle
			if ((isLegal(x,y,0,0) == 1 || isLegal(x,y,0,0) == 3) && RunWindow.canRookCastleWhiteLeft) temp1.add(new int[] {0,0});
			if ((isLegal(x,y,7,0) == 1 || isLegal(x,y,7,0) == 3) && RunWindow.canRookCastleWhiteRight) temp1.add(new int[] {7,0});
		}
		
		else if (!itsColor && RunWindow.canKingCastleBlack) { 
			// if the selected king is black and it can legally castle
			if ((isLegal(x,y,0,7) == 1 || isLegal(x,y,0,7) == 3) && RunWindow.canRookCastleBlackLeft) temp1.add(new int[] {0,7});
			if ((isLegal(x,y,7,7) == 1 || isLegal(x,y,7,7) == 3) && RunWindow.canRookCastleBlackRight) temp1.add(new int[] {7,7});
		}
		

		return temp1;
	}
	
	
	public void delete(int x, int y) {
		board[x][y] = null;
	}
	
	
}
