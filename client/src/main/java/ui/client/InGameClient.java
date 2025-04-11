package ui.client;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;

public class InGameClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken;
    private String playerColor = "WHITE";
    private GameData gameData = null;

    public InGameClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "draw" -> new ClientResult("draw", null, null);
            case "logout" -> logout();
            default -> help();
        };
    }

    public ClientResult logout() throws ResponseException {
        try {
            serverFacade.logout(authToken);
            return new ClientResult("logout", ClientState.SIGNEDOUT, null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientResult help() {
        String helpText =
                """
                draw - redraws chess board
                logout - when you are done
                help - with possible commands
                """;
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void setPlayerColor(String token) {
        this.authToken = token;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
