package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            String username = getUsername(command.getAuthToken());
            if (username == null) {
                throw new RuntimeException("Error: no user found");
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                throw new RuntimeException("Error: no game data");
            }

            connections.add(username, session, command.getGameID());

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                    connect(username, gameData, connectCommand);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(username, makeMoveCommand, gameData);
                }
                case LEAVE -> {
                    LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
                    leave(username, leaveCommand, gameData);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                    resign(username, resignCommand, gameData);
                }
            }
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + e.getMessage())));
        }
    }

    private void makeMove(String username, MakeMoveCommand command, GameData gameData) throws IOException, InvalidMoveException {
        if (!gameData.game().isActiveGame()) {
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Game has already finished.")));
            return;
        }
        boolean isPlayerWhite = Objects.equals(gameData.whiteUsername(), username);
        boolean isPlayerBlack = Objects.equals(gameData.blackUsername(), username);

        if (!isPlayerBlack && !isPlayerWhite) {
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Only players can make moves.")));
            return;
        }

        ChessGame.TeamColor currentTeamTurn = gameData.game().getTeamTurn();
        if ((isPlayerWhite && currentTeamTurn != ChessGame.TeamColor.WHITE) ||
                (isPlayerBlack && currentTeamTurn != ChessGame.TeamColor.BLACK)) {
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Not your turn.")));
            return;
        }

        boolean validMove = validChessMove(gameData, command, currentTeamTurn);
        if (validMove) {
            gameData.game().makeMove(command.getMove());
            currentTeamTurn = gameData.game().getTeamTurn();
            gameDAO.update(gameData);
            connections.broadcast(username,
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            String.format(
                                    username + " made move " + moveString(command.getMove().getStartPosition())
                                    + "->" + moveString(command.getMove().getEndPosition())
                            )),
                    command.getGameID());
            if (gameData.game().isInCheck(currentTeamTurn) &&
                    !gameData.game().isInStalemate(currentTeamTurn) &&
                            !gameData.game().isInCheckmate(currentTeamTurn)
                ) {
                connections.broadcast(username,
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                String.format(currentTeamTurn + " is in check")),
                        command.getGameID());
                connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                        new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                String.format(
                                        username + " made move " + moveString(command.getMove().getStartPosition())
                                                + "->" + moveString(command.getMove().getEndPosition())
                                ))));
            } else if (gameData.game().isInCheckmate(currentTeamTurn)) {
                gameData.game().setActiveGame(false);
                gameDAO.update(gameData);
                connections.broadcast(username,
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                               "Game has ended in stalemate"),
                        command.getGameID());
                connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                        new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                "Game has ended in stalemate")));
            } else if (gameData.game().isInCheckmate(currentTeamTurn)) {
                gameData.game().setActiveGame(false);
                gameDAO.update(gameData);
                connections.broadcast(username,
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                String.format(username + " is now in checkmate")),
                        command.getGameID());
                connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                        new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                username + " is now in checkmate")));
            }
            connections.broadcast(username,
                    new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData),
                    command.getGameID());
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                            gameData)));
        } else {
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Invalid move.")));
        }
    }

    private String moveString(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        char[] files = {'a','b','c','d','e','f','g','h'};

        char file = files[col - 1];
        return file + String.valueOf(row);
    }

    private boolean validChessMove(GameData gameData, MakeMoveCommand command, ChessGame.TeamColor currentTeamTurn) {
        ChessPosition startPosition = command.getMove().getStartPosition();
        ChessPosition endPosition = command.getMove().getEndPosition();
        ChessGame chessGame = gameData.game();
        ChessBoard chessBoard = chessGame.getBoard();
        ChessPiece chessPiece = chessBoard.getPiece(startPosition);

        if (chessBoard.getPiece(startPosition) == null || currentTeamTurn != chessPiece.getTeamColor()) {
            return false;
        }
        Collection<ChessMove> validMoves = chessGame.validMoves(startPosition);
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().equals(endPosition)) {
                return true;
            }
        }
        return false;
    }

    private void resign(String username, ResignCommand command, GameData gameData) throws IOException {
        if (!gameData.game().isActiveGame()) {
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "Game has already finished.")));
            return;
        }
        if (Objects.equals(username, gameData.whiteUsername()) || Objects.equals(username, gameData.blackUsername())) {
            gameData.game().setActiveGame(false);
            gameDAO.update(gameData);
            connections.broadcast(username,
                    new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format(username + " has resigned")),
                    command.getGameID());
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "You have successfully resigned the chess game.")));
        } else {
            connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                    new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                            "You need to be a player to resign.")));
        }
    }

    private void leave(String username, LeaveCommand command, GameData gameData) throws IOException {
        String playerColor = command.getPlayerColor();
        if (Objects.equals(username, gameData.whiteUsername())) {
            GameData newGameData = new GameData(
                    gameData.gameID(),
                    null,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
            gameDAO.update(newGameData);
        } else if (Objects.equals(username, gameData.blackUsername())) {
            GameData newGameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game()
            );
            gameDAO.update(newGameData);
        }

        connections.remove(username, gameData.gameID());

        connections.broadcast(username,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format(username + " has left as " + playerColor)),
                gameData.gameID());
    }

    private void connect(String username, GameData gameData, ConnectCommand command) throws IOException {
        connections.broadcast(username,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format(username + " has connected as " + command.getPlayerColor())),
                gameData.gameID());
        connections.connectionByGameIdUsername(gameData.gameID(), username).send(
                new Gson().toJson(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData))
        );
    }

    private String getUsername(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);
        return authData.username();
    }
}