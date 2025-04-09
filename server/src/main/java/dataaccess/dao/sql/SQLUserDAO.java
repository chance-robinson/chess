package dataaccess.dao.sql;

import dataaccess.DatabaseManager;
import dataaccess.dao.UserDAO;
import model.UserData;
import server.ServerException;

import java.sql.SQLException;


public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws ServerException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userData (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              json TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        DatabaseManager.configureDatabase(createStatements);
    }

    /**
     * Clears all user data
     */
    @Override
    public void clear() {
        var statement = "TRUNCATE userData";
        try {
            DatabaseManager.executeUpdate(statement);
        } catch (ServerException e) {
            throw new RuntimeException("Unable to clear", e);
        }
    }

    /**
     * Creates a new user data
     *
     * @param userData the user data to be created
     */
    @Override
    public void createUser(UserData userData) {
        var statement = "INSERT INTO userData(username, password, email) VALUES (?, ?, ?)";
        try {
            DatabaseManager.executeUpdate(statement, userData.username(), userData.password(), userData.password());
        } catch (ServerException e) {
            throw new RuntimeException("Unable to createUser", e);
        }
    }

    /**
     * Returns the user data for a given username
     *
     * @param username the username to retrieve
     * @return the user data for the given username
     */
    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM userData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                    }
                }
            }
        } catch (ServerException | SQLException e) {
            throw new RuntimeException("Unable to get user by username", e);
        }
        return null;
    }

    /**
     * Returns the user data for a given email address
     *
     * @param email the email address to retrieve the user with
     * @return the user data for the given email address
     */
    @Override
    public UserData getUserByEmail(String email) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM userData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, email);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                    }
                }
            }
        } catch (ServerException | SQLException e) {
            throw new RuntimeException("Unable to get user by email", e);
        }
        return null;
    }
}
