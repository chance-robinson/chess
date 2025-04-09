package service;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.UserDAO;
import model.AuthData;
import model.UserData;
import server.ServerException;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import java.util.UUID;

/**
 * The service pertaining to all methods related to the UserDAO
 */
public class UserService {
    final UserDAO userDAO;
    final AuthDAO authDAO;


    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    /**
     * Registers a user to the DAO based on username, password, email
     *
     * @param req of type RegisterResult
     * @return RegisterResult the username and authToken
     * @throws ServerException Codes: 400, 403
     */
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

    /**
     * Logs in a user given a username and password
     *
     * @param req of type LoginRequest
     * @return LoginResult the username and authToken
     * @throws ServerException Codes: 400, 401
     */
    public LoginResult login(LoginRequest req) throws ServerException {
        String username = req.username();
        String password = req.password();

        if (username == null || password == null) {
            throw new ServerException("Error: bad request", 400);
        }

        UserData user = userDAO.getUser(username);
        if (user == null || !userDAO.isPasswordEqual(password, user.password())) {
            throw new ServerException("Error: unauthorized", 401);
        }

        String generatedAuthToken = generateAuthToken();

        authDAO.createAuth(generatedAuthToken, new AuthData(generatedAuthToken, username));

        return new LoginResult(username, generatedAuthToken);
    }

    /**
     * Logs out a user based on authToken
     *
     * @param authToken an authToken that will get authenticated
     * @throws ServerException Codes: 401
     */
    public void logout(String authToken) throws ServerException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new ServerException("Error: unauthorized", 401);
        }

        authDAO.deleteAuth(authToken);
    }

    /**
     * Returns a randomly generated authToken
     *
     * @return String of type UUID
     */
    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
