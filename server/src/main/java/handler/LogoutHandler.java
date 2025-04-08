package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.result.EmptyResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService userService)  {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            String authToken = req.headers("Authorization");

            EmptyResult logoutResult = userService.logout(authToken);

            res.status(200);
            return new Gson().toJson(logoutResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
