package dataaccess.dao;

import model.AuthData;

public interface AuthDAO {
    void clear();
    void createAuth(String authToken, AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
}
