package handler;

import com.google.gson.Gson;
import server.ServerException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the Logout request/result on the userService.logout method
 */
public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService userService)  {
        this.userService = userService;
    }

    /**
     * Handles the logout for a user from the Spark server
     *
     * @param req JSON formatted HTTP request with authToken in header
     * @param res JSON formatted HTTP response object
     * @return empty response indicating success
     * @throws ServerException on errors
     */
    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            String authToken = req.headers("Authorization");

            userService.logout(authToken);

            res.status(200);
            return "";
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
