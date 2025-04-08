package service;

import chess.ChessGame;
import dataAccess.dao.AuthDAO;
import dataAccess.dao.GameDAO;
import dataAccess.dao.UserDAO;
import dataAccess.dao.memory.MemoryAuthDAO;
import dataAccess.dao.memory.MemoryGameDAO;
import dataAccess.dao.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;
import server.net.request.LoginRequest;
import server.net.request.LogoutRequest;
import server.net.request.RegisterRequest;
import server.net.result.LoginResult;
import server.net.result.LogoutResult;
import server.net.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private UserData testUser;
    private AuthData testAuth;

    @BeforeEach
    public void initialize() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, gameDAO, authDAO);
        testUser = new UserData("testUser", "testPass", "test@example.com");
        testAuth = new AuthData("testAuth", "testUser");
    }

    @Test
    public void clear() {
        userDAO.createUser(testUser);

        int generatedGameID = gameDAO.generateGameID();
        GameData game = new GameData(generatedGameID, null, null, "testGameName", new ChessGame());
        gameDAO.createGame(game);

        authDAO.createAuth(testAuth.authToken(), testAuth);

        assertNotNull(userDAO.getUser(testUser.username()));
        assertNotNull(gameDAO.getGame(generatedGameID));
        assertNotNull(authDAO.getAuth(testAuth.authToken()));

        userService.clear();

        assertNull(userDAO.getUser(testUser.username()));
        assertNull(gameDAO.getGame(generatedGameID));
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    public void register() {
        RegisterRequest request = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());

        RegisterResult result = userService.register(request);

        assertNotNull(result);

        UserData createdUser = userDAO.getUser(testUser.username());
        assertNotNull(createdUser);

        assertEquals(testUser.username(), createdUser.username());
        assertEquals(testUser.email(), createdUser.email());
        assertNotNull(result.authToken());
    }

    @Test
    public void register_usernameTaken() {
        userDAO.createUser(testUser);

        RegisterRequest request = new RegisterRequest(testUser.username(), testUser.password(), "differentemail@email.com");

        ServerException exception = assertThrows(ServerException.class, () -> {
            userService.register(request);
        });

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    public void register_emailTaken() {
        userDAO.createUser(testUser);

        RegisterRequest request = new RegisterRequest("differentTestUser", testUser.password(), testUser.email());

        ServerException exception = assertThrows(ServerException.class, () -> {
            userService.register(request);
        });

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    public void login() {
        RegisterRequest reg_request = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult reg_result = userService.register(reg_request);
        assertNotNull(reg_result);

        LoginRequest request = new LoginRequest(testUser.username(), testUser.password());
        LoginResult result = userService.login(request);
        assertNotNull(result);

        assertEquals(testUser.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void login_badUsername() {
        RegisterRequest reg_request = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult reg_result = userService.register(reg_request);
        assertNotNull(reg_result);

        LoginRequest request = new LoginRequest("badUser", testUser.password());

        ServerException exception = assertThrows(ServerException.class, () -> {
            userService.login(request);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void login_badPassword() {
        RegisterRequest reg_request = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult reg_result = userService.register(reg_request);
        assertNotNull(reg_result);

        LoginRequest request = new LoginRequest(testUser.username(), "badPassword");

        ServerException exception = assertThrows(ServerException.class, () -> {
            userService.login(request);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void logout() {
        RegisterRequest reg_request = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult reg_result = userService.register(reg_request);
        assertNotNull(reg_result);

        LoginRequest login_request = new LoginRequest(testUser.username(), testUser.password());
        LoginResult login_result = userService.login(login_request);
        assertNotNull(login_result);

        String authToken = login_result.authToken();
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResult result = userService.logout(request);

        assertEquals(new LogoutResult(), result);
    }

    @Test
    public void logout_badAuthToken() {
        RegisterRequest reg_request = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult reg_result = userService.register(reg_request);
        assertNotNull(reg_result);

        LoginRequest login_request = new LoginRequest(testUser.username(), testUser.password());
        LoginResult login_result = userService.login(login_request);
        assertNotNull(login_result);

        LogoutRequest request = new LogoutRequest("badAuthToken");

        ServerException exception = assertThrows(ServerException.class, () -> {
            userService.logout(request);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }
}
