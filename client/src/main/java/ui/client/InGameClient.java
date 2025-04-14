package ui.client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import serverfacade.ServerFacade;
import ui.ChessBoardUI;
import ui.client.websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Arrays;

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
