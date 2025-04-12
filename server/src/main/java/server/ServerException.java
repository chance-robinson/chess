package server;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic Exception specifically for Server messages and status codes
 */
public class ServerException extends RuntimeException {
    private final int statusCode;

    /**
     * Constructor for a ServerException with a specific message
     * and default statusCode (500).
     *
     * @param message a description of the error
     */
    public ServerException(String message) {
        super(message);
        this.statusCode = 500;
    }

    /**
     * Constructor for a ServerException with a specific message
     * and specific statusCode.
     *
     * @param message a description of the error
     * @param statusCode the HTTP status code
     */
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

    /**
     * Converts the exception to a Json string to use for writing on a
     * response body.
     *
     * @return a Json string of the serverException
     */
    public String toHandlerGson() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", getMessage());
        response.put("status", getStatusCode());

        return new Gson().toJson(response);
    }
}
