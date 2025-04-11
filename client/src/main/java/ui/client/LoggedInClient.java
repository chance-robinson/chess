package ui.client;

import exception.ResponseException;
import model.GameData;
import serverFacade.ServerFacade;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.result.CreateGameResult;
import server.net.result.ListGamesResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static ui.EscapeSequences.*;

public class LoggedInClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken = null;

    public LoggedInClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> joinGame(params);
//            case "observe" -> observe(params);
            case "logout" -> logout();
            default -> help();
        };
    }

    public ClientResult logout() {
        try {
            serverFacade.logout(authToken);
            return new ClientResult("logout", ClientState.SIGNEDOUT, null);
        } catch (ResponseException e) {
            return handleError(e);
        }
    }

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

    // not implemented yet
    public ClientResult observe(String... params) {
        if (params.length == 1) {
            try {
                var gameID = Integer.parseInt(params[0]);
                JoinGameRequest joinGameRequest = new JoinGameRequest(null, gameID);
                serverFacade.joinGame(joinGameRequest, authToken);
                return new ClientResult("observe", ClientState.OBSERVER, null);
            } catch (ResponseException e) {
                return handleError(e);
            }
        } else {
            System.out.println("Not enough arguments");
            return new ClientResult("observe", null, null);
        }
    }

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

    public ClientResult help() {
        String helpText =
            "    " + SET_TEXT_COLOR_BLUE + "create <NAME>" + RESET_TEXT_COLOR + " - a game\n" +
            "    " + SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_COLOR + " - games\n" +
            "    " + SET_TEXT_COLOR_BLUE + "join <GameID> [WHITE|BLACK]" + RESET_TEXT_COLOR + " - a game\n" +
            "    " + SET_TEXT_COLOR_RED + "observe <GameID>" + RESET_TEXT_COLOR + " - a game (not currently implemented)\n" +
            "    " + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " - when you are done\n" +
            "    " + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - with possible commands\n";
        System.out.print(helpText);
        return new ClientResult("help", null, null);
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }
}
