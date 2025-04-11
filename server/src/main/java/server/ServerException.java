package server;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic Exception specifically for Server messages and status codes
 */
public class ServerException extends RuntimeException {
    private final int statusCode;

    public ServerException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public ServerException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Returns the statusCode for a given ServerException
     *
     * @return an int statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    public String toHandlerGson() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", getMessage());
        response.put("status", getStatusCode());

        return new Gson().toJson(response);
    }
}
