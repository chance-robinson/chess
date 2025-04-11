package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.LoginRequest;
import server.net.result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the Login request/result on the userService.login method
 */
public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserService userService)  {
        this.userService = userService;
    }

    /**
     * Handles the login for a user from the Spark server
     *
     * @param req JSON formatted HTTP request with LoginRequest data
     * @param res JSON formatted HTTP response object
     * @return JSON formatted registerResult object
     * @throws ServerException on errors
     */
    @Override
    public Object handle(Request req, Response res) {
        try {
            LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);

            res.status(200);
            return new Gson().toJson(loginResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return e.toHandlerGson();
        }
    }
}
