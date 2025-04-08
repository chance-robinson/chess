package dataAccess.dao.memory;

import dataAccess.dao.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public UserData getUserByEmail(String email) {
        for (UserData user : users.values()) {
            if (user.email().equals(email)) {
                return user;
            }
        }
        return null;
    }
}
