package websocket.messages;

/**
 * The class for deciding the ErrorMessage on the ServerMessage class
 * whenever an error has occurred.
 */
public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    /**
     * The constructor for the ErrorMessage.
     *
     * @param type the type of message being thrown i.e. ERROR
     * @param errorMessage the error that has happened
     */
    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the error message.
     *
     * @return the errorMessage string
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
