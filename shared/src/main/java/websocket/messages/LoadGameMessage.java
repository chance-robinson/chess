package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame chessGame;

    public LoadGameMessage(ServerMessageType type, ChessGame chessGame) {
        super(type);
        this.chessGame = chessGame;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
}
