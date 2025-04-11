package handler;

import server.ServerException;
import service.GeneralService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark handler for the Clear request/result on the generalService.clear method
 */
public class ClearHandler implements Route {
    private final GeneralService generalService;

    public ClearHandler(GeneralService generalService) {
        this.generalService = generalService;
    }

    /**
     * Handles the clearing of all DAO data from the spark Server
     *
     * @param req JSON formatted HTTP request object
     * @param res JSON formatted HTTP response object
     * @return empty response indicating success
     * @throws ServerException on errors
     */
    @Override
    public Object handle(Request req, Response res) {
        try {
            generalService.clear();
            res.status(200);
            return "{}";
        } catch (ServerException e) {
            return e.toHandlerGson();
        }
    }
}
