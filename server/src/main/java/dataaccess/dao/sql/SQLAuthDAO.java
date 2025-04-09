package dataaccess.dao.sql;

import dataaccess.DatabaseManager;
import dataaccess.dao.AuthDAO;
import model.AuthData;
import server.ServerException;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws ServerException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `authToken` varchar(256),
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              json TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        DatabaseManager.configureDatabase(createStatements);
    }

    /**
     * Clears all auth data
     */
    @Override
    public void clear() {
        try {
            DatabaseManager.executeUpdate("TRUNCATE authData");
        } catch (ServerException e) {
            throw new RuntimeException("Unable to clear", e);
        }
    }

    /**
     * Creates a new authData
     *
     * @param authToken the authToken to be assigned with authData
     * @param authData  the authData to be created with the authToken
     */
    @Override
    public void createAuth(String authToken, AuthData authData) {
        var statement = "INSERT INTO authData(authToken, username) VALUES (?, ?)";
        try {
            DatabaseManager.executeUpdate(statement, authToken, authData.username());
        } catch (ServerException e) {
            throw new RuntimeException("Unable to createAuth", e);
        }
    }

    /**
     * Returns an authData given an authToken
     *
     * @param authToken the authToken to retrieve
     * @return AuthData for the given authToken
     */
    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),
                                rs.getString("username"));
                    }
                }
            }
        } catch (ServerException | SQLException e) {
            throw new RuntimeException("Unable to get auth by authToken", e);
        }
        return null;
    }

    /**
     * Deletes an authData given an authToken
     *
     * @param authToken the authToken to delete
     */
    @Override
    public void deleteAuth(String authToken) {
        try {
            DatabaseManager.executeUpdate("DELETE FROM authData WHERE authToken=?", authToken);
        } catch (ServerException e) {
            throw new RuntimeException("Unable to clear", e);
        }
    }
}
