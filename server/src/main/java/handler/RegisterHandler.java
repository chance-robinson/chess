package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.RegisterRequest;
import server.net.result.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the Register request/result on the userService.register method
 */
public class RegisterHandler implements Route {
    private final UserService userService;

    public RegisterHandler(UserService userService)  {
        this.userService = userService;
    }

    /**
     * Handles the user registration from the Spark server
     *
     * @param req JSON formatted HTTP request with RegisterRequest data
     * @param res JSON formatted HTTP response object
     * @return JSON formatted registerResult object
     * @throws ServerException on errors
     */
    @Override
    public Object handle(Request req, Response res) {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);

            res.status(200);
            return new Gson().toJson(registerResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return e.toHandlerGson();
        }
    }
}
