package ui.client;

import exception.ResponseException;
import server.ServerFacade;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import java.util.Arrays;

public class PreLoginClient implements Client {
    private static ServerFacade serverFacade;

    public PreLoginClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> new ClientResult("quit", ClientState.SIGNEDOUT, null);
            default -> help();
        };
    }

    public ClientResult help() {
        String helpText =
                """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
        System.out.println(helpText);
        return new ClientResult("help", null, null);
    }

    public ClientResult login(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                var username = params[0];
                var password = params[1];
                LoginRequest loginRequest = new LoginRequest(username, password);
                LoginResult loginResult = serverFacade.login(loginRequest);
                return new ClientResult(String.format("You have logged in as: %s",
                        loginResult.username()), ClientState.SIGNEDIN, loginResult.authToken());
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Not enough arguments");
            return new ClientResult("login", null, null);
        }
    }

    public ClientResult register(String... params) {
        if (params.length == 3) {
            try {
                var username = params[0];
                var password = params[1];
                var email = params[2];
                RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                RegisterResult registerResult = serverFacade.register(registerRequest);
                return new ClientResult("register", ClientState.SIGNEDIN, registerResult.authToken());
            } catch (RuntimeException | ResponseException e) {
                System.out.println("Bad response");
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Not enough arguments");
            return new ClientResult("register", ClientState.SIGNEDOUT, null);
        }
    }
}
