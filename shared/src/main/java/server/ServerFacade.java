package server;

import server.net.request.RegisterRequest;
import server.net.result.RegisterResult;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) {
        return null;
    }

//    // Deletes
//    ClearHandler clearHandler = new ClearHandler(generalService);
//    delete("/db", clearHandler);
//
//    LogoutHandler logoutHandler = new LogoutHandler(userService);
//    delete("/session", logoutHandler);
//
//    // Posts
//    RegisterHandler registerHandler = new RegisterHandler(userService);
//    post("/user", registerHandler);
//
//    LoginHandler loginHandler = new LoginHandler(userService);
//    post("/session", loginHandler);
//
//    CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
//    post("/game", createGameHandler);
//
//    // Puts
//    JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
//    put("/game", joinGameHandler);
//
//    // Gets
//    ListGamesHandler listGameHandler = new ListGamesHandler(gameService);
//    get("/game", listGameHandler);
}
