package service;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public abstract class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;
    private UserData testUser;

    protected abstract AuthDAO createAuthDAO();
    protected abstract UserDAO createUserDAO();

    @BeforeEach
    public void initialize() {
        this.userDAO = createUserDAO();
        this.authDAO = createAuthDAO();
        this.userService = new UserService(userDAO, authDAO);
        this.testUser = new UserData("testUser", "testPass", "test@example.com");
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
    public void registerUsernameTaken() {
        userDAO.createUser(testUser);

        RegisterRequest request = new RegisterRequest(testUser.username(), testUser.password(), "differentemail@email.com");

        ServerException exception = assertThrows(ServerException.class, () -> userService.register(request));

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    public void registerEmailTaken() {
        userDAO.createUser(testUser);

        RegisterRequest request = new RegisterRequest("differentTestUser", testUser.password(), testUser.email());

        ServerException exception = assertThrows(ServerException.class, () -> userService.register(request));

        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    public void login() {
        RegisterRequest regRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult regResult = userService.register(regRequest);
        assertNotNull(regResult);

        LoginRequest request = new LoginRequest(testUser.username(), testUser.password());
        LoginResult result = userService.login(request);
        assertNotNull(result);

        assertEquals(testUser.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginBadUsername() {
        RegisterRequest regRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult regResult = userService.register(regRequest);
        assertNotNull(regResult);

        LoginRequest request = new LoginRequest("badUser", testUser.password());

        ServerException exception = assertThrows(ServerException.class, () -> userService.login(request));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void loginBadPassword() {
        RegisterRequest regRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult regResult = userService.register(regRequest);
        assertNotNull(regResult);

        LoginRequest request = new LoginRequest(testUser.username(), "badPassword");

        ServerException exception = assertThrows(ServerException.class, () -> userService.login(request));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    public void logout() {
        RegisterRequest regRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult regResult = userService.register(regRequest);
        assertNotNull(regResult);

        userService.logout(regResult.authToken());

        AuthData loggedOutUserAuth = authDAO.getAuth(regResult.authToken());
        assertNull(loggedOutUserAuth);
    }

    @Test
    public void logoutBadAuthToken() {
        RegisterRequest regRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult regResult = userService.register(regRequest);
        assertNotNull(regResult);

        ServerException exception = assertThrows(ServerException.class, () -> userService.logout("badAuthToken"));

        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.getStatusCode());
    }
}
