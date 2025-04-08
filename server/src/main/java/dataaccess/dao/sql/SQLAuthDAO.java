package dataaccess.dao.sql;

import dataaccess.dao.AuthDAO;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
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
