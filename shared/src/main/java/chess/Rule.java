package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class Rule {
    private final boolean canRepeat;
    private final int[][] directions;

    public Rule(boolean canRepeat, int[][] directions) {
        this.canRepeat = canRepeat;
        this.directions = directions;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "canRepeat=" + canRepeat +
                ", directions=" + Arrays.toString(directions) +
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
        Rule rule = (Rule) o;
        return canRepeat == rule.canRepeat && Objects.deepEquals(directions, rule.directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(canRepeat, Arrays.deepHashCode(directions));
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        ChessPiece pieceAtCurPosition = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()));
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        if (pieceAtCurPosition.getPieceType() == ChessPiece.PieceType.PAWN) {
            return getPawnMoves(board, myPosition);
        }

        for (int[] direction : directions) {
            int tempRow = startRow;
            int tempCol = startCol;
            do {
                tempRow += direction[0];
                tempCol += direction[1];

                if (!board.isValidPosition(new ChessPosition(tempRow, tempCol))) {
                    break;
                }

                ChessPiece pieceAtNewPosition = board.getPiece(new ChessPosition(tempRow, tempCol));
                if (pieceAtNewPosition != null && pieceAtCurPosition.getTeamColor() == pieceAtNewPosition.getTeamColor()) {
                    break;
                }
                if (pieceAtNewPosition != null && pieceAtCurPosition.getTeamColor() != pieceAtNewPosition.getTeamColor()) {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow, tempCol), null));
                    break;
                }
                chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow, tempCol), null));
            } while (canRepeat);
        }

        return chessMoves;
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        ChessPiece pieceAtCurPosition = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()));
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        int direction = pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int tempRow1 = startRow + direction;
        int tempRow2 = startRow + (direction*2);

        // one move
        // promotion rules
        if (board.isValidPosition(new ChessPosition(tempRow1, startCol))) {
            ChessPiece pieceAtNewPosition1 = board.getPiece(new ChessPosition(tempRow1, startCol));
            if (pieceAtNewPosition1 == null) {
                if ((tempRow1 == 1 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.BLACK) ||
                        (tempRow1 == 8 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.WHITE)) {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol), ChessPiece.PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol), ChessPiece.PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol), ChessPiece.PieceType.KNIGHT));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol), ChessPiece.PieceType.QUEEN));
                } else {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol), null));
                }
            }
        }

        // two moves
        if (board.isValidPosition(new ChessPosition(tempRow1, startCol)) && board.isValidPosition(new ChessPosition(tempRow2, startCol))) {
            ChessPiece pieceAtNewPosition1 = board.getPiece(new ChessPosition(tempRow1, startCol));
            ChessPiece pieceAtNewPosition2 = board.getPiece(new ChessPosition(tempRow2, startCol));
            if (pieceAtNewPosition1 == null && pieceAtNewPosition2 == null) {
                if ((startRow == 7 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.BLACK) ||
                        (startRow == 2 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.WHITE)) {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow2, startCol), null));
                }
            }
        }

        // capture left and right
        // promotion rules
        if (board.isValidPosition(new ChessPosition(tempRow1, startCol+1))) {
            ChessPiece pieceAtNewPosition1 = board.getPiece(new ChessPosition(tempRow1, startCol+1));
            if (pieceAtNewPosition1 != null && pieceAtCurPosition.getTeamColor() != pieceAtNewPosition1.getTeamColor()) {
                if ((tempRow1 == 1 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.BLACK) ||
                        (tempRow1 == 7 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.BLACK)) {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol+1), ChessPiece.PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol+1), ChessPiece.PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol+1), ChessPiece.PieceType.KNIGHT));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol+1), ChessPiece.PieceType.QUEEN));
                } else {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol+1), null));
                }
            }
        }

        if (board.isValidPosition(new ChessPosition(tempRow1, startCol-1))) {
            ChessPiece pieceAtNewPosition1 = board.getPiece(new ChessPosition(tempRow1, startCol-1));
            if (pieceAtNewPosition1 != null && pieceAtCurPosition.getTeamColor() != pieceAtNewPosition1.getTeamColor()) {
                if ((tempRow1 == 1 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.BLACK) ||
                        (tempRow1 == 8 && pieceAtCurPosition.getTeamColor() == ChessGame.TeamColor.WHITE)) {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol-1), ChessPiece.PieceType.ROOK));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol-1), ChessPiece.PieceType.BISHOP));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol-1), ChessPiece.PieceType.KNIGHT));
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol-1), ChessPiece.PieceType.QUEEN));
                } else {
                    chessMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow1, startCol-1), null));
                }
            }
        }

        return chessMoves;
    }
}
