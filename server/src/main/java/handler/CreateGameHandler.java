package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.CreateGameRequest;
import server.net.result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService)  {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            String authToken = req.headers("Authorization");
            CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
            CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);

            res.status(200);
            return new Gson().toJson(createGameResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
