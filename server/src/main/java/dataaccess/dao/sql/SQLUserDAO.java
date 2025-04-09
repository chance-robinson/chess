package dataaccess.dao.sql;

import dataaccess.DatabaseManager;
import dataaccess.dao.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import server.ServerException;

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

    }

    /**
     * Creates a new user data
     *
     * @param userData the user data to be created
     */
    @Override
    public void createUser(UserData userData) {

    }

    /**
     * Returns the user data for a given username
     *
     * @param username the username to retrieve
     * @return the user data for the given username
     */
    @Override
    public UserData getUser(String username) {
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
        return null;
    }
}
