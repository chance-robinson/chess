package ui.client;

import exception.ResponseException;
import serverfacade.ServerFacade;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginClient implements Client {
    private static ServerFacade serverFacade;

    public PreLoginClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) {
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
            "    " + SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR + " - to create an account\n" +
            "    " + SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR + " - to play chess\n" +
            "    " + SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " - playing chess\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    public ClientResult login(String... params)  {
        if (params.length == 2) {
            try {
                var username = params[0];
                var password = params[1];
                LoginRequest loginRequest = new LoginRequest(username, password);
                LoginResult loginResult = serverFacade.login(loginRequest);
                System.out.printf("    " + SET_TEXT_COLOR_BLUE + "You have logged in as: " + RESET_TEXT_COLOR + "%s\n", loginResult.username());
                return new ClientResult("login", ClientState.SIGNEDIN, loginResult.authToken());
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println("Arguments required: <USERNAME> <PASSWORD>");
            return new ClientResult("error", null, null);
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
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println("Arguments required: <USERNAME> <PASSWORD> <EMAIL>");
            return new ClientResult("error", null, null);
        }
    }
}
