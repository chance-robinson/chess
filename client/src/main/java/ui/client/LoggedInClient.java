package ui.client;

import server.ServerFacade;

public class LoggedInClient implements Client {
    private static ServerFacade serverFacade;

    public LoggedInClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) {
        return new ClientResult("loggedInEval", null, null);
    }
}
