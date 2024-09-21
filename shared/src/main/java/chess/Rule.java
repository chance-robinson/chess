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

        if (pieceAtCurPosition.getPieceType() == ChessPiece.PieceType.PAWN) {
            return getDefaultPawnMoves(board, pos);
        }

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
                if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() != pieceAtCurPosition.getTeamColor()) {
                    moves.add(new int[]{tempX, tempY});
                    break;
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

    // Method for generating rook moves
    private Collection<ChessMove> getDefaultPawnMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        ChessPiece pieceAtCurPosition = board.getPiece(new ChessPosition(pos.getRow(), pos.getColumn()));
        int direction = (pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1; // Assume white pawns are on the lower half
        int newRow = pos.getRow() + direction;

        // Regular move
        if (board.isValidPosition(new ChessPosition(newRow, pos.getColumn())) &&
                board.getPiece(new ChessPosition(newRow, pos.getColumn())) == null) {
            // Check for promotion
            if (newRow == 8 || newRow == 1) {
                // Add promotion moves for each piece type
                for (ChessPiece.PieceType promoPiece : new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}) {
                    chessMoves.add(new ChessMove(pos, new ChessPosition(newRow, pos.getColumn()), promoPiece));
                }
            } else {
                chessMoves.add(new ChessMove(pos, new ChessPosition(newRow, pos.getColumn()), null));
            }
        }

        // First move double forward
        if ((pos.getRow() == 2 && direction == 1) || (pos.getRow() == 7 && direction == -1)) {
            newRow += direction; // Move two steps forward
            if (board.isValidPosition(new ChessPosition(newRow, pos.getColumn())) &&
                    board.getPiece(new ChessPosition(newRow-direction, pos.getColumn())) == null &&
                    board.getPiece(new ChessPosition(newRow, pos.getColumn())) == null) {
                // Check for promotion
                if (newRow == 8 || newRow == 1) {
                    // Add promotion moves for each piece type
                    for (ChessPiece.PieceType promoPiece : new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}) {
                        chessMoves.add(new ChessMove(pos, new ChessPosition(newRow, pos.getColumn()), promoPiece));
                    }
                } else {
                    chessMoves.add(new ChessMove(pos, new ChessPosition(newRow, pos.getColumn()), null));
                }
            }
        }

        // Capture moves (diagonal)
        for (int columnOffset : new int[]{-1, 1}) {
            int captureRow = pos.getRow() + direction;
            int captureColumn = pos.getColumn() + columnOffset;
            if (board.isValidPosition(new ChessPosition(captureRow, captureColumn))) {
                ChessPiece targetPiece = board.getPiece(new ChessPosition(captureRow, captureColumn));
                if (targetPiece != null && targetPiece.getTeamColor() != pieceAtCurPosition.getTeamColor()) {
                    // Check for promotion
                    if (newRow == 8 || newRow == 1) {
                        // Add promotion moves for each piece type
                        for (ChessPiece.PieceType promoPiece : new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}) {
                            chessMoves.add(new ChessMove(pos, new ChessPosition(captureRow, captureColumn), promoPiece));
                        }
                    } else {
                        chessMoves.add(new ChessMove(pos, new ChessPosition(captureRow, captureColumn), null));
                    }
                }
            }
        }

        return chessMoves;
    }
}
