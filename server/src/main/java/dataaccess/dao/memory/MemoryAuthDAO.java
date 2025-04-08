package dataaccess.dao.memory;

import dataaccess.dao.AuthDAO;
import model.AuthData;

import java.util.HashMap;

/**
 * In-memory implementation of the AuthDAO interface.
 */
public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auths = new HashMap<>();

    /**
     * Clears all auth data from memory HashMap auths
     */
    @Override
    public void clear() {
        auths.clear();
    }

    /**
     * Creates a new authData from memory
     *
     * @param authToken the authToken to be assigned with authData
     * @param authData the authData to be created with the authToken
     */
    @Override
    public void createAuth(String authToken, AuthData authData) {
        auths.put(authToken, authData);
    }

    /**
     * Returns an authData given an authToken
     *
     * @param authToken the authToken to retrieve from memory
     * @return AuthData for the given authToken
     */
    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    /**
     * Deletes an authData given an authToken
     *
     * @param authToken the authToken to delete from memory
     */
    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }
}
