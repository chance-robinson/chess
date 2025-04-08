package service;

import chess.ChessGame;
import dataAccess.dao.GameDAO;
import dataAccess.dao.UserDAO;
import dataAccess.dao.memory.MemoryGameDAO;
import dataAccess.dao.memory.MemoryUserDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void clearPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, gameDAO);

        // Populate userDAO
        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userDAO.createUser(user);

        assertNotNull(userDAO.getUser("testUser"));

        // Populate gameDAO
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));

        // Clear
        userService.clear();

        // Checks
        assertNull(userDAO.getUser("testUser"));
        assertNull(gameDAO.getGame(generatedGameID));
    }
}
