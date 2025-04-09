package dataaccess.dao;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class GameDAOTest {
    private GameDAO gameDAO;

    protected abstract GameDAO createGameDAO();

    @BeforeEach
    public void initialize() {
        this.gameDAO = createGameDAO();
        gameDAO.clear();
    }

    @Test
    public void clear() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));

        gameDAO.clear();

        assertNull(gameDAO.getGame(generatedGameID));
    }

    @Test
    public void createGame() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));
    }

    @Test
    public void createGameDuplicatedGame() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));

        assertThrows(RuntimeException.class, () -> gameDAO.createGame(game));
    }

    @Test
    public void getGame() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));

        GameData retrievedGame = gameDAO.getGame(generatedGameID);

        assertEquals(game, retrievedGame);
    }

    @Test
    public void getGameBadGameID() {
        GameData game = gameDAO.getGame(1234);
        assertNull(game);
    }

    @Test
    public void generateGameID() {
        int generatedGameID = gameDAO.generateGameID();
        assertTrue(generatedGameID >= 1);
    }

    @Test
    public void generateGameIDLessThanEqual0() {
        int generatedGameID = gameDAO.generateGameID();
        assertFalse(generatedGameID <= 0);
    }

    @Test
    public void getAllGames() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));

        int generatedGameID2 = gameDAO.generateGameID();
        GameData game2 = new GameData(generatedGameID2, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game2);

        assertNotNull(gameDAO.getGame(generatedGameID2));

        ArrayList<GameData> games = gameDAO.getAllGames();
        assertEquals(2, games.size());
    }

    @Test
    public void getAllGamesNoGames() {
        ArrayList<GameData> games = gameDAO.getAllGames();
        assertEquals(0, games.size());
    }

    @Test
    public void getGameByGameName() {
        String gameName = "RandomGameName";
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, gameName, new ChessGame());
        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(generatedGameID));

        GameData retrievedGame = gameDAO.getGameByGameName(gameName);

        assertEquals(retrievedGame.gameName(), gameName);
    }

    @Test
    public void getGameByGameNameNone() {
        GameData retrievedGame = gameDAO.getGameByGameName("TestGameName");
        assertNull(retrievedGame);
    }

    @Test
    public void update() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        GameData updatedGame = new GameData(game.gameID(), "testUser",
                null, game.gameName(), game.game());

        gameDAO.update(updatedGame);

        GameData retrievedGame = gameDAO.getGame(updatedGame.gameID());
        assertEquals(retrievedGame.whiteUsername(), "testUser");
        assertNull(retrievedGame.blackUsername());
        assertEquals(retrievedGame.gameID(), game.gameID());
    }

    @Test
    public void updateGameNotCreated() {
        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        int generatedGameID2 = gameDAO.generateGameID();
        GameData updatedGame = new GameData(generatedGameID2, "testUser",
                null, game.gameName(), game.game());

        assertThrows(RuntimeException.class, () -> gameDAO.update(updatedGame));
    }

}
