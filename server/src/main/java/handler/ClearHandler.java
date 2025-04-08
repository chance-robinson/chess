package handler;

import server.ServerException;
import service.GeneralService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final GeneralService generalService;

    public ClearHandler(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerException {
        try {
            generalService.clear();
            res.status(200);
            return "{}";
        } catch (Exception e) {
            throw new ServerException("Error: clear failed");
        }
    }
}
