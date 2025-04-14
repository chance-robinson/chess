package websocket.commands;

/**
 * The class for the ConnectCommand for whenever a user wants to leave a game and disconnect
 * from the websocket.
 */
public class LeaveCommand extends UserGameCommand {
    private final String playerColor;

    /**
     * The constructor for the LeaveCommand.
     *
     * @param commandType the commandType i.e. LEAVE
     * @param authToken the authToken for the user
     * @param gameID the gameID to connect to
     * @param playerColor the playerColor to join on
     */
    public LeaveCommand(CommandType commandType, String authToken, Integer gameID, String playerColor) {
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
