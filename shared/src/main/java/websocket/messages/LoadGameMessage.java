package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    private final String loadGameMessage;

    public LoadGameMessage(ServerMessageType type, String loadGameMessage) {
        super(type);
        this.loadGameMessage = loadGameMessage;
    }

    public String getLoadGameMessage() {
        return String.format(loadGameMessage);
    }
}
