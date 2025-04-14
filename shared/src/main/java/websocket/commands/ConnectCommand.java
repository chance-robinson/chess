package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final String playerColor;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String playerColor) {
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
