package ui.client;

import exception.ResponseException;
import model.GameData;
import serverfacade.ServerFacade;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.result.CreateGameResult;
import server.net.result.ListGamesResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static ui.EscapeSequences.*;

/**
 * The LoggedInClient handles all the logic for when a user has launched the client and
 * has been logged in but not currently in a chess game.
 */
public class LoggedInClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken = null;

    /**
     * The constructor for the PreLoginClient, which sets up a connection to the server.
     *
     * @param serverUrl the specific server url
     */
    public LoggedInClient(String serverUrl) {
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
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> joinGame(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            default -> help();
        };
    }

    /**
     * Attempts to log out a user based on their authToken
     *
     * @return a generic ClientResult response indicating if the logout
     * was successful or not
     */
    public ClientResult logout() {
        try {
            serverFacade.logout(authToken);
            return new ClientResult("logout", ClientState.SIGNEDOUT, null);
        } catch (ResponseException e) {
            return handleError(e);
        }
    }

    /**
     * Attempts to create a game given a gameName and returns the GameID.
     *
     * @param params the list of user parameters a user may have entered
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     *  and the authToken if received by a response result.
     */
    public ClientResult createGame(String... params) {
        if (params.length == 1) {
            try {
                var gameName = params[0];
                CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
                CreateGameResult createGameResult = serverFacade.createGame(createGameRequest, authToken);
                System.out.printf("    " + SET_TEXT_COLOR_BLUE + "Game created with GameID: " + RESET_TEXT_COLOR + "%d\n",createGameResult.gameID());
                return new ClientResult("create", null, null);
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println("Arguments required: <GameName>");
            return new ClientResult("error", null, null);
        }
    }

    /**
     * Attempts to join a game based on the GameID and on the playerColor
     * that the user has specified.
     *
     * @param params the list of user parameters a user may have entered
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     *  and the authToken if received by a response result.
     */
    public ClientResult joinGame(String... params) {
        if (params.length == 2) {
            try {
                var gameID = Integer.parseInt(params[0]);
                var playerColor = params[1].toUpperCase();
                JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID);
                serverFacade.joinGame(joinGameRequest, authToken);
                return new ClientResult(String.format("join:%s",playerColor), ClientState.INGAME, null);
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println("Arguments required: <GameID> [WHITE|BLACK]");
            return new ClientResult("error", null, null);
        }
    }

    /**
     * Attempts to observe a game based on the GameID and playerColor
     * set to "observe".
     *
     * @param params the list of user parameters a user may have observed
     * @return a ClientResult response containing the result value, the new ClientState (if any),
     *  and the authToken if received by a response result.
     */
    public ClientResult observe(String... params) {
        if (params.length == 1) {
            try {
                var gameID = Integer.parseInt(params[0]);
                JoinGameRequest joinGameRequest = new JoinGameRequest("observe", gameID);
                serverFacade.joinGame(joinGameRequest, authToken);
                return new ClientResult("observe", ClientState.OBSERVER, null);
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println("Arguments required: <GameID> [WHITE|BLACK]");
            return new ClientResult("error", null, null);
        }
    }

    /**
     * Attempts to retrieve the list of games from the server, sorts them from
     * lowest GameID to highest, and prints them out.
     *
     * @return a generic ClientResult response indicating nothing has changed
     */
    public ClientResult listGames() {
        try {
            ListGamesResult listGamesResult = serverFacade.listGames(authToken);
            ArrayList<GameData> games = listGamesResult.games();
            games.sort(Comparator.comparingInt(GameData::gameID));
            StringBuilder gamesListString = listGameStringBuilder(games);
            System.out.print(gamesListString);
            return new ClientResult("listGames", null, null);
        } catch (ResponseException e) {
            return handleError(e);
        }
    }

    /**
     * A method for building the gamesListString in listGames with
     * specific formatting.
     *
     * @param games an ArrayList of GameData
     * @return a constructed StringBuilder string of all the games
     */
    private static StringBuilder listGameStringBuilder(ArrayList<GameData> games) {
        StringBuilder gamesListString = new StringBuilder();
        for (GameData game: games) {
            gamesListString.append(String.format(
                    "    " + SET_TEXT_COLOR_BLUE + "GameID" + RESET_TEXT_COLOR + ": %d " +
                            SET_TEXT_COLOR_BLUE + "GameName" + RESET_TEXT_COLOR + ": %s " +
                            SET_TEXT_COLOR_BLUE + "White" + RESET_TEXT_COLOR + ": %s " +
                            SET_TEXT_COLOR_BLUE + "Black" + RESET_TEXT_COLOR + ": %s\n",
                    game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        return gamesListString;
    }

    /**
     * For displaying all the types of commands that can be run in the eval loop.
     *
     * @return a generic ClientResult response indicating nothing has changed
     */
    public ClientResult help() {
        String helpText =
            "    " + SET_TEXT_COLOR_BLUE + "create <NAME>" + RESET_TEXT_COLOR + " - a game\n" +
            "    " + SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_COLOR + " - games\n" +
            "    " + SET_TEXT_COLOR_BLUE + "join <GameID> [WHITE|BLACK]" + RESET_TEXT_COLOR + " - a game\n" +
            "    " + SET_TEXT_COLOR_BLUE + "observe <GameID>" + RESET_TEXT_COLOR + " - a game to observe\n" +
            "    " + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " - when you are done\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    /**
     * Sets the authToken on the LoggedInClient to be used in the client requests.
     *
     * @param token the authToken
     */
    public void setAuthToken(String token) {
        this.authToken = token;
    }
}
