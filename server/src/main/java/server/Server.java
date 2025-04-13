package server;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryGameDAO;
import dataaccess.dao.memory.MemoryUserDAO;
import dataaccess.dao.sql.SQLAuthDAO;
import dataaccess.dao.sql.SQLGameDAO;
import dataaccess.dao.sql.SQLUserDAO;
import handler.*;
import service.GameService;
import service.GeneralService;
import service.UserService;
import spark.*;

import static spark.Spark.*;

/**
 * The Server which we run our routes off of using Spark to initialize and create
 * based on a specific port and location of staticFiles.
 */
public class Server {

    /**
     * Runs the Spark server on a given port
     *
     * @param desiredPort the port to run on
     * @return int the spark port
     */
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", Server.class);

        // Register your endpoints and handle exceptions here.
        createRoutes();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    /**
     * Creates the routes on Spark using the dedicated handlers for a specified path
     */
    private static void createRoutes() {
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;

        boolean useSQL = true;
        if (useSQL) {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } else {
            userDAO = new MemoryUserDAO();
            authDAO = new MemoryAuthDAO();
            gameDAO = new MemoryGameDAO();
        }
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

    /**
     * Stops the Spark server
     */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
