package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(ServerMessageType type, String notificationMessage) {
        super(type);
        this.message = notificationMessage;
    }

    public String getMessage() {
        return String.format(message);
    }
}
