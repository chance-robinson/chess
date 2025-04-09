package service;

import chess.ChessGame;
import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryGameDAO;
import dataaccess.dao.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class GeneralServiceTest {
    private UserDAO userDAO;
    private UserData testUser;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private AuthData testAuth;
    private GeneralService generalService;

    protected abstract GameDAO createGameDAO();
    protected abstract AuthDAO createAuthDAO();
    protected abstract UserDAO createUserDAO();

    @BeforeEach
    public void initialize() {
        this.gameDAO = createGameDAO();
        this.authDAO = createAuthDAO();
        this.userDAO = createUserDAO();
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        this.generalService = new GeneralService(userDAO, gameDAO, authDAO);
        this.testAuth = new AuthData("testAuth", "testUser");
        this.testUser = new UserData("testUser", "testPass", "test@example.com");
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
