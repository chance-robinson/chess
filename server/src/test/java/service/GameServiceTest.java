package service;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;
import server.net.request.CreateGameRequest;
import server.net.request.JoinGameRequest;
import server.net.result.CreateGameResult;
import server.net.result.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;
    private AuthData testAuth;
    private AuthData testAuth2;

    @BeforeEach
    public void initialize() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        testAuth = new AuthData("testAuth", "testUser");
        testAuth2 = new AuthData("testAuth2", "testUser2");
    }

    @Test
    public void createGame() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameResult result = gameService.createGame(request, testAuth.authToken());

        assertNotNull(result.gameID());
    }

    @Test
    public void createGameDuplicateGameName() {
        String gameName = "testGame";

        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest1 = new CreateGameRequest(gameName);
        CreateGameResult createGameResult1 = gameService.createGame(createGameRequest1, testAuth.authToken());
        assertNotNull(createGameResult1.gameID());

        CreateGameRequest createGameRequest2 = new CreateGameRequest(gameName);

        ServerException exception = assertThrows(ServerException.class, () -> gameService.createGame(createGameRequest2, testAuth.authToken()));

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    public void createGameBadAuthToken() {
        String gameName = "testGame";

        CreateGameRequest createGameRequest1 = new CreateGameRequest(gameName);
        ServerException exception = assertThrows(ServerException.class, () -> gameService.createGame(createGameRequest1, "badAuthToken"));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void listGames() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest1 = new CreateGameRequest("testGame1");
        CreateGameResult createGameResult1 = gameService.createGame(createGameRequest1, testAuth.authToken());
        assertNotNull(createGameResult1.gameID());

        CreateGameRequest createGameRequest2 = new CreateGameRequest("testGame2");
        CreateGameResult createGameResult2 = gameService.createGame(createGameRequest2, testAuth.authToken());
        assertNotNull(createGameResult2.gameID());

        ListGamesResult listGamesResult = gameService.listGames(testAuth.authToken());

        assertEquals(2, listGamesResult.games().size());
    }

    @Test
    public void listGamesBadAuthToken() {
        ServerException exception = assertThrows(ServerException.class, () -> gameService.listGames("badAuthToken"));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void joinGame() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, testAuth.authToken());
        assertNotNull(createGameResult.gameID());

        gameService.listGames(testAuth.authToken());

        GameData game = gameDAO.getGame(createGameResult.gameID());
        assertNull(game.whiteUsername());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        gameService.joinGame(joinGameRequest, testAuth.authToken());

        game = gameDAO.getGame(createGameResult.gameID());
        assertEquals(game.whiteUsername(), testAuth.username());
    }

    @Test
    public void joinGameBadAuthToken() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, testAuth.authToken());
        assertNotNull(createGameResult.gameID());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        ServerException exception = assertThrows(ServerException.class, () -> gameService.joinGame(joinGameRequest, "badAuthToken"));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void joinGameAlreadyTaken() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        authDAO.createAuth(testAuth2.authToken(), testAuth2);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, testAuth.authToken());
        assertNotNull(createGameResult.gameID());

        gameService.listGames(testAuth.authToken());

        GameData game = gameDAO.getGame(createGameResult.gameID());
        assertNull(game.whiteUsername());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        gameService.joinGame(joinGameRequest, testAuth.authToken());

        game = gameDAO.getGame(createGameResult.gameID());
        assertEquals(game.whiteUsername(), testAuth.username());

        JoinGameRequest joinGameRequest2 = new JoinGameRequest("WHITE", createGameResult.gameID());
        ServerException exception = assertThrows(ServerException.class, () -> gameService.joinGame(joinGameRequest2, testAuth2.authToken()));

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    public void joinGameBadGameID() {
        authDAO.createAuth(testAuth.authToken(), testAuth);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 999);
        ServerException exception = assertThrows(ServerException.class, () -> gameService.joinGame(joinGameRequest, testAuth.authToken()));

        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
    }
}
