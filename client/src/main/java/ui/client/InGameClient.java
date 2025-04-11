package ui.client;

import server.ServerFacade;

public class InGameClient implements Client {
    private static ServerFacade serverFacade;

    public InGameClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) {
        return new ClientResult("inGameEval", null, null);
    }
}
