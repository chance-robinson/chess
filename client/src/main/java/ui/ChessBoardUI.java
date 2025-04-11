package ui;

public class ChessBoardUI {
    private static boolean isWhite = true;

    private static final char[][] default_board = {
        {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
        {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };

    public static void drawBoard(String playerColor) {
        isWhite = playerColor.equals("WHITE");
        System.out.printf("Viewing: %s", playerColor);
        System.out.println();
        int[] rowOrder = isWhite ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] colOrder = isWhite ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};
        char[] colNames = new char[]{'a','b','c','d','e','f','g','h'};

        for (int row: rowOrder) {
            System.out.print(row + " ");
            for (int col: colOrder) {
                System.out.print("|" + default_board[row-1][col-1]);
            }
            System.out.print("|\n");
        }

        System.out.print(" ");
        for (int col: colOrder) {
            System.out.print(" " + colNames[col-1]);
        }
        System.out.println();
    }
}
