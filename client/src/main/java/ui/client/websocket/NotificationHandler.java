package ui.client.websocket;

import websocket.messages.ServerMessage;

import java.io.IOException;

public interface NotificationHandler {
    void notify(ServerMessage notification) throws IOException;
}