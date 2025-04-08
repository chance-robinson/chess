package dataaccess.dao.sql;

import dataaccess.dao.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {
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
