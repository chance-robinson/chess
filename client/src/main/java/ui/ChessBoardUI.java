package ui;

import chess.*;

import java.util.Collection;

import static ui.EscapeSequences.*;

/**
 *  The class for rendering the Chess Board based on a given playerColor perspective
 *  and specific ChessGame data.
 */
public class ChessBoardUI {
    private static final char[][] CHESS_BOARD = {
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
    };


    /**
     * Renders the chessBoard based on the playerColor perspective and the
     * current ChessGame.
     *
     * @param playerColor the playerColor to base render off of.
     */
    public static void drawBoard(String playerColor, ChessGame chessGame, boolean highlightLegalMoves, ChessPosition chessPosition) {
        boolean isWhite = !playerColor.equals("BLACK");
        int[] rowOrder = isWhite ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] colOrder = isWhite ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};
        char[] colNames = new char[]{'a','b','c','d','e','f','g','h'};

        System.out.println();
        System.out.println("    Current Turn: " + chessGame.getTeamTurn());
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_YELLOW + "   ");
        for (int col: colOrder) {
            System.out.print(" " + colNames[col-1] + " ");
        }
        System.out.println("   " + RESET_BG_COLOR + RESET_TEXT_COLOR);

        ChessBoard chessBoard = chessGame.getBoard();
        Collection<ChessMove> validMoves = null;
        if (highlightLegalMoves) {
            validMoves = chessGame.validMoves(chessPosition);
        }

        for (int row: rowOrder) {
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_YELLOW + " " + row + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int col: colOrder) {
                char pieceFromBoard = getPieceFromBoard(chessBoard, row, col);
                CHESS_BOARD[row-1][col-1] = pieceFromBoard;
                char piece = CHESS_BOARD[row-1][col-1];
                String pieceColor = getPieceColor(piece);

                String bgColor = getBgColor(row, col, highlightLegalMoves, chessPosition, validMoves);
                System.out.print(RESET_BG_COLOR + bgColor + " " + pieceColor + " ");
            }
            System.out.print(SET_BG_COLOR_BLACK + " " + SET_TEXT_COLOR_YELLOW + row + " " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_YELLOW + "   ");
        for (int col: colOrder) {
            System.out.print(" " + colNames[col-1] + " ");
        }
        System.out.println("   " + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    /**
     * Returns the given bgColor based on alternation, as well as
     * whether a tile is meant to be highlighted or not.
     *
     * @param row the current row being drawn
     * @param col the current col being drawn
     * @param highlightLegalMoves if we are highlighting moves
     * @param chessPosition the current chessPosition
     * @param validMoves the current validMoves
     * @return the string containing the bgColor
     */
    private static String getBgColor(int row, int col, boolean highlightLegalMoves,
                                     ChessPosition chessPosition, Collection<ChessMove> validMoves) {
        boolean alternate = (row + col) % 2 == 0;
        String bgColor = alternate ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;

        if (!highlightLegalMoves) {
            return bgColor;
        }

        if (chessPosition.getRow() == row && chessPosition.getColumn() == col) {
            return SET_BG_COLOR_YELLOW;
        }
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn() == col) {
                return SET_BG_COLOR_LIGHT_YELLOW;
            }
        }

        return bgColor;
    }

    /**
     * To determine what color is a piece based on whether it's black (lowercase)
     * or white (uppercase).
     *
     * @param piece the piece char
     * @return the formatted string based on the piece
     */
    private static String getPieceColor(char piece) {
        if (piece == ' ') {
            return RESET_TEXT_COLOR + " ";
        }
        if (Character.isUpperCase(piece)) {
            return SET_TEXT_COLOR_WHITE + piece + RESET_TEXT_COLOR;
        } else {
            return SET_TEXT_COLOR_BLACK + piece + RESET_TEXT_COLOR;
        }
    }

    /**
     * Returns the piece we want to assign to our ChessBoardUI from
     * the ChessBoard.
     *
     * @param chessboard the given ChessBoard
     * @param row the current row being drawn
     * @param col the current col being drawn
     * @return the char to assign to the board being drawn
     */
    private static char getPieceFromBoard(ChessBoard chessboard, int row, int col) {
        ChessPiece piece = chessboard.getPiece(new ChessPosition(row, col));

        if (piece == null) {
            return ' ';
        }

        return switch (piece.getPieceType()) {
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 'R' : 'r';
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 'N' : 'n';
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 'B' : 'b';
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 'Q' : 'q';
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 'K' : 'k';
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 'P' : 'p';
        };
    }
}
