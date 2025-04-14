package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import ui.client.*;
import ui.client.websocket.NotificationHandler;
import ui.client.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static java.lang.System.exit;
import static ui.EscapeSequences.*;
/**
 * The Read,Eval,Print,Loop class that handlers all the user states and interactions
 * between the 3 specific clients: PreLogin, LoggedIn, and InGame, as well as all the
 * terminal display logic.
 */
public class Repl implements NotificationHandler {
    private final PreLoginClient preLoginClient;
    private final LoggedInClient loggedInClient;
    private final InGameClient inGameClient;
    private ClientState state = ClientState.SIGNEDOUT;
    private AuthData authData = null;
    private WebSocketFacade ws;

    /**
     * The constructor of the Repl class to initialize
     * the 3 clients: PreLogin, LoggedIn, and InGame.
     *
     * @param serverUrl the server url of the chess server
     */
    public Repl(String serverUrl) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, this);
        preLoginClient = new PreLoginClient(serverUrl);
        loggedInClient = new LoggedInClient(serverUrl, ws);
        inGameClient = new InGameClient(serverUrl, ws, state);
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
                        authData = preLoginClient.getAuthData();
                    } else if (clientResult.updatedState() == ClientState.SIGNEDOUT) {
                        authData = null;
                    }
                    loggedInClient.setAuthData(authData);
                    inGameClient.setAuthData(authData);
                }
            } catch (Throwable e) {
                System.out.printf(SET_TEXT_COLOR_RED + "    Error has occurred with the previous command.\n" + RESET_TEXT_COLOR);
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

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGameData());
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getNotificationMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
        }
    }

    private void displayError(String errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED + SET_TEXT_BOLD + "\n    " + errorMessage
                + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        printPrompt();
    }

    private void displayNotification(String notificationMessage) {
        System.out.println(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "\n    " + notificationMessage
                + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        printPrompt();
    }

    private void loadGame(GameData gameData) {
        String playerColor;
        if (authData.username().equals(gameData.whiteUsername())) {
            playerColor = "WHITE";
        } else if (authData.username().equals(gameData.blackUsername())) {
            playerColor = "BLACK";
        } else {
            playerColor = "WHITE";
        }

        inGameClient.setPlayerColor(playerColor);
        inGameClient.setGameData(gameData);
        inGameClient.eval("REDRAW");
        printPrompt();
    }
}