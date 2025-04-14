package ui.client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);

                    switch (notification.getServerMessageType()) {
                        case LOAD_GAME:
                            try {
                                notificationHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case NOTIFICATION:
                            try {
                                notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case ERROR:
                            try {
                                notificationHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        default:
                            try {
                                notificationHandler.notify(notification);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }


    public void connect(String authToken, int gameID, String playerColor) throws IOException {
        ConnectCommand command = new ConnectCommand(UserGameCommand.CommandType.CONNECT,
                authToken, gameID, playerColor.toUpperCase());
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void leave(String authToken, int gameID, String playerColor) throws IOException {
        LeaveCommand command = new LeaveCommand(UserGameCommand.CommandType.LEAVE,
                authToken, gameID, playerColor.toUpperCase());
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void resign(String authToken, int gameID) throws IOException {
        ResignCommand command = new ResignCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void makeMove(String authToken, int gameID, ChessMove chessMove) throws IOException {
        MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,
                authToken, gameID, chessMove);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}
