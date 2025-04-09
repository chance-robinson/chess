package dataaccess.dao.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.dao.GameDAO;
import model.GameData;
import server.ServerException;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {
    /**
     * This constructor ensures that the gameData table is built on the
     * chess database
     *
     * @throws ServerException if the database fails to configure
     */
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
     * Clears all game data by truncating the table
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
     * Creates a new game entry in gameData table
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
     * Returns the game data for a given game ID from the gameData table
     *
     * @param gameID the game ID to retrieve
     * @return the game data for the given game ID
     */
    @Override
    public GameData getGame(int gameID) {
        String statement = "SELECT * FROM gameData WHERE gameID=?";
        return getGameDataQuery(statement, gameID);
    }

    /**
     * Generates a unique game ID based on the max gameID
     * from the gameData table
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
     * Returns a list of all games from the gameData table
     *
     * @return a list of all game data
     */
    @Override
    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                var rs = ps.executeQuery();
                while (rs.next()) {
                    games.add(new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            new Gson().fromJson(rs.getString("game"), ChessGame.class)
                    ));
                }
            }
            return games;
        } catch (ServerException | SQLException e) {
            throw new RuntimeException("Unable to getAllGames", e);
        }
    }

    /**
     * Returns game data for a given game from the gameData table
     *
     * @param gameName the game name to retrieve game data for
     * @return game data for the given game name
     */
    @Override
    public GameData getGameByGameName(String gameName) {
        String statement = "SELECT * FROM gameData WHERE gameName=?";
        return getGameDataQuery(statement, gameName);
    }

    /**
     * Updates a game with new game data from the gameData table
     *
     * @param gameData the new game data to be updated
     */
    @Override
    public void update(GameData gameData) {
        var statement = "UPDATE gameData SET whiteUsername=?, " +
                "blackUsername=?, game=? WHERE gameID=?";
        try {
            GameData existingGame = getGame(gameData.gameID());
            if (existingGame != null) {
                DatabaseManager.executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.game(), gameData.gameID());
            } else {
                throw new ServerException("Error: bad request", 500);
            }
        } catch (ServerException e) {
            throw new RuntimeException("Unable to update", e);
        }
    }

    /**
     * Returns game data based on a template query and a param
     * from the gameData table
     *
     * @param query a SQL query following the format "SELECT * FROM gameData WHERE ...=?"
     * @param param a param specifying what we are equaling in the WHERE, i.e. gameName/gameID
     * @return the gameData for the given query and param
     */
    private GameData getGameDataQuery(String query, Object param) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(query)) {
                if (param instanceof String) {
                    ps.setString(1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(1, (Integer) param);
                }
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("game"), ChessGame.class)
                        );
                    }
                }
            }
        } catch (ServerException | SQLException e) {
            throw new RuntimeException("Unable to getGameDataQuery", e);
        }
        return null;
    }
}
