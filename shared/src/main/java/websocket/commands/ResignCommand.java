package websocket.commands;

/**
 * The class for the ResignCommand for whenever a user decides to resign from a game.
 */
public class ResignCommand extends UserGameCommand {
    /**
     * The constructor for the ResignCommand.
     *
     * @param commandType the commandType i.e. RESIGN
     * @param authToken the authToken for the user
     * @param gameID the gameID to connect to
     */
    public ResignCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
