package ui.client;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import serverfacade.ServerFacade;
import ui.ChessBoardUI;
import ui.client.websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

/**
 * The InGameClient handles all the logic for when a user has launched the client and
 * has entered a game either as an observer or player, allowing them to view the current
 * chess board from the given playerColor perspective.
 */
public class InGameClient implements Client {
    private static ServerFacade serverFacade;
    private AuthData authData;
    private String playerColor = "WHITE";
    private GameData gameData = null;
    private final WebSocketFacade ws;
    private ClientState state;

    /**
     * The constructor for the InGameClient, which sets up a connection to the server.
     *
     * @param serverUrl the specific server url
     */
    public InGameClient(String serverUrl,  WebSocketFacade ws, ClientState state) {
        serverFacade = new ServerFacade(serverUrl);
        this.ws = ws;
        this.state = state;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    /**
     * Evaluates a specific input string as a command.
     *
     * @param input the command to evaluate on
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     * and the authToken if received by a response result.
     */
    @Override
    public ClientResult eval(String input) throws IOException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "redraw" -> redraw();
            case "move" -> makeMove(params);
            case "leave" -> leave();
            case "resign" -> resign();
            case "logout" -> logout();
            default -> help();
        };
    }

    private ClientResult resign() throws IOException {
        if (this.state == ClientState.INGAME) {
            ws.resign(authData.authToken(), gameData.gameID());
        } else {
            System.out.println(SET_TEXT_COLOR_YELLOW + "    " + "Only a player can resign." + RESET_TEXT_COLOR);
        }
        return new ClientResult("resign", null, null);
    }

    private ClientResult redraw() {
        ChessBoardUI.drawBoard(playerColor);
        return new ClientResult("redraw", null, null);
    }

    private ClientResult makeMove(String... params) throws IOException {
        if (params.length == 2) {
            var startPos = params[0].toLowerCase();
            var endPos = params[1].toLowerCase();
            if (!isValidPosition(startPos) || !isValidPosition(endPos)) {
                System.out.println(SET_TEXT_COLOR_YELLOW + "    Invalid move format. Use positions like 'e2' or 'h7'." + RESET_TEXT_COLOR);
                return new ClientResult("error", null, null);
            }
            int startRow, startCol, endRow, endCol;
            try {
                startCol = startPos.charAt(0) - 'a' + 1;
                startRow = Integer.parseInt(String.valueOf(startPos.charAt(1)));
                endRow = Integer.parseInt(String.valueOf(endPos.charAt(1)));
                endCol = endPos.charAt(0) - 'a' + 1;
                if (startRow < 1 || startRow > 8 || startCol < 1 || startCol > 8
                        || endRow < 1 || endRow > 8 || endCol < 1 || endCol > 8) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println(SET_TEXT_COLOR_YELLOW + "    " + "Need <START_POS> and <END_POS> to be within "
                        + "1-8 for rows and a-h for columns in format <Column,Row> for each position." + RESET_TEXT_COLOR);
                return new ClientResult("error", null, null);
            }
            ChessPosition startPosition = new ChessPosition(startRow, startCol);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);
            ChessPiece.PieceType promotionPiece = null;
            ChessPiece pieceType = gameData.game().getBoard().getPiece(startPosition);
            ChessPiece.PieceType pieceType1 = pieceType.getPieceType();
            String teamColor = pieceType.getTeamColor().toString();
            if (!Objects.equals(teamColor, playerColor)) {
                System.out.println(SET_TEXT_COLOR_YELLOW + "    Invalid starting position" + RESET_TEXT_COLOR);
                return new ClientResult("makeMove", null, null);
            }
            if ((Objects.equals(teamColor, "WHITE") && endRow == 8 && pieceType1 == ChessPiece.PieceType.PAWN) ||
                    ((Objects.equals(teamColor, "BLACK") && endRow == 1 && pieceType1 == ChessPiece.PieceType.PAWN))) {
                while (promotionPiece == null) {
                    promotionPiece = getPromotionPiece();
                    System.out.println(SET_TEXT_COLOR_YELLOW + "    Select correct promotion piece");
                }
            }

            ChessMove chessMove = new ChessMove(startPosition, endPosition, promotionPiece);
            if (!gameData.game().validMoves(startPosition).contains(chessMove)) {
                System.out.println(SET_TEXT_COLOR_YELLOW + "\n    Invalid move" + RESET_TEXT_COLOR);
                return new ClientResult("makeMove", null, null);
            }
            ws.makeMove(authData.authToken(), gameData.gameID(), chessMove);
            return new ClientResult("makeMove", null, null);
        }
        else {
            System.out.println(SET_TEXT_COLOR_YELLOW + "    " + "Arguments required: move <START_POS> <END_POSITION>" + RESET_TEXT_COLOR);
            return new ClientResult("error", null, null);
        }
    }

    private ChessPiece.PieceType getPromotionPiece() {
        System.out.println(SET_TEXT_COLOR_BLUE + "    Promotion piece selection available:");
        System.out.println(SET_TEXT_COLOR_BLUE + "        rook");
        System.out.println(SET_TEXT_COLOR_BLUE + "        knight");
        System.out.println(SET_TEXT_COLOR_BLUE + "        bishop");
        System.out.println(SET_TEXT_COLOR_BLUE + "        queen" + RESET_TEXT_COLOR);
        Scanner scanner = new Scanner(System.in);
        String eval = scanner.nextLine();
        return switch (eval) {
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Select either: \"rook, knight, bishop, or queen\"");
                yield getPromotionPiece();
            }
        };
    }

    private boolean isValidPosition(String pos) {
        if (pos.length() != 2) {
            return false;
        }
        char file = pos.charAt(0);
        char rank = pos.charAt(1);

        return (file >= 'a' && file <= 'h') && (rank >= '1' && rank <= '8');
    }

    private ClientResult leave() {
        try {
            String playerColorType = state == ClientState.OBSERVER ? "OBSERVER" : playerColor;
            ws.leave(authData.authToken(), gameData.gameID(), playerColorType);
            System.out.println(SET_TEXT_COLOR_GREEN + "    " + "You have left the game." + RESET_TEXT_COLOR);
            return new ClientResult("logout", ClientState.SIGNEDIN, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to log out a user based on their authToken
     *
     * @return a generic ClientResult response indicating if the logout
     * was successful or not
     */
    public ClientResult logout() {
        try {
            serverFacade.logout(authData.authToken());
            return new ClientResult("logout", ClientState.SIGNEDOUT, null);
        } catch (ResponseException e) {
            return handleError(e);
        }
    }

    /**
     * For displaying all the types of commands that can be run in the eval loop.
     *
     * @return a generic ClientResult response indicating nothing has changed
     */
    public ClientResult help() {
        String helpText =
            "    " + SET_TEXT_COLOR_BLUE + "redraw" + RESET_TEXT_COLOR + " - redraws chess board\n" +
            "    " + SET_TEXT_COLOR_BLUE + "move" + RESET_TEXT_COLOR + " - make move using format \"move <START_POS> <END_POS>\"" +
                    " where each position is in format <Column, Row>\n" +
            "    " + SET_TEXT_COLOR_BLUE + "leave" + RESET_TEXT_COLOR + " - leave chess game\n" +
            "    " + SET_TEXT_COLOR_BLUE + "resign" + RESET_TEXT_COLOR + " - forfeit the game and lose\n" +
            "    " + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " - when you are done\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    /**
     * Sets the authToken on the LoggedInClient to be used in the client requests.
     *
     * @param authData the authData
     */
    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    /**
     * Sets the playerColor of the user to be used in InGameClient.
     *
     * @param playerColor the playerColor to set
     */
    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public void setState(ClientState clientState) {
        this.state = clientState;
    }
}
