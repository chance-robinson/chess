package dataaccess.dao;

import model.UserData;

/**
 * Interface for the MemoryUserDAO and SQLUserDAO to implement
 */
public interface UserDAO {
    /**
     * Clears all user data
     */
    void clear();

    /**
     * Creates a new user data
     *
     * @param userData the user data to be created
     */
    void createUser(UserData userData);

    /**
     * Returns the user data for a given username
     *
     * @param username the username to retrieve
     * @return the user data for the given username
     */
    UserData getUser(String username);

    /**
     * Returns the user data for a given email address
     *
     * @param email the email address to retrieve the user with
     * @return the user data for the given email address
     */
    UserData getUserByEmail(String email);

    boolean isPasswordEqual(String password, String storedPassword);
}
