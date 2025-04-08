package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.RegisterRequest;
import server.net.result.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final UserService userService;

    public RegisterHandler(UserService userService)  {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);

            res.status(200);
            return new Gson().toJson(registerResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
