package dataaccess.dao.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.dao.AuthDAO;
import model.AuthData;
import server.ServerException;

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

    }

    /**
     * Creates a new authData
     *
     * @param authToken the authToken to be assigned with authData
     * @param authData  the authData to be created with the authToken
     */
    @Override
    public void createAuth(String authToken, AuthData authData) {

    }

    /**
     * Returns an authData given an authToken
     *
     * @param authToken the authToken to retrieve
     * @return AuthData for the given authToken
     */
    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    /**
     * Deletes an authData given an authToken
     *
     * @param authToken the authToken to delete
     */
    @Override
    public void deleteAuth(String authToken) {

    }
}
