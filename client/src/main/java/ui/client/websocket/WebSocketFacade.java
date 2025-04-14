package ui.client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
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
                            notificationHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                            break;
                        case NOTIFICATION:
                            notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                            break;
                        case ERROR:
                            notificationHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                            break;
                        default:
                            notificationHandler.notify(notification);
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
}
