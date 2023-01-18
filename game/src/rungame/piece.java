package rungame;

public class piece {
	
	// This simple piece class is used to store two variables: the color of the piece and its type as 
	// represented by a double.
	
	double type; // 0 = pawn; 1 = rook; 2 = knight; 3 = bishop; 4 = queen; 5 = king;
	boolean color; // true = white; false = black
	
	public piece(double typee, boolean colorr) { // constructor initializes input
		this.type = typee;
		this.color = colorr;
	}
	
	@Override
	public String toString() {
		if (type == 0) return "pawn";
		else return "piece";
	}
	
}
