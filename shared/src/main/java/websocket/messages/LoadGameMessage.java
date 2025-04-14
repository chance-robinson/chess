package websocket.messages;

import chess.ChessGame;
import model.GameData;

/**
 * The class for deciding that a game is to be Loaded on the ServerMessage class.
 */
public class LoadGameMessage extends ServerMessage {
    private final GameData gameData;
    private final ChessGame game;

    /**
     * The constructor for the LoadGameMessage.
     *
     * @param type the type of message being thrown i.e. LOAD_GAME
     * @param gameData the gameData to return after being assigned
     */
    public LoadGameMessage(ServerMessageType type, GameData gameData) {
        super(type);
        this.gameData = gameData;
        this.game = gameData.game();
    }

    /**
     * Returns the gameData in its entirety.
     *
     * @return the gameData
     */
    public GameData getGameData() {
        return gameData;
    }

    /**
     * Returns just the ChessGame
     *
     * @return the chessGame
     */
    public ChessGame getGame() {
        return game;
    }
}
