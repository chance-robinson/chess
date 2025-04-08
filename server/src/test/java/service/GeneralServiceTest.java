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

public class GeneralServiceTest {
    private UserDAO userDAO;
    private UserData testUser;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private AuthData testAuth;
    private GeneralService generalService;

    @BeforeEach
    public void initialize() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        generalService = new GeneralService(userDAO, gameDAO, authDAO);
        testAuth = new AuthData("testAuth", "testUser");
        testUser = new UserData("testUser", "testPass", "test@example.com");
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

        generalService.clear();

        assertNull(userDAO.getUser(testUser.username()));
        assertNull(gameDAO.getGame(generatedGameID));
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }
}
