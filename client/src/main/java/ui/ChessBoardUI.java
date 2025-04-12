package ui;

import static ui.EscapeSequences.*;

public class ChessBoardUI {
    private static boolean isWhite = true;

    private static final char[][] DEFAULT_BOARD  = {
        {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'},
        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
        {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}
    };

    public static void drawBoard(String playerColor) {
        isWhite = !playerColor.equals("BLACK");
        int[] rowOrder = isWhite ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] colOrder = isWhite ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};
        char[] colNames = new char[]{'a','b','c','d','e','f','g','h'};

        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_YELLOW + "   ");
        for (int col: colOrder) {
            System.out.print(" " + colNames[col-1] + " ");
        }
        System.out.println("   " + RESET_BG_COLOR + RESET_TEXT_COLOR);

        for (int row: rowOrder) {
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_YELLOW + " " + row + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int col: colOrder) {
                char piece = DEFAULT_BOARD[row-1][col-1];
                String pieceColor = getPieceColor(piece);

                boolean alternate = (row + col) % 2 == 0;
                String bgColor = alternate ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_GREEN;
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
}
