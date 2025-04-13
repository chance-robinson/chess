package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String notificationMessage;

    public NotificationMessage(ServerMessageType type, String notificationMessage) {
        super(type);
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationMessage() {
        return String.format(notificationMessage);
    }
}
