package websocket.commands;

import chess.ChessMove;

/**
 * The class for the MakeMoveCommand for whenever a user wants to make a move in their game.
 */
public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    /**
     * The constructor for the LeaveCommand.
     *
     * @param commandType the commandType i.e. MAKE_MOVE
     * @param authToken the authToken for the user
     * @param gameID the gameID to connect to
     * @param move the ChessMove that is being made
     */
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    /**
     * Returns the move of the player.
     *
     * @return the given ChessMove
     */
    public ChessMove getMove() {
        return move;
    }
}
