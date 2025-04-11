package ui;

import ui.client.*;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final LoggedInClient loggedInClient;
    private final InGameClient inGameClient;
    private ClientState state = ClientState.SIGNEDOUT;
    private String authToken = null;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        loggedInClient = new LoggedInClient(serverUrl);
        inGameClient = new InGameClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess. Log-in to start.");
        preLoginClient.help();

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

    private void printPrompt() {
        System.out.print(getStateUiDescriptor());
    }

    private Client getCurrentClient() {
        return switch (state) {
            case SIGNEDOUT -> preLoginClient;
            case SIGNEDIN ->  loggedInClient;
            case INGAME, OBSERVER -> inGameClient;
        };
    }

    private String getStateUiDescriptor() {
        return switch (state) {
            case SIGNEDOUT -> "[LOGGED_OUT] >>> ";
            case SIGNEDIN ->  "[LOGGED_IN] >>> ";
            case INGAME -> "[CHESS_GAME] >>> ";
            case OBSERVER -> "[OBSERVER] >>> ";
        };
    }
}