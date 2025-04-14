package websocket.commands;

/**
 * The class for the ConnectCommand for whenever a user has connected to the websocket.
 */
public class ConnectCommand extends UserGameCommand {
    private final String playerColor;

    /**
     * The constructor for the ConnectCommand.
     *
     * @param commandType the commandType i.e. CONNECT
     * @param authToken the authToken for the user
     * @param gameID the gameID to connect to
     * @param playerColor the playerColor to join on
     */
    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, String playerColor) {
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
    }


    /**
     * Returns the player color
     *
     * @return string of the player color.
     */
    public String getPlayerColor() {
        return playerColor;
    }
}
