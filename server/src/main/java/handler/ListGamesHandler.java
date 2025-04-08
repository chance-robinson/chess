package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService)  {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            String authToken = req.headers("Authorization");

            ListGamesResult listGamesResult = gameService.listGames(authToken);

            res.status(200);
            return new Gson().toJson(listGamesResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
