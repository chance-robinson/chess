package server;

import dataAccess.dao.AuthDAO;
import dataAccess.dao.GameDAO;
import dataAccess.dao.UserDAO;
import dataAccess.dao.memory.MemoryAuthDAO;
import dataAccess.dao.memory.MemoryGameDAO;
import dataAccess.dao.memory.MemoryUserDAO;
import handler.*;
import service.GameService;
import service.GeneralService;
import service.UserService;
import spark.*;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private static void createRoutes() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        GeneralService generalService = new GeneralService(userDAO, gameDAO, authDAO);

        // Deletes
        ClearHandler clearHandler = new ClearHandler(generalService);
        delete("/db", clearHandler);

        LogoutHandler logoutHandler = new LogoutHandler(userService);
        delete("/session", logoutHandler);

        // Posts
        RegisterHandler registerHandler = new RegisterHandler(userService);
        post("/user", registerHandler);

        LoginHandler loginHandler = new LoginHandler(userService);
        post("/session", loginHandler);

        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        post("/game", createGameHandler);

        // Puts
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        put("/game", joinGameHandler);

        // Gets
        ListGamesHandler listGameHandler = new ListGamesHandler(gameService);
        get("/game", listGameHandler);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
