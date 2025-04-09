package dataaccess.dao.sql;

import dataaccess.DatabaseManager;
import dataaccess.dao.GameDAO;
import model.GameData;
import server.ServerException;

import java.sql.SQLException;
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
        var statement = "INSERT INTO gameData(gameID, whiteUsername, " +
                "blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try {
            DatabaseManager.executeUpdate(statement, gameData.gameID(),
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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT MAX(gameID) FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) + 1;
                    } else {
                        return 1;
                    }
                }
            }
        } catch (ServerException | SQLException e) {
            throw new RuntimeException("Unable to generateGameID", e);
        }
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
