package dataaccess.dao.sql;

import dataaccess.DatabaseManager;
import dataaccess.dao.GameDAO;
import model.GameData;
import server.ServerException;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws ServerException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gameData (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` longtext DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        DatabaseManager.configureDatabase(createStatements);
    }

    /**
     * Clears all game data
     */
    @Override
    public void clear() {
        try {
            DatabaseManager.executeUpdate("TRUNCATE gameData");
        } catch (ServerException e) {
            throw new RuntimeException("Unable to clear", e);
        }
    }

    /**
     * Creates a new game entry
     *
     * @param gameData the game data to be created
     */
    @Override
    public void createGame(GameData gameData) {
        var statement = "INSERT INTO gameData(gameName, whiteUsername, " +
                "blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try {
            DatabaseManager.executeUpdate(statement, gameData.gameName(),
                    gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        } catch (ServerException e) {
            throw new RuntimeException("Unable to createGame", e);
        }
    }

    /**
     * Returns the game data for a given game ID
     *
     * @param gameID the game ID to retrieve
     * @return the game data for the given game ID
     */
    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    /**
     * Generates a unique game ID
     *
     * @return a new unique game ID
     */
    @Override
    public int generateGameID() {
        return 0;
    }

    /**
     * Returns a list of all games
     *
     * @return a list of all game data
     */
    @Override
    public ArrayList<GameData> getAllGames() {
        return null;
    }

    /**
     * Returns game data for a given game
     *
     * @param gameName the game name to retrieve game data for
     * @return game data for the given game name
     */
    @Override
    public GameData getGameByGameName(String gameName) {
        return null;
    }

    /**
     * Updates a game with new game data
     *
     * @param gameData the new game data to be updated
     */
    @Override
    public void update(GameData gameData) {

    }
}
