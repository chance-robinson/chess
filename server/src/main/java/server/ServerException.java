package server;

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

    public int getStatusCode() {
        return statusCode;
    }
}
