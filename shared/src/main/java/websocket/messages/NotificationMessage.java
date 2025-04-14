package websocket.messages;

/**
 * The class for deciding the Notification on the ServerMessage class
 * should be setup.
 */
public class NotificationMessage extends ServerMessage {
    private final String message;

    /**
     * The constructor for the NotificationMessage.
     *
     * @param type the type of message being thrown i.e. NOTIFICATION
     * @param notificationMessage the notification to assign and then return
     */
    public NotificationMessage(ServerMessageType type, String notificationMessage) {
        super(type);
        this.message = notificationMessage;
    }

    /**
     * Returns the notification message.
     *
     * @return the notificationMessage string
     */
    public String getNotificationMessage() {
        return message;
    }
}
