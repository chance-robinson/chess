package ui;

import ui.client.*;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The Read,Eval,Print,Loop class that handlers all the user states and interactions
 * between the 3 specific clients: PreLogin, LoggedIn, and InGame, as well as all the
 * terminal display logic.
 */
public class Repl {
    private final PreLoginClient preLoginClient;
    private final LoggedInClient loggedInClient;
    private final InGameClient inGameClient;
    private ClientState state = ClientState.SIGNEDOUT;
    private String authToken = null;

    /**
     * The constructor of the Repl class to initialize
     * the 3 clients: PreLogin, LoggedIn, and InGame.
     *
     * @param serverUrl the server url of the chess server
     */
    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        loggedInClient = new LoggedInClient(serverUrl);
        inGameClient = new InGameClient(serverUrl);
    }

    /**
     * Contains the REPL looping logic
     */
    public void run() {
        System.out.println("Welcome to 240 Chess. Type help to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                ClientResult clientResult = getCurrentClient().eval(line);
                result = clientResult.result();

                if (clientResult.updatedState() != null && state != clientResult.updatedState()) {
                    state = clientResult.updatedState();
                    if (clientResult.updatedState() != ClientState.SIGNEDOUT && clientResult.authToken() != null) {
                        authToken = clientResult.authToken();
                    } else if (clientResult.updatedState() == ClientState.SIGNEDOUT) {
                        authToken = null;
                    }
                    loggedInClient.setAuthToken(authToken);
                    inGameClient.setAuthToken(authToken);
                }
                if ("join:black".equalsIgnoreCase(result)) {
                    inGameClient.setPlayerColor("BLACK");
                } else {
                    inGameClient.setPlayerColor("WHITE");
                }
                if (getCurrentClient() instanceof InGameClient && !List.of("help", "logout", "quit").contains(result)) {
                    ChessBoardUI.drawBoard(inGameClient.getPlayerColor());
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        exit(0);
    }

    /**
     * Print out the UI state descriptor like "[LOGGED_IN] >>> "
     */
    private void printPrompt() {
        System.out.print(getStateUiDescriptor());
    }

    /**
     * Gets the current client that a user should be making
     * an eval on depending on the state.
     *
     * @return the Client to run commands on
     */
    private Client getCurrentClient() {
        return switch (state) {
            case SIGNEDOUT -> preLoginClient;
            case SIGNEDIN ->  loggedInClient;
            case INGAME, OBSERVER -> inGameClient;
        };
    }

    /**
     * Returns the string of the UI state descriptor for printing out
     * after every prompt is given.
     *
     * @return the string prompt prefix
     */
    private String getStateUiDescriptor() {
        return switch (state) {
            case SIGNEDOUT -> "[LOGGED_OUT] >>> ";
            case SIGNEDIN ->  "[LOGGED_IN] >>> ";
            case INGAME -> "[CHESS_GAME] >>> ";
            case OBSERVER -> "[OBSERVER] >>> ";
        };
    }
}