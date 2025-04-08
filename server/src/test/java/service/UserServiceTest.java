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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void clearPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, gameDAO, authDAO);

        // Populate userDAO
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDAO.createUser(user);

        // Populate gameDAO
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        // Populate authDAO
        AuthData auth = new AuthData("testAuth", "testUser");
        authDAO.createAuth("testAuth", auth);

        // Asserts
        assertNotNull(userDAO.getUser("testUser"));
        assertNotNull(gameDAO.getGame(generatedGameID));
        assertNotNull(authDAO.getAuth("testAuth"));

        // Clear
        userService.clear();

        // Checks
        assertNull(userDAO.getUser("testUser"));
        assertNull(gameDAO.getGame(generatedGameID));
    }
}
