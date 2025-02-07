package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private ChessBoard simulatedBoard;
    private TeamColor currentTeam = TeamColor.WHITE;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;  // If no piece exists at the start position, return null
        }

        // Generate all possible moves for the piece
        Collection<ChessMove> curMoves = piece.pieceMoves(board, startPosition);

        // Filter out moves that would put the player's king in check
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : curMoves) {
            simulatedBoard = board.copyBoard();
            // Simulate the move on the board
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece movingPiece = simulatedBoard.getPiece(startPosition);
            simulatedBoard.addPiece(endPosition, movingPiece);  // Move the piece
            simulatedBoard.addPiece(startPosition, null);       // Remove the piece from the start position

            // Check if the move causes check
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }

        simulatedBoard = null;
        return validMoves;  // Return the list of valid moves
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find the position of the king of the given team on the appropriate board
        ChessBoard activeBoard = (simulatedBoard != null) ? simulatedBoard : board;
        ChessPosition kingPosition = findKingPosition(activeBoard, teamColor);

        // Go through all pieces on the board and check if any opponent can attack the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = activeBoard.getPiece(pos);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    // Get possible moves for this opponent piece
                    Collection<ChessMove> opponentMoves = piece.pieceMoves(activeBoard, pos);

                    // Check if any move attacks the king, including promotions
                    for (ChessMove move : opponentMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true; // King is in check
                        }
                    }
                }
            }
        }
        return false;  // No attacking moves found
    }


    public ChessPosition findKingPosition(ChessBoard board, TeamColor teamColor) {
        // Loop through the entire board to find the king's position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition pos = new ChessPosition(row + 1, col + 1);  // Board positions are 1-indexed
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;  // Return the position of the king
                }
            }
        }
        return null;  // Return null if no king is found (shouldn't happen in a valid game)
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If the king is not in check, it's not checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Go through all pieces of the given team and check if any move can escape check
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    // Get all valid moves for this piece
                    Collection<ChessMove> moves = validMoves(position);

                    // Debugging: Print moves
                    System.out.println("Piece at " + position + " has moves: " + moves);

                    // If any valid move exists that gets the king out of check, it's not checkmate
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        // If no valid moves exist, it's checkmate
        return true;
    }




    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
//        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
