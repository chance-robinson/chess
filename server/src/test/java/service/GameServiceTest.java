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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GameServiceTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private UserData testUser;
    private AuthData testAuth;

    @BeforeEach
    public void initialize() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, gameDAO, authDAO);
        testUser = new UserData("testUser", "testPass", "test@example.com");
        testAuth = new AuthData("testAuth", "testUser");
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
    public void listGames() {

    }
}
