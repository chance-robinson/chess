package dataaccess.dao.memory;

import dataaccess.dao.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

/**
 * In-memory implementation of the UserDAO interface.
 */
public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    /**
     * Clears all user data from memory HashMap users
     */
    @Override
    public void clear() {
        users.clear();
    }

    /**
     * Creates a new user data
     *
     * @param userData the user data to be created from memory
     */
    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    /**
     * Returns the user data for a given username
     *
     * @param username the username to retrieve from memory
     * @return the user data for the given username
     */
    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    /**
     * Returns the user data for a given email address
     *
     * @param email the email address to retrieve the user with from memory
     * @return the user data for the given email address
     */
    @Override
    public UserData getUserByEmail(String email) {
        for (UserData user : users.values()) {
            if (user.email().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Helper to return whether two passwords are equal
     *
     * @param password the password that we originally had and want to compare
     * @param storedPassword the password we are comparing against ex. hashed
     * @return true if they are equal, false if they aren't
     */
    @Override
    public boolean isPasswordEqual(String password, String storedPassword) {
        return Objects.equals(password, storedPassword);
    }
}
