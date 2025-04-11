package handler;

import com.google.gson.Gson;
import server.ServerException;
import server.net.result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the ListGames request/result on the gameService.listGames method
 */
public class ListGamesHandler implements Route {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService)  {
        this.gameService = gameService;
    }

    /**
     * Handles the return of listing games for a user from the Spark server
     *
     * @param req JSON formatted HTTP request with authToken in header
     * @param res JSON formatted HTTP response object
     * @return JSON formatted ListGamesResult object
     * @throws ServerException on errors
     */
    @Override
    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");

            ListGamesResult listGamesResult = gameService.listGames(authToken);

            res.status(200);
            return new Gson().toJson(listGamesResult);
        } catch (ServerException e) {
            res.status(e.getStatusCode());
            return e.toHandlerGson();
        }
    }
}
