package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.CreateGameRequest;
import server.net.result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the CreateGame request/result on the gameService.createGame method
 */
public class CreateGameHandler implements Route {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService)  {
        this.gameService = gameService;
    }

    /**
     * Handles the creation of a game for a user from the Spark server
     *
     * @param req JSON formatted HTTP request with CreateGameRequest data and authToken in header
     * @param res JSON formatted HTTP response object
     * @return JSON formatted ListGamesResult object
     * @throws ServerException on errors
     */
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
