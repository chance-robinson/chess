package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData gameData;
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType type, GameData gameData) {
        super(type);
        this.gameData = gameData;
        this.game = gameData.game();
    }

    public GameData getGameData() {
        return gameData;
    }

    public ChessGame getGame() {
        return game;
    }
}
