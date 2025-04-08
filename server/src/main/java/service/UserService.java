package service;

import dataAccess.dao.AuthDAO;
import dataAccess.dao.UserDAO;
import model.AuthData;
import model.UserData;
import server.ServerException;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.EmptyResult;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    final UserDAO userDAO;
    final AuthDAO authDAO;


    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws ServerException {
        String email = req.email();
        String username = req.username();
        String password = req.password();

        if (email == null || username == null || password == null) {
            throw new ServerException("Error: bad request", 400);
        }

        if (userDAO.getUser(username) != null || userDAO.getUserByEmail(email) != null) {
            throw new ServerException("Error: already taken", 403);
        }

        String generatedAuthToken = generateAuthToken();

        userDAO.createUser(new UserData(username, password, email));
        authDAO.createAuth(generatedAuthToken, new AuthData(generatedAuthToken, username));

        return new RegisterResult(username, generatedAuthToken);
    }

    public LoginResult login(LoginRequest req) throws ServerException {
        String username = req.username();
        String password = req.password();

        if (username == null || password == null) {
            throw new ServerException("Error: bad request", 400);
        }

        UserData user = userDAO.getUser(username);
        if (user == null || !Objects.equals(user.password(), password)) {
            throw new ServerException("Error: unauthorized", 401);
        }

        String generatedAuthToken = generateAuthToken();

        authDAO.createAuth(generatedAuthToken, new AuthData(generatedAuthToken, username));

        return new LoginResult(username, generatedAuthToken);
    }

    public EmptyResult logout(String authToken) throws ServerException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new ServerException("Error: unauthorized", 401);
        }

        authDAO.deleteAuth(authToken);

        return new EmptyResult();
    }

    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
