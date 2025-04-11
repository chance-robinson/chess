package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.request.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the JoinGame request/result on the gameService.joinGame method
 */
public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService)  {
        this.gameService = gameService;
    }

    /**
     * Handles the joining of a game for a user from the Spark server
     *
     * @param req JSON formatted HTTP request with joinGameRequest data and authToken in header
     * @param res JSON formatted HTTP response object
     * @return empty response indicating success
     * @throws ServerException on errors
     */
    @Override
    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            gameService.joinGame(joinGameRequest, authToken);

            res.status(200);
            return "";
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return e.toHandlerGson();
        }
    }
}
