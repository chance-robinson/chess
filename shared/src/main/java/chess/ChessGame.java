package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 */
public class ChessGame {
    private ChessBoard board;
    private ChessBoard simulatedBoard;
    private TeamColor currentTeam = TeamColor.WHITE;
    private boolean isActiveGame = true;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", simulatedBoard=" + simulatedBoard +
                ", currentTeam=" + currentTeam +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board)
                && Objects.equals(simulatedBoard, chessGame.simulatedBoard) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, simulatedBoard, currentTeam);
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

    public boolean isActiveGame() {
        return isActiveGame;
    }

    public void setActiveGame(boolean isActiveGame) {
        this.isActiveGame = isActiveGame;
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
        // For checking validMoves using a simulated board
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> curMoves = piece.pieceMoves(board, startPosition);

        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : curMoves) {
            simulatedBoard = board.copyBoard();
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece movingPiece = simulatedBoard.getPiece(startPosition);
            simulatedBoard.addPiece(endPosition, movingPiece);
            simulatedBoard.addPiece(startPosition, null);

            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }

        simulatedBoard = null;
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece movingPiece = board.getPiece(startPos);
        if (movingPiece == null) {
            throw new InvalidMoveException("No start piece");
        }

        if (movingPiece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Wrong turn");
        }

        Collection<ChessMove> validMoves = validMoves(startPos);
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        board.addPiece(endPos, movingPiece);
        board.addPiece(startPos, null);

        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int promotionRow = (currentTeam == TeamColor.WHITE) ? 8 : 1;
            if (endPos.getRow() == promotionRow) {
                if (move.getPromotionPiece() == null) {
                    throw new InvalidMoveException("Pawn requires promotion");
                }
                ChessPiece promotedPiece = new ChessPiece(currentTeam, move.getPromotionPiece());
                board.addPiece(endPos, promotedPiece);
            }
        }

        if (isInCheck(currentTeam)) {
            board.addPiece(startPos, movingPiece);
            board.addPiece(endPos, null);
            throw new InvalidMoveException("Move puts your king in check");
        }

        currentTeam = (currentTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard activeBoard = (simulatedBoard != null) ? simulatedBoard : board;
        ChessPosition kingPosition = findKingPosition(activeBoard, teamColor);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = activeBoard.getPiece(pos);

                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                for (ChessMove move : piece.pieceMoves(activeBoard, pos)) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition findKingPosition(ChessBoard board, TeamColor teamColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noAvailableMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && noAvailableMoves(teamColor);
    }

    /**
     * Check for any available moves for a given teamColor
     *
     * @param teamColor which team to check for moves
     * @return True if the specified team has any available moves, otherwise false
     */
    private boolean noAvailableMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
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
