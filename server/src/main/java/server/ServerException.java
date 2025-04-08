package server;

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
}
