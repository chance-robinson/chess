package dataaccess.dao;

import model.AuthData;

/**
 * Interface for the MemoryAuthDAO and SQLAuthDAO to implement
 */
public interface AuthDAO {
    /**
     * Clears all auth data
     */
    void clear();

    /**
     * Creates a new authData
     *
     * @param authToken the authToken to be assigned with authData
     * @param authData the authData to be created with the authToken
     */
    void createAuth(String authToken, AuthData authData);

    /**
     * Returns an authData given an authToken
     *
     * @param authToken the authToken to retrieve
     * @return AuthData for the given authToken
     */
    AuthData getAuth(String authToken);

    /**
     * Deletes an authData given an authToken
     *
     * @param authToken the authToken to delete
     */
    void deleteAuth(String authToken);
}
