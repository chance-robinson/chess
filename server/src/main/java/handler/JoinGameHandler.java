package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.JoinGameRequest;
import server.net.result.JoinGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService)  {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            String authToken = req.headers("Authorization");
            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, authToken);

            res.status(200);
            return new Gson().toJson(joinGameResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new HandlerError(e.getMessage()));
        }
    }
}
