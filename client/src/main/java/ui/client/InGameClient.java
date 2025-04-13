package ui.client;

import exception.ResponseException;
import model.GameData;
import serverfacade.ServerFacade;
import ui.client.websocket.NotificationHandler;
import ui.client.websocket.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

/**
 * The InGameClient handles all the logic for when a user has launched the client and
 * has entered a game either as an observer or player, allowing them to view the current
 * chess board from the given playerColor perspective.
 */
public class InGameClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken;
    private String playerColor = "WHITE";
    private GameData gameData = null;
    private WebSocketFacade ws;

    /**
     * The constructor for the InGameClient, which sets up a connection to the server.
     *
     * @param serverUrl the specific server url
     */
    public InGameClient(String serverUrl, NotificationHandler notificationHandler) throws ResponseException {
        serverFacade = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, notificationHandler);
    }

    /**
     * Evaluates a specific input string as a command.
     *
     * @param input the command to evaluate on
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     * and the authToken if received by a response result.
     */
    @Override
    public ClientResult eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "redraw" -> new ClientResult("redraw", null, null);
            case "logout" -> logout();
            default -> help();
        };
    }

    /**
     * Attempts to log out a user based on their authToken
     *
     * @return a generic ClientResult response indicating if the logout
     * was successful or not
     */
    public ClientResult logout() {
        try {
            serverFacade.logout(authToken);
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
            "    " + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " - when you are done\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    /**
     * Sets the authToken on the LoggedInClient to be used in the client requests.
     *
     * @param token the authToken
     */
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    /**
     * Sets the playerColor of the user to be used in InGameClient.
     *
     * @param playerColor the playerColor to set
     */
    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }
    /**
     * Returns the playerColor of the user from the InGameClient.
     *
     * @return the playerColor
     */
    public String getPlayerColor() {
        return playerColor;
    }
}
