package service;

import chess.ChessGame;
import dataAccess.dao.AuthDAO;
import dataAccess.dao.GameDAO;
import dataAccess.dao.UserDAO;
import dataAccess.dao.memory.MemoryAuthDAO;
import dataAccess.dao.memory.MemoryGameDAO;
import dataAccess.dao.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.request.ListGamesRequest;
import server.net.result.CreateGameResult;
import server.net.result.JoinGameResult;
import server.net.result.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private GameService gameService;
    private UserData testUser;
    private AuthData testAuth;
    private AuthData testAuth2;

    @BeforeEach
    public void initialize() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, gameDAO, authDAO);
        gameService = new GameService(userDAO, gameDAO, authDAO);
        testUser = new UserData("testUser", "testPass", "test@example.com");
        testAuth = new AuthData("testAuth", "testUser");
        testAuth2 = new AuthData("testAuth2", "testUser2");
    }

    @Test
    public void clear() {
        userDAO.createUser(testUser);

        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        authDAO.createAuth(testAuth.authToken(), testAuth);

        assertNotNull(userDAO.getUser(testUser.username()));
        assertNotNull(gameDAO.getGame(generatedGameID));
        assertNotNull(authDAO.getAuth(testAuth.authToken()));

        userService.clear();

        assertNull(userDAO.getUser(testUser.username()));
        assertNull(gameDAO.getGame(generatedGameID));
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    public void createGame() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest request = new CreateGameRequest("testGame", testAuth.authToken());
        CreateGameResult result = gameService.createGame(request);

        assertNotNull(result.gameID());
    }

    @Test
    public void createGame_duplicateGameName() {
        String gameName = "testGame";

        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest_1 = new CreateGameRequest(gameName, testAuth.authToken());
        CreateGameResult createGameResult_1 = gameService.createGame(createGameRequest_1);
        assertNotNull(createGameResult_1.gameID());

        CreateGameRequest createGameRequest_2 = new CreateGameRequest(gameName, testAuth.authToken());

        ServerException exception = assertThrows(ServerException.class, () -> {
            gameService.createGame(createGameRequest_2);
        });

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    public void createGame_badAuthToken() {
        String gameName = "testGame";

        CreateGameRequest createGameRequest_1 = new CreateGameRequest(gameName, "badAuthToken");
        ServerException exception = assertThrows(ServerException.class, () -> {
            gameService.createGame(createGameRequest_1);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void listGames() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest_1 = new CreateGameRequest("testGame1", testAuth.authToken());
        CreateGameResult createGameResult_1 = gameService.createGame(createGameRequest_1);
        assertNotNull(createGameResult_1.gameID());

        CreateGameRequest createGameRequest_2 = new CreateGameRequest("testGame2", testAuth.authToken());
        CreateGameResult createGameResult_2 = gameService.createGame(createGameRequest_2);
        assertNotNull(createGameResult_2.gameID());

        ListGamesRequest listGamesRequest = new ListGamesRequest(testAuth.authToken());
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);

        assertEquals(2, listGamesResult.games().size());
    }

    @Test
    public void listGames_badAuth() {
        ListGamesRequest listGamesRequest = new ListGamesRequest("badAuthToken");
        ServerException exception = assertThrows(ServerException.class, () -> {
            gameService.listGames(listGamesRequest);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void joinGame() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame", testAuth.authToken());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        assertNotNull(createGameResult.gameID());

        ListGamesRequest listGamesRequest = new ListGamesRequest(testAuth.authToken());
        gameService.listGames(listGamesRequest);

        GameData game = gameDAO.getGame(createGameResult.gameID());
        assertNull(game.whiteUsername());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), testAuth.authToken());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);

        game = gameDAO.getGame(createGameResult.gameID());
        assertEquals(game.whiteUsername(), testAuth.username());

        assertEquals(new JoinGameResult(), joinGameResult);
    }

    @Test
    public void joinGame_badAuthToken() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame", testAuth.authToken());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        assertNotNull(createGameResult.gameID());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), "badAuthToken");
        ServerException exception = assertThrows(ServerException.class, () -> {
            gameService.joinGame(joinGameRequest);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void joinGame_alreadyTaken() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        authDAO.createAuth(testAuth2.authToken(), testAuth2);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame", testAuth.authToken());
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        assertNotNull(createGameResult.gameID());

        ListGamesRequest listGamesRequest = new ListGamesRequest(testAuth.authToken());
        gameService.listGames(listGamesRequest);

        GameData game = gameDAO.getGame(createGameResult.gameID());
        assertNull(game.whiteUsername());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), testAuth.authToken());
        gameService.joinGame(joinGameRequest);

        game = gameDAO.getGame(createGameResult.gameID());
        assertEquals(game.whiteUsername(), testAuth.username());

        JoinGameRequest joinGameRequest2 = new JoinGameRequest("WHITE", createGameResult.gameID(), testAuth2.authToken());
        ServerException exception = assertThrows(ServerException.class, () -> {
            gameService.joinGame(joinGameRequest2);
        });

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    public void joinGame_badGameID() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 999, testAuth.authToken());
        ServerException exception = assertThrows(ServerException.class, () -> {
            gameService.joinGame(joinGameRequest);
        });

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }
}
