package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            String username = getUsername(command.getAuthToken());

            connections.add(Integer.toString(command.getGameID()), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leave(session, username, (LeaveCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + e.getMessage())));
        }
    }

    private void resign(Session session, String username, ResignCommand command) {
    }

    private void leave(Session session, String username, LeaveCommand command) {
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
    }

    private void connect(Session session, String username, ConnectCommand command) {
    }

    private String getUsername(String authToken) {
        return "";
    };
}