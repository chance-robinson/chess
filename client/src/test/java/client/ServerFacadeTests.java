package client;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import server.net.request.CreateGameRequest;
import server.net.request.LoginRequest;
import server.net.request.RegisterRequest;
import server.net.result.CreateGameResult;
import server.net.result.LoginResult;
import server.net.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static UserData testUser;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url);
        testUser = new UserData("testUsername", "testPassword", "testEmail");
    }

    @BeforeEach
    public void initialize() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void register() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        RegisterResult registerResult = serverFacade.register(registerRequest);
        assertNotNull(registerResult);
        assertEquals(registerRequest.email(), testUser.email());
    }

    @Test
    public void registerDuplicate() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        serverFacade.register(registerRequest);

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.register(registerRequest));
        assertNotNull(exception);
    }

    @Test
    public void login() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        serverFacade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(testUser.username(), testUser.password());
        LoginResult loginResult = serverFacade.login(loginRequest);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        assertEquals(loginResult.username(), testUser.username());
    }

    @Test
    public void loginInvalidUser() {
        LoginRequest loginRequest = new LoginRequest(testUser.username(), testUser.password());

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.login(loginRequest));
        assertNotNull(exception);
    }

    @Test
    public void createGame() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        serverFacade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(testUser.username(), testUser.password());
        LoginResult loginResult = serverFacade.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
        CreateGameResult createGameResult = serverFacade.createGame(createGameRequest, loginResult.authToken());
        assertNotNull(createGameResult);
        assertEquals(createGameResult.gameID(), 1);
    }

    @Test
    public void createGameBadAuthToken() {
        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");

        ResponseException exception = assertThrows(ResponseException.class, () -> serverFacade.createGame(createGameRequest, "DNE"));
        assertNotNull(exception);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }
}
