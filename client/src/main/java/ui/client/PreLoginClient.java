package ui.client;

import java.util.Arrays;

public class PreLoginClient implements Client {
    public PreLoginClient(String serverUrl) {

    }

    @Override
    public ClientResult eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> new ClientResult("quit", ClientState.SIGNEDOUT);
            default -> help();
        };
    }

    @Override
    public ClientResult help() {
        return new ClientResult("help", null);
    }

    public ClientResult login(String... params) {
        return new ClientResult("login", ClientState.SIGNEDIN);
    }

    public ClientResult register(String... params) {
        return new ClientResult("register", ClientState.SIGNEDIN);
    }
}
