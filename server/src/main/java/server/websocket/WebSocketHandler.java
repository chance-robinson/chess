package server.websocket;

import com.google.gson.Gson;
import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
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
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> {
                    LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
                    leave(session, username, leaveCommand, gameData);
                }
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + e.getMessage())));
        }
    }

    private void resign(Session session, String username, ResignCommand command) {
    }

    private void leave(Session session, String username, LeaveCommand command, GameData gameData) throws IOException {
        String playerColor = command.getPlayerColor();
        if (Objects.equals(playerColor, "WHITE") && Objects.equals(username, gameData.whiteUsername())) {
            GameData newGameData = new GameData(
                    gameData.gameID(),
                    null,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
            gameDAO.update(newGameData);
        } else if (Objects.equals(playerColor, "BLACK") && Objects.equals(username, gameData.blackUsername())) {
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
                String.format(username + " has left as " + command.getPlayerColor())),
                gameData.gameID());
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
    }

    private void connect(String username, GameData gameData, ConnectCommand command) throws IOException {
        connections.broadcast(username,
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format(username + " has connected as " + command.getPlayerColor())),
                gameData.gameID());
    }

    private String getUsername(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);
        return authData.username();
    }
}