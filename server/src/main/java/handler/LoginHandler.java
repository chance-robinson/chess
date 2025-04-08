package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.LoginRequest;
import server.net.result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserService userService)  {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);

            res.status(200);
            return new Gson().toJson(loginResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
