package ui.client;

import exception.ResponseException;
import model.AuthData;
import serverfacade.ServerFacade;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import java.util.Arrays;

import static ui.EscapeSequences.*;

/**
 * The PreLoginClient handles all the logic for when a user has launched the client and
 * hasn't logged in yet.
 */
public class PreLoginClient implements Client {
    private static ServerFacade serverFacade;
    private AuthData authData;

    /**
     * The constructor for the PreLoginClient, which sets up a connection to the server.
     *
     * @param serverUrl the specific server url
     */
    public PreLoginClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    /**
     * Evaluates a specific input string as a command.
     *
     * @param input the command to evaluate on
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     * and the authToken if received by a response result.
     */
    @Override
    public ClientResult eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> new ClientResult("quit", null, null);
            default -> help();
        };
    }

    /**
     * For displaying all the types of commands that can be run in the eval loop.
     *
     * @return a generic ClientResult response indicating nothing has changed
     */
    public ClientResult help() {
        String helpText =
            "    " + SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR + " - to create an account\n" +
            "    " + SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR + " - to play chess\n" +
            "    " + SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " - playing chess\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    /**
     * Attempts to log in a user give the params.
     *
     * @param params the list of user parameters a user may have entered
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     *  and the authToken if received by a response result.
     */
    public ClientResult login(String... params)  {
        if (params.length == 2) {
            try {
                var username = params[0];
                var password = params[1];
                LoginRequest loginRequest = new LoginRequest(username, password);
                LoginResult loginResult = serverFacade.login(loginRequest);
                this.authData = new AuthData(loginResult.authToken(), loginResult.username());
                System.out.printf("    " + SET_TEXT_COLOR_GREEN + "You have logged in as: " + RESET_TEXT_COLOR + "%s\n", loginResult.username());
                return new ClientResult("login", ClientState.SIGNEDIN, loginResult.authToken());
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println(SET_TEXT_COLOR_YELLOW + "    " + "Arguments required: login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR);
            return new ClientResult("error", null, null);
        }
    }

    /**
     * Attempts to register a user given the params.
     *
     * @param params the list of user parameters a user may have entered
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     * and the authToken if received by a response result.
     */
    public ClientResult register(String... params) {
        if (params.length == 3) {
            try {
                var username = params[0];
                var password = params[1];
                var email = params[2];
                RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                RegisterResult registerResult = serverFacade.register(registerRequest);
                this.authData = new AuthData(registerResult.authToken(), registerRequest.username());
                return new ClientResult("register", ClientState.SIGNEDIN, registerResult.authToken());
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println(SET_TEXT_COLOR_YELLOW + "    " + "Arguments required: register <USERNAME> <PASSWORD> <EMAIL>"
                    + RESET_TEXT_COLOR);
            return new ClientResult("error", null, null);
        }
    }

    /**
     * Sets the authData on the REPL class to be shared across all clients.
     *
     * @return the AuthData for the logged-in user
     */
    public AuthData getAuthData() {
        return authData;
    }
}
