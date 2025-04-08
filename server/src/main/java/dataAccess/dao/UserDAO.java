package dataAccess.dao;

import model.UserData;

public interface UserDAO {
    void clear();
    void createUser(UserData userData);
    UserData getUser(String username);
}
