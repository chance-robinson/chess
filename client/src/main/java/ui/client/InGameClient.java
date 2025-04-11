package ui.client;

import exception.ResponseException;
import model.GameData;
import serverFacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class InGameClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken;
    private String playerColor = "WHITE";
    private GameData gameData = null;

    public InGameClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "draw" -> new ClientResult("draw", null, null);
            case "logout" -> logout();
            default -> help();
        };
    }

    public ClientResult logout() {
        try {
            serverFacade.logout(authToken);
            return new ClientResult("logout", ClientState.SIGNEDOUT, null);
        } catch (ResponseException e) {
            return handleError(e);
        }
    }

    public ClientResult help() {
        String helpText =
            "    " + SET_TEXT_COLOR_BLUE + "draw" + RESET_TEXT_COLOR + " - redraws chess board\n" +
            "    " + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " - when you are done\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
