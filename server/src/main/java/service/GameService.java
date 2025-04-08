package service;

import chess.ChessGame;
import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import model.AuthData;
import model.GameData;
import server.ServerException;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.result.CreateGameResult;
import server.net.result.EmptyResult;
import server.net.result.ListGamesResult;

import java.util.Objects;

public class GameService {
    final GameDAO gameDAO;
    final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws ServerException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new ServerException("Error: unauthorized", 401);
        }

        return new ListGamesResult(gameDAO.getAllGames());
    }

    public CreateGameResult createGame(CreateGameRequest req, String authToken) throws ServerException {
        String gameName = req.gameName();
        if (authToken == null) {
            throw new ServerException("Error: bad request", 400);
        }

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new ServerException("Error: unauthorized", 401);
        }
        if (gameDAO.getGameByGameName(gameName) != null) {
            throw new ServerException("Error: bad request", 400);
        }

        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(
                gameDAO.generateGameID(),
                null,
                null,
                gameName,
                chessGame);

        gameDAO.createGame(gameData);

        return new CreateGameResult(gameData.gameID());
    }

    public EmptyResult joinGame(JoinGameRequest req, String authToken) throws ServerException {
        String playerColor = req.playerColor();
        int gameId = req.gameID();

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new ServerException("Error: unauthorized", 401);
        }

        if (gameId == 0 || playerColor == null) {
            throw new ServerException("Error: bad request", 400);
        }

        GameData gameData = gameDAO.getGame(gameId);
        if (gameData == null) {
            throw new ServerException("Error: bad request", 400);
        }

        if (Objects.equals(playerColor, "BLACK")) {
            if (gameData.blackUsername() == null) {
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
            } else {
                throw new ServerException("Error: already taken", 403);
            }
        } else if (Objects.equals(playerColor, "WHITE")) {
            if (gameData.whiteUsername() == null) {
                gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else {
                throw new ServerException("Error: already taken", 403);
            }
        }
        else {
            throw new ServerException("Error: bad request", 400);
        }

        gameDAO.update(gameData);

        return new EmptyResult();
    }
}
