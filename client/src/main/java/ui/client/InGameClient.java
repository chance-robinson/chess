package ui.client;

public class InGameClient implements Client {
    public InGameClient(String serverUrl) {

    }

    @Override
    public ClientResult eval(String input) {
        return new ClientResult("inGameEval", null);
    }

    @Override
    public ClientResult help() {
        return new ClientResult("help", null);
    }
}
