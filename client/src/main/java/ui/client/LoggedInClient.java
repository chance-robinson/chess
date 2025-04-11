package ui.client;

public class LoggedInClient implements Client {
    public LoggedInClient(String serverUrl) {

    }

    @Override
    public ClientResult eval(String input) {
        return new ClientResult("loggedInEval", null, null);
    }
}
