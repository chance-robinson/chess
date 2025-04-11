package ui.client;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.result.CreateGameResult;
import server.net.result.ListGamesResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class LoggedInClient implements Client {
    private static ServerFacade serverFacade;
    private String authToken = null;

    public LoggedInClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> joinGame(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "quit" -> new ClientResult("quit", ClientState.SIGNEDOUT, null);
            default -> help();
        };
    }

    public ClientResult logout() throws ResponseException {
        try {
            serverFacade.logout(authToken);
            return new ClientResult("logout", ClientState.SIGNEDOUT, null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientResult createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                var gameName = params[0];
                CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
                CreateGameResult createGameResult = serverFacade.createGame(createGameRequest, authToken);
                System.out.printf("Game created with ID: %d",createGameResult.gameID());
                return new ClientResult("create", null, null);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Not enough arguments");
            return new ClientResult("create", null, null);
        }
    }

    public ClientResult joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                var gameID = Integer.parseInt(params[0]);
                var playerColor = params[1].toUpperCase();
                JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID);
                serverFacade.joinGame(joinGameRequest, authToken);
                return new ClientResult(String.format("join:%s",playerColor), ClientState.INGAME, null);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Not enough arguments");
            return new ClientResult("join", null, null);
        }
    }

    public ClientResult observe(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                var gameID = Integer.parseInt(params[0]);
                JoinGameRequest joinGameRequest = new JoinGameRequest(null, gameID);
                serverFacade.joinGame(joinGameRequest, authToken);
                return new ClientResult("observe", ClientState.OBSERVER, null);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Not enough arguments");
            return new ClientResult("observe", null, null);
        }
    }

    public ClientResult listGames() throws ResponseException {
        try {
            ListGamesResult listGamesResult = serverFacade.listGames(authToken);
            ArrayList<GameData> games = listGamesResult.games();
            games.sort(Comparator.comparingInt(GameData::gameID));
            StringBuilder gamesListString = new StringBuilder();
            for (GameData game: games) {
                gamesListString.append(String.format("GameID: %d, GameName: %s, White: %s, Black: %s\n",
                        game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername()));
            }
            System.out.print(gamesListString);
            return new ClientResult("listGames", null, null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientResult help() {
        String helpText =
                """
                create <NAME> - a game
                list - games
                join <GameID> [WHITE|BLACK] - a game
                observe <GameID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
        System.out.println(helpText);
        return new ClientResult("help", null, null);
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }
}
