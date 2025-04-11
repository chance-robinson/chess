package ui;

import ui.client.*;

import java.util.Objects;
import java.util.Scanner;

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
        System.out.println("\uD83D\uDC36 Welcome to Chess. Log-in to start.");
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
                    loggedInClient.setAuthToken(clientResult.authToken());
                    inGameClient.setAuthToken(clientResult.authToken());
                }
                if ("join:WHITE".equals(result) || "join:BLACK".equals(result)) {
                    inGameClient.setPlayerColor(result);
                } else {
                    inGameClient.setPlayerColor("WHITE");
                }
                if (state == ClientState.SIGNEDOUT) {
                    authToken = null;
                } else if (!Objects.equals(clientResult.authToken(), authToken) && authToken != null) {
                    authToken = clientResult.authToken();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
//        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + getStateUiDescriptor());
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