package ui.client;

import server.ServerFacade;

public class InGameClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken;
    private String playerColor = "WHITE";

    public InGameClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) {
        return new ClientResult("inGameEval", null, null);
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void setPlayerColor(String token) {
        this.authToken = token;
    }
}
