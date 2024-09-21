package chess;

import java.util.*;

public class Rule {
    private final boolean canRepeat;
    private final int[][] directions;

    // Constructor for Rule class
    public Rule(boolean canRepeat, int[][] directions) {
        this.canRepeat = canRepeat;
        this.directions = directions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return canRepeat == rule.canRepeat && Objects.deepEquals(directions, rule.directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(canRepeat, Arrays.deepHashCode(directions));
    }


    // Method to generate possible moves for the given piece on the board
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<int[]> moves = new ArrayList<>();  // Use ArrayList to store {x, y} pairs
        int currentX = pos.getRow();
        int currentY = pos.getColumn();
        ChessPiece pieceAtCurPosition = board.getPiece(new ChessPosition(pos.getRow(), pos.getColumn()));

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            int tempX = pos.getRow();
            int tempY = pos.getColumn();

            while (true) {
                tempX += dx;
                tempY += dy;

                // Check if the new position is valid on the board
                if (!board.isValidPosition(new ChessPosition(tempX, tempY))) {
                    break;
                }

                ChessPiece pieceAtNewPosition = board.getPiece(new ChessPosition(tempX, tempY));
                if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() == pieceAtCurPosition.getTeamColor()) {
                    break;  // Stop if a teammate is in the way
                }

                // Add the move (new position) as {x, y} pair to the ArrayList
                moves.add(new int[]{tempX, tempY});

                // If the piece cannot repeat moves in the same direction, stop here
                if (!canRepeat) {
                    break;
                }
            }
        }

        // Create a collection to store ChessMove objects
        Collection<ChessMove> chessMoves = new ArrayList<>();

        // Convert int[][] moves to ChessMove and add them to the collection
        for (int[] move : moves) {
            ChessPosition endPosition = new ChessPosition(move[0], move[1]);
            chessMoves.add(new ChessMove(pos, endPosition, null));  // Assuming no promotion
        }

        return chessMoves;  // Return the ArrayList of int[] pairs
    }
}
